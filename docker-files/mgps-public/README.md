# MGPS Public Website deployment

This folder is meant to be copied to /home/mgps01/mgpsv2/dev/mgps-public on the Linux server.

## Run on the server

```bash
cd /home/mgps01/mgpsv2/dev/mgps-public
cp .env.example .env
docker compose up -d --build
```

The public website will be available on:
- http://YOUR_SERVER_IP:6079/
