FROM node:12.7-alpine AS frontBuild

LABEL maintainer="Tim 29"

WORKDIR /usr/src/frontend
COPY package.json ./
RUN npm install
RUN npm install ng-starrating@1.0.11
COPY . .
RUN npm run build


FROM nginx:1.17.1-alpine
COPY --from=frontBuild /usr/src/frontend/dist/Frontend /usr/share/nginx/html