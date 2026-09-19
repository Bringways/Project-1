# =====================================================================
# STAGE 1: BUILD
# =====================================================================
# We use a full Maven + JDK image here ONLY to compile the code and
# produce the JAR. This image is large (~500MB+) because it contains the
# whole Maven toolchain, but that's fine - it never ships to production.
# It exists only while `docker build` is running.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy ONLY the pom.xml first, not the source code yet.
# Why? Docker caches each instruction as a "layer". If pom.xml hasn't
# changed since the last build, Docker will reuse the cached layer below
# (all the downloaded dependencies) instead of re-downloading everything
# from the internet every single time we change a .java file. This is one
# of the most important Docker performance patterns to learn.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source code and build the JAR.
# -DskipTests is used here because we want unit tests to run explicitly
# via `mvn test` in CI (or by you, locally) as a separate, visible step -
# not silently inside the Docker build where failures are harder to see.
COPY src ./src
RUN mvn clean package -DskipTests -B

# =====================================================================
# STAGE 2: RUNTIME
# =====================================================================
# This is a "JRE" (Java Runtime Environment) image, not a full JDK - it
# has everything needed to RUN Java code but not to compile it. It's
# also based on Alpine Linux, a minimal Linux distribution, so the final
# image is small (~200MB vs ~500MB+ for the build image).
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Run as a non-root user. If an attacker ever compromised the app inside
# the container, they'd be limited to this unprivileged user's
# permissions rather than full root access to the container.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy ONLY the final JAR from Stage 1 into this clean image. None of the
# Maven cache, source code, or build tools come along - that's the whole
# point of a multi-stage build.
COPY --from=build /app/target/devops-practice-app.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

# Documents to humans and to tools like `docker inspect` that this
# container listens on 8080. It does NOT actually publish the port -
# that still requires `-p 8080:8080` on `docker run`.
EXPOSE 8080

# A basic container-level health check. Docker will periodically run
# this command inside the container; `docker ps` will then show the
# container as "healthy" or "unhealthy" accordingly.
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD wget -qO- http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
