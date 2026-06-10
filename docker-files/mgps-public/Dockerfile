FROM node:20-alpine AS build
WORKDIR /app

COPY mgps-public-frontend/package*.json ./
RUN npm ci --no-audit --no-fund

COPY mgps-public-frontend ./
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY docker-files/mgps-public/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
