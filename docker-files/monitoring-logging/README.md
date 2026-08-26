# Monitoring & Logging

Grafana + Prometheus for metrics, Graylog for centralized logs, for the
`mgps-fenchise-app` stack in [../application/production](../application/production).

Runs as its own compose project, alongside — not inside — the application
stack:
- **Metrics** are pulled over HTTP from the backend's already-exposed
  `/api/actuator/prometheus` endpoint every 15s. No app code or config
  changes, no measurable load.
- **Logs** are read from the host directory the app stack bind-mounts its
  log files to (`APP_LOG_DIR`, default `/var/log/mgps`) — Filebeat tails
  those files read-only and ships new lines to Graylog. The app keeps
  writing its log file exactly as before; this stack never touches the
  running containers.

## What's included

| Purpose | Service |
|---|---|
| Metrics collection | `prometheus` |
| Host metrics (CPU/mem/disk) | `node-exporter` |
| Per-container metrics | `cadvisor` |
| Dashboards | `grafana` (pre-provisioned with a Prometheus datasource + a "MGPS Backend Overview" dashboard) |
| Log storage/indexing | `opensearch`, `mongodb` (Graylog's backing stores) |
| Log search UI | `graylog` |
| Log shipping | `filebeat` (tails the host log files, ships to Graylog) |

## One-time host setup

1. **Log directory.** Both this stack and the application stack read/write
   `APP_LOG_DIR` (default `/var/log/mgps`) on the host. Create it and make
   sure the app stack's containers can write to it:
   ```bash
   sudo mkdir -p /var/log/mgps/backend /var/log/mgps/nginx
   sudo chmod -R 777 /var/log/mgps   # or chown to the container's uid if you prefer tighter perms
   ```
   Set the same `APP_LOG_DIR` value in **both** `.env` files (this one and
   `../application/production/.env`) if you're not using the default.

2. **OpenSearch needs a higher `vm.max_map_count`** (a standard OpenSearch/
   Elasticsearch requirement), or it will fail to start:
   ```bash
   sudo sysctl -w vm.max_map_count=262144
   echo 'vm.max_map_count=262144' | sudo tee -a /etc/sysctl.conf
   ```

3. **The app stack must already be running** and joined to the external
   `mgps-shared` network (it creates it) — Prometheus attaches to that same
   network to reach `backend:8080`.

## Generating Graylog secrets

Graylog refuses to start without these two values in `.env`:

```bash
# GRAYLOG_PASSWORD_SECRET — at least 16 random characters
openssl rand -hex 48

# GRAYLOG_ROOT_PASSWORD_SHA2 — SHA-256 of the admin password you want to log in with
echo -n 'your-chosen-admin-password' | sha256sum | awk '{print $1}'
```
Put the first value in `GRAYLOG_PASSWORD_SECRET` and the second in
`GRAYLOG_ROOT_PASSWORD_SHA2`. You'll log into Graylog as `admin` /
`your-chosen-admin-password` — keep that plaintext password somewhere safe,
you'll also need it for the step below.

## Steps

1. Copy `.env.example` to `.env` and fill in the values above.
2. Start everything:
   ```bash
   docker compose up -d
   ```
3. Wait for Graylog to report healthy (`docker compose ps`, can take a
   minute or two on first boot while OpenSearch initializes), then create
   the input Filebeat ships to:
   ```bash
   cd graylog
   ./init-inputs.sh 'your-chosen-admin-password' http://localhost:9000
   ```
   This is idempotent — safe to re-run, and only needs to be done once ever
   (the input is stored in MongoDB and survives restarts).
4. Restart Filebeat so it reconnects now that the input exists:
   ```bash
   docker compose restart filebeat
   ```

## Access

- Grafana: `http://YOUR_SERVER_IP:3000` (login: `GRAFANA_ADMIN_USER` / `GRAFANA_ADMIN_PASSWORD`)
- Graylog: `http://YOUR_SERVER_IP:9000` (login: `admin` / the password you hashed above)

Prometheus, MongoDB, OpenSearch, and the Beats/GELF ports are intentionally
**not** published to the host — only Grafana and Graylog's web UI are, same
"don't expose more than the two things people actually need to open"
approach as the app stack's own nginx proxy.

## Useful commands

```bash
# Start only the monitoring half
docker compose up -d grafana prometheus node-exporter cadvisor

# Start only the logging half
docker compose up -d graylog mongodb opensearch filebeat

# Tail Filebeat's own logs if events aren't showing up in Graylog
docker compose logs -f filebeat

# Stop everything (keeps volumes — dashboards, Graylog config, indexed logs)
docker compose down

# Stop and wipe all stored data (Grafana dashboards you made in the UI,
# Graylog's indexed logs, Prometheus's history)
docker compose down -v
```

## Notes / things to revisit later

- **Retention**: OpenSearch will keep every indexed log line forever unless
  you configure an index rotation/retention policy in Graylog (System >
  Indices). Worth setting once you know your log volume.
- **Resource usage**: OpenSearch + Graylog + MongoDB is the heaviest part of
  this stack (OpenSearch alone wants ~1GB+ heap, set via
  `OPENSEARCH_JAVA_OPTS`). Size the server accordingly, or drop the logging
  half and keep only Grafana/Prometheus if that's all you need right now.
- **Postgres metrics**: not included here. The app's Postgres runs in a
  separate compose stack ([../postgres](../postgres)) — adding
  `postgres-exporter` there is a natural follow-up if you want DB-level
  dashboards too.
