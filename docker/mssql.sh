#!/usr/bin/env bash

[[ ! -d ./docker/mssql/initdb.d ]] && echo ERR: Run this at the project root! && exit 1

case "$1" in
    "start") docker run -p 1433:1433 --name mssql-lupreview -e 'ACCEPT_EULA=Y' -e 'MSSQL_PID=Express' -e 'SA_PASSWORD=saPass.123' -v $PWD/docker/mssql/initdb.d:/docker-entrypoint-initdb.d -d nexbit/mssql-server-linux ;;
     "stop") docker stop mssql-lupreview && docker rm mssql-lupreview ;;
          *) echo Unknown command: $1 ;;
esac