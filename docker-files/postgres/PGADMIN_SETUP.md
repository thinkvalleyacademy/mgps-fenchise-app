# pgAdmin Auto-Registration Setup

This directory contains pgAdmin provisioning and configuration files for the MGPS multi-tenant application.

## How It Works

### Auto Server Registration
- The `servers.json` file automatically registers the PostgreSQL server with pgAdmin on startup
- pgAdmin connects to the PostgreSQL server as `postgres` user
- All databases created in PostgreSQL are automatically visible in pgAdmin

### How New Tenant Databases Appear

When your application creates a new tenant database:
1. The database is created in PostgreSQL using the `schools` table reference
2. pgAdmin automatically detects the new database when you:
   - Refresh the server in pgAdmin UI (F5 or click refresh)
   - Navigate to Databases section
   - Perform any query operation

### Directory Structure

- `servers.json` - Server configuration for pgAdmin (auto-loaded at startup)
- `provisioning/` - Directory for additional pgAdmin configurations (if needed)

## Configuration Details

### servers.json Properties
- **Host**: `postgres` (Docker service name)
- **Port**: `5432`
- **Username**: `postgres`
- **Password**: `postgres123`
- **AutoReconnect**: Enabled (`1`)
- **ConnectTimeoutInSeconds**: `10`

## How to Manually Refresh Database List

1. Open pgAdmin at `http://localhost:5050`
2. Login with:
   - Email: `thinkvalleyacademy@gmail.com`
   - Password: `admin123`
3. In the left panel, right-click "MGPS PostgreSQL Server" and select "Refresh"
4. All tenant databases will appear under "Databases"

## Common Tasks

### View Tenant Database
1. Expand "MGPS PostgreSQL Server"
2. Click "Databases"
3. Find and click your tenant database name
4. View schemas, tables, and data

### Query a Tenant Database
1. In pgAdmin, right-click your server
2. Select "Query Tool"
3. Use `\c database_name` to connect to a specific tenant database
4. Run your queries

## Enhanced Configuration (Optional)

If you want pgAdmin to automatically refresh without user interaction, you can:

1. Create a provisioning script in the `provisioning/` directory
2. Configure pgAdmin environment variables for auto-refresh interval
3. Use webhook integrations with your application

## Troubleshooting

**Issue**: New databases not appearing in pgAdmin
- Solution: Click the refresh button next to "MGPS PostgreSQL Server"
- Check that the database was successfully created in PostgreSQL

**Issue**: Cannot connect to server
- Verify PostgreSQL container is running: `docker compose ps`
- Check credentials match in `servers.json`
- Verify network connectivity between containers

**Issue**: Permission denied errors
- Ensure PostgreSQL user has necessary privileges
- Check `init-db.sql` for user creation and grant statements

## Next Steps

When new tenant databases are created by your application:
1. They are automatically created in PostgreSQL
2. They appear in pgAdmin's database list after refresh
3. You can immediately query and manage them through pgAdmin
