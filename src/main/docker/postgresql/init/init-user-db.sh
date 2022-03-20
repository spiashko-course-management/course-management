#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER course_management WITH PASSWORD 'course_management';
    CREATE DATABASE course_management;
    GRANT ALL PRIVILEGES ON DATABASE course_management TO course_management;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER keycloak WITH PASSWORD 'keycloak';
    CREATE DATABASE keycloak;
    GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
EOSQL
