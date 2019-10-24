#!/usr/bin/env bash
# Must be run as a user with postgresql permissions

export TZ=":America/Los_Angeles"
TIMESTAMP=`date "+%Y%m%d-%H%M%S%Z"`

pg_dump registration > ${TIMESTAMP}-registration.sql
pg_dump registrationtraining > ${TIMESTAMP}-registrationtraining.sql

tar -czvf ${TIMESTAMP}-database.tgz ${TIMESTAMP}-registration.sql ${TIMESTAMP}-registrationtraining.sql
tar -czvf ${TIMESTAMP}-data.tgz /opt/registration/data/

rm ${TIMESTAMP}-registration.sql ${TIMESTAMP}-registrationtraining.sql
