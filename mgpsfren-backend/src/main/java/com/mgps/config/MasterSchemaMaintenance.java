package com.mgps.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Keeps small master-schema fixes applied when master Flyway is disabled.
 */
@Component
public class MasterSchemaMaintenance {

    private static final Logger log = LoggerFactory.getLogger(MasterSchemaMaintenance.class);

    private final JdbcTemplate masterJdbcTemplate;

    public MasterSchemaMaintenance(@Qualifier("masterDataSource") DataSource masterDataSource) {
        this.masterJdbcTemplate = new JdbcTemplate(masterDataSource);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applyMasterSchemaFixes() {
        masterJdbcTemplate.execute("ALTER TABLE IF EXISTS schools ALTER COLUMN logo_url TYPE TEXT");
        log.info("Verified master schema supports long school logo values");

        // Encrypted values (base64(iv + ciphertext)) are longer than the plain
        // text they replace — see EncryptedStringConverter.
        masterJdbcTemplate.execute("ALTER TABLE IF EXISTS schools ALTER COLUMN admin_phone TYPE TEXT");
        masterJdbcTemplate.execute("ALTER TABLE IF EXISTS app_users ALTER COLUMN phone TYPE TEXT");
        log.info("Verified master schema supports encrypted contact-detail values");
    }
}
