#!/bin/sh
set -e  # Oprește scriptul la prima eroare

minio server /data --console-address ":9001" &

until mc alias list | grep -q myminio; do
  echo "Aștept initializarea Minio..."
  sleep 1
  mc alias set myminio http://localhost:9000 admin admin123 --insecure
done

mc mb myminio/public-bucket --insecure
mc policy set public myminio/public-bucket --insecure

fg %1