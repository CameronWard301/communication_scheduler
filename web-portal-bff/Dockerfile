# Use node:18 to build the app:
FROM node:20.11.1-slim AS build-env

RUN npm install -g typescript@5.3.3 ts-node@10.9.2

WORKDIR /app
COPY package.json package-lock.json ./

RUN npm install --production

COPY . .
RUN npm run swagger-autogen
RUN npm run build


# Use nodejs20-debian12 to run the app:
FROM gcr.io/distroless/nodejs20-debian12

WORKDIR /app

# Copy the built application to the distroless image
COPY --from=build-env /app/build ./build
COPY --from=build-env /app/node_modules ./node_modules

EXPOSE 3000

CMD ["build/app.js"]
#docker buildx create --use
#docker buildx build --platform linux/amd64,linux/arm64 -t cameronward/web-portal-bff-api:latest --push .
