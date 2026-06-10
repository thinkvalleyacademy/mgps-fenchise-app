#!/usr/bin/env bash
set -euo pipefail

# Standalone deployment script for the MGPS public website.
# Run from /home/mgps01/mgpsv2/dev/mgps-public
#
# Example:
#   cd /home/mgps01/mgpsv2/dev/mgps-public
#   ./deploy.sh

SOURCE_DIR="${SOURCE_DIR:-/home/mgps01/mgpsv2/code/mgpsfren/mgps-fenchise-app/mgps-public-frontend}"
DEPLOY_DIR="${DEPLOY_DIR:-$(cd "$(dirname "$0")" && pwd)}"

if [[ ! -d "${SOURCE_DIR}" ]]; then
  echo "Source directory not found: ${SOURCE_DIR}" >&2
  exit 1
fi

cd "${SOURCE_DIR}"

echo "[1/4] Pulling latest public website code from ${SOURCE_DIR}"
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  git pull --ff-only
else
  echo "Git repo not found in ${SOURCE_DIR}; skipping pull." >&2
fi

cd "${DEPLOY_DIR}"

echo "[2/4] Rebuilding public website container"
export CODE_DIR="${SOURCE_DIR}"
export DEPLOY_DIR="${DEPLOY_DIR}"

docker compose down --remove-orphans || true

echo "[3/4] Starting public website"
docker compose up -d --build

echo "[4/4] Deployment finished."
docker compose ps
