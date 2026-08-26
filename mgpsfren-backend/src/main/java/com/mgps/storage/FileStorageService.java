package com.mgps.storage;

import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Stores uploaded files on the local filesystem under one dedicated root,
 * partitioned per school then per category ({uploadDir}/{schoolId}/{category}/{uuid}.{ext}),
 * so a school's files are trivially locatable/exportable/deletable as a unit.
 * Filenames are always server-generated — never trust a client-supplied name.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final String REFERENCE_PREFIX = "/files/";

    private final Path rootDir;
    private final long maxFileSizeBytes;
    private final Set<String> allowedExtensions;

    public FileStorageService(@Value("${app.file.upload-dir}") String uploadDir,
                               @Value("${app.file.max-file-size}") long maxFileSizeBytes,
                               @Value("${app.file.allowed-extensions}") String allowedExtensionsCsv) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.allowedExtensions = Arrays.stream(allowedExtensionsCsv.split(","))
            .map(String::trim)
            .map(String::toLowerCase)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
        try {
            Files.createDirectories(rootDir);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create file storage root: " + rootDir, ex);
        }
    }

    public static boolean isDataUrl(String value) {
        return value != null && value.startsWith("data:");
    }

    public static boolean isStoredReference(String value) {
        return value != null && value.startsWith(REFERENCE_PREFIX);
    }

    public String store(UUID schoolId, String category, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessLogicException("File is required");
        }
        if (file.getSize() > maxFileSizeBytes) {
            throw new BusinessLogicException("File exceeds the maximum allowed size");
        }
        String extension = extensionOf(file.getOriginalFilename());
        assertExtensionAllowed(extension);
        try (InputStream in = file.getInputStream()) {
            return writeToDisk(schoolId, category, extension, in);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file", ex);
        }
    }

    /**
     * Decodes a {@code data:<mime>;base64,<payload>} string and writes it to disk,
     * returning a stable {@code /files/...} reference. Used to migrate the school
     * logo flow off base64-in-the-database without any frontend change.
     */
    public String storeDataUrl(UUID schoolId, String category, String dataUrl) {
        if (!isDataUrl(dataUrl)) {
            throw new IllegalArgumentException("Not a data URL");
        }
        int comma = dataUrl.indexOf(',');
        if (comma < 0) {
            throw new IllegalArgumentException("Malformed data URL");
        }
        String mime = dataUrl.substring(5, comma).split(";")[0];
        String extension = extensionForMime(mime);
        byte[] bytes = Base64.getDecoder().decode(dataUrl.substring(comma + 1));
        if (bytes.length > maxFileSizeBytes) {
            throw new BusinessLogicException("File exceeds the maximum allowed size");
        }
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            return writeToDisk(schoolId, category, extension, in);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store file", ex);
        }
    }

    /**
     * Reverse of {@link #storeDataUrl}: reads a stored file back as a base64 data
     * URL. Used so consumers that need actual image bytes (e.g. jsPDF's
     * {@code addImage}) don't need to know files moved off the database.
     */
    public String readAsDataUrl(String reference) {
        Path path = resolveReference(reference);
        try {
            byte[] bytes = Files.readAllBytes(path);
            String mime = Files.probeContentType(path);
            if (mime == null) {
                mime = "application/octet-stream";
            }
            return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read stored file: " + reference, ex);
        }
    }

    public Resource resolve(UUID schoolId, String category, String filename) {
        Path path = fileFor(schoolId, category, filename);
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File not found");
        }
        return new FileSystemResource(path);
    }

    public String probeContentType(UUID schoolId, String category, String filename) {
        try {
            String mime = Files.probeContentType(fileFor(schoolId, category, filename));
            return mime != null ? mime : "application/octet-stream";
        } catch (IOException ex) {
            return "application/octet-stream";
        }
    }

    private String writeToDisk(UUID schoolId, String category, String extension, InputStream in) throws IOException {
        String safeCategory = sanitizeCategory(category);
        String filename = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path dir = rootDir.resolve(schoolId.toString()).resolve(safeCategory);
        Files.createDirectories(dir);
        Path target = dir.resolve(filename);
        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        log.info("Stored file for school {} in category {}: {}", schoolId, safeCategory, filename);
        return REFERENCE_PREFIX + schoolId + "/" + safeCategory + "/" + filename;
    }

    private Path resolveReference(String reference) {
        String trimmed = isStoredReference(reference) ? reference.substring(REFERENCE_PREFIX.length()) : reference;
        String[] parts = trimmed.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid file reference: " + reference);
        }
        return fileFor(UUID.fromString(parts[0]), parts[1], parts[2]);
    }

    private Path fileFor(UUID schoolId, String category, String filename) {
        if (filename == null || filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            throw new IllegalArgumentException("Invalid filename");
        }
        return rootDir.resolve(schoolId.toString()).resolve(sanitizeCategory(category)).resolve(filename);
    }

    private String sanitizeCategory(String category) {
        String cleaned = category == null ? "misc" : category.replaceAll("[^a-zA-Z0-9_-]", "");
        return cleaned.isEmpty() ? "misc" : cleaned;
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        return dot < 0 ? "" : originalFilename.substring(dot + 1).toLowerCase();
    }

    private void assertExtensionAllowed(String extension) {
        if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
            throw new BusinessLogicException("File type not allowed: " + extension);
        }
    }

    private String extensionForMime(String mime) {
        return switch (mime) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "application/pdf" -> "pdf";
            default -> "bin";
        };
    }
}
