#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   cd /home/mgps01/mgpsv2/prod    # or .../dev for a second environment
#   ./deploy.sh
#
# This script:
#   1) pulls the latest code from /home/mgps01/mgpsv2/code/mgpsfren/mgps-fenchise-app
#   2) rebuilds the Docker stack
#   3) starts the services in the background

SOURCE_DIR="${SOURCE_DIR:-/home/mgps01/mgpsv2/code/mgpsfren/mgps-fenchise-app}"
DEPLOY_DIR="${DEPLOY_DIR:-$(cd "$(dirname "$0")" && pwd)}"
GIT_PULL_TIMEOUT="${GIT_PULL_TIMEOUT:-60}"

if [[ ! -d "${SOURCE_DIR}" ]]; then
  echo "Source code directory not found: ${SOURCE_DIR}" >&2
  exit 1
fi

if [[ ! -f "${DEPLOY_DIR}/docker-compose.yml" ]]; then
  echo "Deployment compose file not found in: ${DEPLOY_DIR}" >&2
  exit 1
fi

if [[ ! -f "${DEPLOY_DIR}/.env" ]]; then
  echo "No .env found in ${DEPLOY_DIR} — copy .env.example to .env and fill it in first." >&2
  exit 1
fi

cd "${SOURCE_DIR}"

echo "[1/4] Pulling latest code from ${SOURCE_DIR}"
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  # GIT_TERMINAL_PROMPT=0 makes git fail immediately with an error instead of
  # hanging forever on a credential prompt when run non-interactively (e.g.
  # an expired/missing SSH key or HTTPS credential helper). The `timeout`
  # wrapper is a second line of defense for a plain network hang.
  if ! GIT_TERMINAL_PROMPT=0 timeout "${GIT_PULL_TIMEOUT}" git pull --ff-only; then
    echo "git pull failed or timed out after ${GIT_PULL_TIMEOUT}s." >&2
    echo "Common causes: no cached credentials for a non-interactive shell," >&2
    echo "an expired/missing SSH key, or the local branch has diverged from" >&2
    echo "origin (--ff-only refuses to merge/rebase automatically)." >&2
    echo "Fix manually with 'cd ${SOURCE_DIR} && git pull', then re-run this script." >&2
    exit 1
  fi
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
