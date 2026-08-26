# Production deployment

This folder is `~/mgpsv2/prod` on the server. It runs the backend, frontend,
and an nginx reverse proxy for one environment (see "Running dev alongside
prod on the same server" below if you're standing up a second one).

## First-time setup on the server

1. **Start the shared Postgres stack** if it isn't already running — it's a
   separate compose project so multiple environments can share one Postgres
   instance:
   ```bash
   cd ~/mgpsv2/.../docker-files/postgres   # wherever that checkout lives
   docker network create mgps-shared       # one-time, only if it doesn't exist yet
   docker compose up -d
   ```
2. **Copy this folder's env template and fill it in:**
   ```bash
   cd /home/mgps01/mgpsv2/prod
   cp .env.example .env
   ```
   Required values — the backend now refuses to start without these two:
   ```bash
   # Generate real values, don't ship the placeholders:
   openssl rand -base64 32   # -> JWT_SECRET
   openssl rand -base64 32   # -> ENCRYPTION_KEY
   ```
   Put those into `JWT_SECRET` and `ENCRYPTION_KEY` in `.env`. Set
   `APP_CORS_ALLOWED_ORIGINS` to the real domain(s) the frontend is served
   from. Leave `APP_NETWORK_NAME` / `DB_NETWORK_NAME` at their defaults
   unless you're running a second environment (see below).
3. **Create the log directory** the app and nginx will write to (matches
   `APP_LOG_DIR` in `.env`, default `/var/log/mgps`):
   ```bash
   sudo mkdir -p /var/log/mgps/backend /var/log/mgps/nginx
   sudo chmod -R 777 /var/log/mgps
   ```
4. **First deploy:**
   ```bash
   ./deploy.sh
   ```

## Day-to-day deploys (after every commit to main)

```bash
cd /home/mgps01/mgpsv2/prod
./deploy.sh
```

That's the whole workflow: `deploy.sh` pulls the latest commit into
`CODE_DIR` (the separate checkout the containers build from — see
`.env`'s `CODE_DIR`), then rebuilds and restarts backend/frontend/nginx-proxy.
It does **not** touch Postgres or the monitoring-logging stack — those are
independent compose projects, started separately and left running.

If `git pull` fails or hangs (e.g. no cached credentials in a
non-interactive shell), `deploy.sh` now fails within `GIT_PULL_TIMEOUT`
seconds (default 60) with a clear error instead of hanging forever — see
"Troubleshooting" below.

## What changed recently (read this before your next deploy)

- **Two new required `.env` values**: `JWT_SECRET` and `ENCRYPTION_KEY`. The
  backend fails fast on startup if either is blank — this is deliberate
  (the old fallback values were guessable placeholders checked into git).
  If your `.env` predates this, `deploy.sh` will bring the backend
  container up crash-looping until you add them.
- **The backend's port (8081) is no longer published to the host.** It's
  only reachable through nginx-proxy now (`http://SERVER:${PROXY_PORT}/api/`).
  If you had scripts/bookmarks hitting `:8081` directly, switch them to go
  through the proxy.
- **`APP_LOG_DIR`** (default `/var/log/mgps`) is now bind-mounted into the
  backend and nginx-proxy containers, so logs are readable directly on the
  server filesystem (`/var/log/mgps/backend/mgps.log`,
  `/var/log/mgps/nginx/access.log`) instead of living only inside the
  containers. Create this directory before your first deploy after
  upgrading (see step 3 above) or the containers will fail to start.
- **Network/container names are now environment-scoped** (`APP_NETWORK_NAME`,
  no more hardcoded `container_name:`), so a second environment (dev) can
  run on this same server without colliding with prod — see below.
- There's now an optional **monitoring-logging stack**
  (`../monitoring-logging`) for Grafana/Prometheus metrics and
  Graylog-based centralized logs — see its own README.

## Running dev alongside prod on the same server

Copy this whole folder to a second directory (e.g. `/home/mgps01/mgpsv2/dev`)
and in its `.env`, change only what needs to differ:

```bash
APP_NETWORK_NAME=mgps-net-dev      # was mgps-net-prod — keeps the two environments' containers apart
PROXY_PORT=6081                    # was 6080 — pick any free host port
APP_LOG_DIR=/var/log/mgps-dev      # separate log directory
DB_NAME=mgps_master_dev            # separate database, same shared Postgres instance
JWT_SECRET=...                     # generate a DIFFERENT secret for dev
ENCRYPTION_KEY=...                 # generate a DIFFERENT key for dev
```

Leave `DB_NETWORK_NAME=mgps-shared` the same in both — both environments
talk to the one Postgres instance from `../postgres`, isolated from each
other purely by `DB_NAME`. `CODE_DIR` can point at the same checkout as
prod, or a separate one if you want dev tracking a different branch.

Everything else (backend/frontend/nginx-proxy service names, the compose
file itself) is identical between the two folders — Docker Compose
namespaces containers and named volumes by the project (folder) name
automatically, and each environment's `APP_NETWORK_NAME` keeps their
internal networking apart. No manual `docker network create` needed for
`app-net` — Compose creates it itself on first `up`.

## Access

- Frontend + API (via nginx-proxy): `http://YOUR_SERVER_IP:${PROXY_PORT}/`
  and `http://YOUR_SERVER_IP:${PROXY_PORT}/api/`
- There is no direct backend access — see "What changed recently" above.

## Troubleshooting

**`git pull` hangs or fails in `deploy.sh`.** Almost always a credential
problem in a non-interactive shell — SSH agent has no key loaded, or the
HTTPS remote has no cached credential helper. Test manually:
```bash
cd /home/mgps01/mgpsv2/code/mgpsfren/mgps-fenchise-app
git pull
```
If that also hangs/prompts, fix the credential (load your SSH key with
`ssh-add`, or switch the remote to use a deploy key / PAT with a credential
helper) before re-running `deploy.sh`. If it fails with a merge/divergence
error instead, someone committed directly on the server checkout at some
point — resolve that manually (`git status`, `git log`) rather than forcing.

**Backend container keeps restarting after a deploy.** Check for the two
new required env vars:
```bash
docker compose logs backend | grep -i "not set"
```
An `IllegalStateException: app.jwt.secret-key is not set` or
`app.encryption.key is not set` means `.env` is missing `JWT_SECRET` /
`ENCRYPTION_KEY` — add them and re-run `./deploy.sh`.

**Logs aren't showing up under `/var/log/mgps`.** Confirm the directory
exists and is writable *before* the containers start (Docker won't create
intermediate host directories with the right permissions on its own for a
bind mount in all cases) — see step 3 above. Then `docker compose ps` /
`docker compose logs backend` to confirm the container is actually up.

## Useful commands

```bash
# Rebuild after code changes (what deploy.sh does)
docker compose up -d --build

# Check status / tail logs from the containers directly
docker compose ps
docker compose logs -f backend frontend nginx-proxy

# Tail the same logs from the host filesystem instead
tail -f /var/log/mgps/backend/mgps.log
tail -f /var/log/mgps/nginx/access.log

# Stop everything (keeps uploaded files / DB — those are volumes/external)
docker compose down

# Stop and also delete this environment's uploaded-files volume
docker compose down -v
```
