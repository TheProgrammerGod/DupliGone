# Stage 1 — Build the React App
FROM node:20-alpine AS build
WORKDIR /app

# Copy only package files first for faster caching
COPY package.json package-lock.json* ./
RUN npm ci

# Copy the full source code and build
COPY . .
RUN npm run build

# Stage 2 — Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom Nginx configuration
COPY nginx.conf etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]