#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   cd /home/mgps01/mgpsv2/dev
#   ./deploy.sh
#
# This script:
#   1) pulls the latest code from /home/mgps01/mgpsv2/code/mgpsfren/mgps-fenchise-app
#   2) rebuilds the Docker stack
#   3) starts the services in the background

SOURCE_DIR="${SOURCE_DIR:-/home/mgps01/mgpsv2/code/mgpsfren/mgps-fenchise-app}"
DEPLOY_DIR="${DEPLOY_DIR:-$(cd "$(dirname "$0")" && pwd)}"

if [[ ! -d "${SOURCE_DIR}" ]]; then
  echo "Source code directory not found: ${SOURCE_DIR}" >&2
  exit 1
fi

if [[ ! -f "${DEPLOY_DIR}/docker-compose.yml" ]]; then
  echo "Deployment compose file not found in: ${DEPLOY_DIR}" >&2
  exit 1
fi

cd "${SOURCE_DIR}"

echo "[1/4] Pulling latest code from ${SOURCE_DIR}"
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  git pull --ff-only
else
  echo "Git repository not found in ${SOURCE_DIR}; skipping git pull." >&2
fi

cd "${DEPLOY_DIR}"

echo "[2/4] Rebuilding containers using CODE_DIR=${SOURCE_DIR}"
export CODE_DIR="${SOURCE_DIR}"

docker compose down --remove-orphans || true

echo "[3/4] Starting services"
docker compose up -d --build

echo "[4/4] Deployment finished."
docker compose ps
