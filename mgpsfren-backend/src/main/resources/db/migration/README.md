# MGPS Backend - Database Migrations with Flyway

This directory contains all database migration scripts for MGPS application.

## Migration Naming Convention

- **V[VERSION]__[DESCRIPTION].sql**
- Example: V1__create_master_schema.sql
- Version numbers: 1, 2, 3... (no gaps)
- Use double underscore between version and description
- Use underscores to replace spaces in description

## Migration Guidelines

1. **Idempotent**: Migrations must be safe to run multiple times
2. **Atomic**: Each migration is a single transaction
3. **Version Control**: Never modify already executed migrations
4. **Testing**: Test migrations locally first

## Files

- V1__create_master_schema.sql - Master database schema (already in init-db.sql)
- V2__[future] - Additional migrations as needed

## Running Migrations

Migrations run automatically on application startup via Flyway.

To verify executed migrations:
```sql
SELECT * FROM flyway_schema_history;
```
