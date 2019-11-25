#!/usr/bin/env bash

case "$1" in
    "start") docker run -p 54322:5432 --name postgres-lupreview -e 'POSTGRES_USER=cuba' -e 'POSTGRES_PASSWORD=cuba' -e 'POSTGRES_DB=lupreview' -d postgres:9-alpine ;;
     "stop") docker stop postgres-lupreview && docker rm postgres-lupreview ;;
          *) echo Unknown command: $1 ;;
esac