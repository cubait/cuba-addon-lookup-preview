#!/usr/bin/env bash

case "$1" in
    "start") docker run -p 33066:3306 --name mysql-lupreview -e 'MYSQL_USER=cuba' -e 'MYSQL_PASSWORD=cuba' -e 'MYSQL_DATABASE=lupreview' -e 'MYSQL_RANDOM_ROOT_PASSWORD=yes' -d mariadb:10.0 ;;
     "stop") docker stop mysql-lupreview && docker rm mysql-lupreview ;;
          *) echo Unknown command: $1 ;;
esac