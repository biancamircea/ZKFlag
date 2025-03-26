FROM node:18.17.1-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install --legacy-peer-deps

COPY . .

RUN npm run build

FROM nginx:stable-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist /usr/share/nginx/html

RUN mkdir -p /etc/nginx/certs
COPY server.crt /etc/nginx/certs/
COPY server.key /etc/nginx/certs/

EXPOSE 443

CMD ["nginx", "-g", "daemon off;"]