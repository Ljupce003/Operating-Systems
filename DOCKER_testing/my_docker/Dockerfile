#FROM debian:11-slim
#RUN apt-get update && apt-get install -y nginx && rm -rf /val/lib/apt/lists/*

FROM node:slim
COPY . /app/
WORKDIR /app
CMD ["node","jas.js"]
