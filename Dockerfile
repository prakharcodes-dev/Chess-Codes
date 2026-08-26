# Multi-Stage Dockerfile for Java Multiplayer Chess Server

# Stage 1: Compile Java source code
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY backend/src /app/backend/src
RUN javac backend/src/ChessServe.java

# Stage 2: Production JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/backend/src /app/backend/src
COPY frontend /app/frontend

ENV PORT=9090
EXPOSE 9090

CMD ["java", "-cp", "backend/src", "ChessServe"]
