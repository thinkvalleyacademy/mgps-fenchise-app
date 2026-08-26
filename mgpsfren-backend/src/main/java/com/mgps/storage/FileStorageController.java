package com.mgps.storage;

import com.mgps.common.dto.ApiResponse;
import com.mgps.tenant.TenantGuard;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;
    private final TenantGuard tenantGuard;

    public FileStorageController(FileStorageService fileStorageService, TenantGuard tenantGuard) {
        this.fileStorageService = fileStorageService;
        this.tenantGuard = tenantGuard;
    }

    /**
     * For any future upload UI (e.g. the student-document flow, which today only
     * accepts a pre-existing fileUrl string) to obtain a real stored reference.
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<?>> upload(@RequestParam UUID schoolId,
                                                 @RequestParam String category,
                                                 @RequestParam("file") MultipartFile file) {
        tenantGuard.assertSchoolAccessible(schoolId);
        String reference = fileStorageService.store(schoolId, category, file);
        return ResponseEntity.ok(ApiResponse.success(Map.of("reference", reference), "File uploaded successfully"));
    }

    /**
     * Deliberately not tenant-guarded: this is called from plain {@code <img src>}
     * tags (e.g. the school logo in the topbar), which can't attach an
     * Authorization header, so it has no authenticated principal to check
     * against. Protection today is the server-generated, unguessable UUID
     * filename plus only non-sensitive assets (logos) being served through it.
     * If a genuinely confidential file type (e.g. student documents) is ever
     * wired up to this endpoint, switch its access to an authenticated
     * fetch-and-blob-URL flow (or a short-lived signed URL) instead of leaving
     * it public.
     */
    @GetMapping("/{schoolId}/{category}/{filename}")
    public ResponseEntity<Resource> download(@PathVariable UUID schoolId,
                                             @PathVariable String category,
                                             @PathVariable String filename) {
        Resource resource = fileStorageService.resolve(schoolId, category, filename);
        String contentType = fileStorageService.probeContentType(schoolId, category, filename);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CACHE_CONTROL, "private, max-age=86400")
            .body(resource);
    }
}
