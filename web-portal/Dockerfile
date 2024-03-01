# Use node:20 to build the app:
FROM node:20.11.1-slim AS build-env

WORKDIR /app

RUN apt-get update && apt-get install -y jq
RUN npm install express axios
RUN cp -ar node_modules/ run-env/

COPY package*.json ./
RUN echo $(jq -r .version package.json) > ./version


RUN npm ci

# Start with the build of the application
COPY . .

RUN npm run build


# Use nodejs20-debian12 to run the app:
FROM gcr.io/distroless/nodejs20-debian12

WORKDIR /app

# Copy the built application to the distroless image
COPY --from=build-env /app/dist ./dist
# Copy the express modules for being able to run the express server on distroless image
COPY --from=build-env /app/run-env/ ./dist/node_modules

COPY --from=build-env /app/version ./version

EXPOSE 3000

CMD ["dist/server.cjs"]

#docker buildx create --use
#docker buildx build --platform linux/amd64,linux/arm64 -t cameronward/cs-web-portal:latest --push .
