FROM postgres:12 as db
WORKDIR /app
COPY ./scripts/db/init.sh /docker-entrypoint-initdb.d
COPY ./scripts/db/dump.sql ./scripts/db/dump.sql