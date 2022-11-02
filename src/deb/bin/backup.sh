#!/usr/bin/env bash

# Backs up the current registration files and database, saves to
# YYYYMMDD-HHMMSSPDT-registration.tgz in the current directory

set -e
export TZ=":America/Los_Angeles"
TIMESTAMP=$(date "+%Y%m%d-%H%M%S%Z")
TARGET=./regbackup
WORKDIR=/tmp/
pushd "${WORKDIR}"
mkdir "${TARGET}"

sudo -u postgres pg_dump registration | tee "${TARGET}/registration.sql" > /dev/null
cp -r /opt/registration/data/ "${TARGET}/data/"
cp /opt/registration/*.pem "${TARGET}/" || :   # If file doesn't exist, continue
# The .pem files are optional, this script should keep working if they don't exist

tar -czf "${TIMESTAMP}-registration.tgz" "${TARGET}/"

rm -r "${TARGET:?}/"
popd
mv "${WORKDIR}/${TIMESTAMP}-registration.tgz" .
echo "Saved ${TIMESTAMP}-registration.tgz"
ls -lh "${TIMESTAMP}-registration.tgz"
