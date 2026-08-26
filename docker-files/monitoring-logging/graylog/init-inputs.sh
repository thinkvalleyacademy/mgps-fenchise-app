#!/usr/bin/env bash
set -euo pipefail

# Creates the Graylog "Beats" input that Filebeat ships logs to, via
# Graylog's REST API, so you don't have to click through System > Inputs
# by hand. Safe to re-run — it checks whether the input already exists first.
#
# Usage (after `docker compose up -d` and Graylog has finished starting):
#   ./init-inputs.sh <graylog-root-password> [graylog-url]
#
# <graylog-root-password> is the PLAINTEXT password matching the
# GRAYLOG_ROOT_PASSWORD_SHA2 hash configured in .env — it's only used here,
# transiently, to authenticate this one API call.

ROOT_PASSWORD="${1:?Usage: $0 <graylog-root-password> [graylog-url]}"
GRAYLOG_URL="${2:-http://localhost:9000}"

echo "Waiting for Graylog API at ${GRAYLOG_URL}..."
for _ in $(seq 1 60); do
  if curl -sf -u "admin:${ROOT_PASSWORD}" "${GRAYLOG_URL}/api/system/lbstatus" >/dev/null 2>&1; then
    break
  fi
  sleep 5
done

existing=$(curl -sf -u "admin:${ROOT_PASSWORD}" -H "X-Requested-By: init-inputs" \
  "${GRAYLOG_URL}/api/system/inputs" | grep -c '"title" : "Beats (Filebeat)"' || true)

if [ "${existing}" -gt 0 ]; then
  echo "Beats input already exists — nothing to do."
  exit 0
fi

echo "Creating Beats input on port 5044..."
curl -sf -u "admin:${ROOT_PASSWORD}" \
  -H "Content-Type: application/json" \
  -H "X-Requested-By: init-inputs" \
  -X POST "${GRAYLOG_URL}/api/system/inputs" \
  -d '{
    "title": "Beats (Filebeat)",
    "type": "org.graylog.plugins.beats.Beats2Input",
    "global": true,
    "configuration": {
      "bind_address": "0.0.0.0",
      "port": 5044,
      "recv_buffer_size": 1048576,
      "tls_enable": false,
      "tcp_keepalive": false
    }
  }' > /dev/null

echo "Beats input created. Filebeat can now ship to graylog:5044."
