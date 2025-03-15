#!/bin/sh

sleep 10

mc alias set myminio http://minio:9000 admin admin123
mc mb myminio/public-bucket

mc policy set public myminio/public-bucket
