#!/usr/bin/env bash
# Load .env into current shell
if [ ! -f .env ]; then
  echo ".env not found"
  return 1
fi
set -a
# shellcheck disable=SC1090
. ./.env
set +a
echo "Loaded .env into shell (exported)"
