# Production deployment files

These files are ready to copy to your Linux server under /home/mgps01/mgpsv2/dev.

## What is included
- docker-compose.yml
- .env.example
- Dockerfile for backend
- Dockerfile for frontend
- nginx.conf for frontend

## Steps on the server
1. Copy this folder to /home/mgps01/mgpsv2/dev.
2. Rename .env.example to .env and update the values.
3. Run the helper script:
   ```bash
   cd /home/mgps01/mgpsv2/dev
   ./deploy.sh
   ```
   - Replace YOUR_SERVER_IP with the server public IP or hostname.
   - Set a strong JWT_SECRET value.
3. Run:
   ```bash
   cd /home/mgps01/mgpsv2/dev
   docker compose up -d --build
   ```
4. Check status:
   ```bash
   docker compose ps
   docker compose logs -f backend frontend
   ```

## Access
- Frontend (via Nginx reverse proxy): http://YOUR_SERVER_IP/
- Backend API (via the same proxy): http://YOUR_SERVER_IP/api/
- Direct backend container (if needed): http://YOUR_SERVER_IP:8080/api

## Useful commands
```bash
# Rebuild after code changes
docker compose up -d --build

# Stop everything
docker compose down

# Remove volumes (database data will be deleted)
docker compose down -v
```
