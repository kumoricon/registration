#!/usr/bin/env bash
cd /tmp || true   # Avoid permission denied errors if running in a user's directory

echo This script must be run as a user that has access to Postgres

echo Creating registration database
createuser -D -P registration
createdb -O registration registration
psql registration -c "CREATE EXTENSION citext;"
