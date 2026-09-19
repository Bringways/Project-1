# devops-practice-app

A small, realistic **Employee Management REST API** built with Java and Spring Boot,
designed as a hands-on playground for practicing DevOps fundamentals:
Java, Maven, Git, Docker, and (later) CI/CD with Jenkins and AWS deployment.

## 1. Project Overview

This is a Spring Boot REST API with five endpoints for managing employees
(Create, Read, Update, Delete), backed by an in-memory H2 database. It's
deliberately simple in its *business logic* so you can focus on the
*tooling* around it: how Maven builds it, how Docker packages it, and how
you'd eventually wire it into a CI/CD pipeline.

## 2. Architecture

```
HTTP Request
     │
     ▼
EmployeeController   (controller/)  -- handles HTTP, maps URLs to methods
     │
     ▼
EmployeeService       (service/)     -- business logic, orchestrates work
     │
     ▼
EmployeeRepository    (repository/)  -- talks to the database (Spring Data JPA)
     │
     ▼
H2 Database (in-memory)
```

Supporting pieces:
- `model/Employee.java` — the data shape, shared across all three layers above.
- `exception/` — turns error conditions into clean HTTP responses (404, 400)
  instead of raw stack traces.

## 3. Prerequisites

You need three things installed on your machine:
- **Java 17** (JDK, not just JRE — you need the compiler too)
- **Maven 3.9+**
- **Docker** (Desktop on Mac/Windows, or Docker Engine on Linux)

## 4. Installing Java 17

**macOS (Homebrew):**
```bash
brew install openjdk@17
```

**Ubuntu/Debian Linux:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Windows:** Download the installer from
[Adoptium Temurin 17](https://adoptium.net/temurin/releases/?version=17).

Verify it worked:
```bash
java -version
# should print something like: openjdk version "17.x.x"
```

## 5. Installing Maven

**macOS (Homebrew):**
```bash
brew install maven
```

**Ubuntu/Debian Linux:**
```bash
sudo apt install maven
```

**Windows:** Download from [maven.apache.org](https://maven.apache.org/download.cgi)
and add the `bin` folder to your `PATH`.

Verify it worked:
```bash
mvn -version
# should show Maven version and confirm it's using Java 17
```

## 6. Cloning the Project

If this project lives in a Git repository:
```bash
git clone <your-repository-url>
cd devops-practice-app
```

If you're starting from these generated files, just `cd` into the project
folder and run `git init` to start tracking it yourself:
```bash
cd devops-practice-app
git init
git add .
git commit -m "Initial commit: devops-practice-app"
```

## 7. Building with Maven

```bash
mvn clean package
```

What each word does:
- `clean` — deletes the `target/` folder from any previous build, so you're
  never accidentally running stale code.
- `package` — compiles your code, runs tests, and bundles everything into
  a single runnable JAR at `target/devops-practice-app.jar`.

## 8. Running Locally

```bash
java -jar target/devops-practice-app.jar
```

The app starts on port `8080`. You should see Spring Boot's startup banner
in the terminal, ending with a line like `Started DevopsPracticeApplication`.

Alternatively, while developing, you can skip the packaging step entirely:
```bash
mvn spring-boot:run
```

## 9. Running Tests

```bash
mvn test
```

This runs two kinds of tests:
- **Unit tests** (`EmployeeServiceTest`) — test business logic in isolation
  using mocked dependencies (fast, no database).
- **Integration tests** (`EmployeeControllerIntegrationTest`) — boot the
  full application and make real HTTP calls against the real H2 database,
  proving the whole CRUD flow works end to end.

## 10. Creating the Docker Image

Make sure you've built the JAR first isn't actually required — the
Dockerfile builds the JAR *inside* the image using a Maven-based build
stage. You just need Docker running:

```bash
docker build -t devops-practice-app:1.0 .
```

This uses the multi-stage `Dockerfile`:
1. **Build stage** — uses a Maven+JDK image to compile and package the app.
2. **Runtime stage** — copies only the final JAR into a lightweight JRE
   image, so the shipped image doesn't carry the entire Maven toolchain.

## 11. Running the Docker Container

```bash
docker run -d -p 8080:8080 --name devops-practice-app devops-practice-app:1.0
```

- `-d` — run in the background (detached)
- `-p 8080:8080` — map port 8080 on your machine to port 8080 in the container
- `--name` — gives the running container a friendly name to refer to later

Check it's running:
```bash
docker ps
```

## 12. Checking Logs

```bash
docker logs devops-practice-app
```

Add `-f` to keep following the logs live, similar to `tail -f`:
```bash
docker logs -f devops-practice-app
```

## 13. Testing the API with curl

**Health check:**
```bash
curl http://localhost:8080/health
```

**Create an employee:**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","department":"DevOps","salary":75000}'
```

**Get all employees:**
```bash
curl http://localhost:8080/api/employees
```

**Get one employee (replace 1 with a real id from the create response):**
```bash
curl http://localhost:8080/api/employees/1
```

**Update an employee:**
```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Smith","email":"john.smith@example.com","department":"Platform","salary":82000}'
```

**Delete an employee:**
```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

## 14. Stopping and Removing the Container

```bash
docker stop devops-practice-app
docker rm devops-practice-app
```

## 15. Troubleshooting Common Errors

**"Port 8080 is already in use"**
Something else on your machine (maybe a previous run) is using port 8080.
Either stop that process, or run the container on a different host port:
```bash
docker run -d -p 9090:8080 --name devops-practice-app devops-practice-app:1.0
```
Then access the app at `http://localhost:9090` instead.

**`mvn` command not found**
Maven isn't installed or isn't on your `PATH`. Re-check step 5.

**`java.lang.UnsupportedClassVersionError`**
You're running the JAR with an older Java version than it was built with.
Run `java -version` and confirm it reports version 17 or higher.

**Docker build fails downloading dependencies**
Check your internet connection — the build stage needs to download Maven
dependencies the first time. Subsequent builds will be faster because
Docker caches that layer (see the comments in the `Dockerfile`).

**Container exits immediately after `docker run`**
Check the logs: `docker logs devops-practice-app`. A common cause is a
port conflict or a leftover container with the same `--name`. If the name
is taken, remove the old container first: `docker rm devops-practice-app`.

**`curl: (7) Failed to connect to localhost port 8080`**
The container isn't running yet, or it crashed. Run `docker ps` to confirm
it's up, and check `docker logs` if it isn't.

**H2 console shows no tables / "Table EMPLOYEE not found"**
Make sure you're connecting with the exact JDBC URL from
`application.properties`: `jdbc:h2:mem:devopsdb`. The in-memory database is
wiped every time the app restarts, so any data you created before a
restart will be gone.
