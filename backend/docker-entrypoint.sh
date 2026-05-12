#!/bin/sh

# Render fournit DATABASE_URL au format postgresql://user:pass@host:port/db
# Spring Boot attend une URL jdbc:postgresql://
if [ -n "$DATABASE_URL" ]; then
    # Extraire les composants
    URI="${DATABASE_URL#postgresql://}"
    USERPASS="${URI%%@*}"
    HOSTPORTDB="${URI#*@}"

    USER="${USERPASS%%:*}"
    PASS="${USERPASS#*:}"

    # Construire les vars Spring
    export DATASOURCE_USERNAME="$USER"
    export DATASOURCE_PASSWORD="$PASS"
    export DATASOURCE_URL="jdbc:postgresql://${HOSTPORTDB}?sslmode=require"
fi

exec java -jar app.jar --spring.profiles.active=prod "$@"
