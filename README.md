# stu-mgmt-sys

A Student Management System that allows schools to efficiently manage student admissions, enrollments, and courses.

## Tech stack

- Java 21
- Spring Boot 3.5.x (Web, Data JPA, Security)
- PostgreSQL
- JWT (jjwt) for admin authentication
- Maven (wrapper included)

## Prerequisites

- **JDK 21** (the project targets Java 21 — JDK 17 will fail to compile with `release version 21 not supported`)
- **Docker** (for the PostgreSQL database), or a local PostgreSQL instance
- No global Maven needed — use the bundled wrapper (`mvnw` / `mvnw.cmd`)

## 1. Start the database

A `docker-compose.yml` is provided that runs PostgreSQL 16:

```bash
docker compose up -d
```

This starts Postgres on `localhost:5432` with:

| Setting  | Value          |
|----------|----------------|
| Database | `studentmgmt`  |
| User     | `postgres`     |
| Password | `postgres_dev` |

## 2. Configure the application

`src/main/resources/application.properties` ships with JWT and admin-login settings.
You must also provide the datasource connection. Add the following (values match the
Docker Compose database above):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/studentmgmt
spring.datasource.username=postgres
spring.datasource.password=postgres_dev
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Sensitive/overridable settings (all have dev defaults) can be supplied via environment variables:

| Variable         | Default   | Purpose                                   |
|------------------|-----------|-------------------------------------------|
| `JWT_SECRET`     | dev value | HMAC signing key (must be ≥ 32 bytes)     |
| `ADMIN_USERNAME` | `admin`   | Admin login username                      |
| `ADMIN_PASSWORD` | `admin`   | Admin login password                      |

## 3. Build, run, and test

The Maven module lives in the `mgmt/` directory. Run the wrapper from there:

```bash
cd mgmt

# Run the application (http://localhost:8080)
./mvnw spring-boot:run

# Build a runnable jar
./mvnw clean package

# Run the test suite
./mvnw test
```

On Windows use `mvnw.cmd` instead of `./mvnw`. If your default JDK is not 21,
point `JAVA_HOME` at a JDK 21 first, e.g.:

```bash
# Windows (PowerShell)
$env:JAVA_HOME="C:\path\to\jdk-21"; .\mvnw.cmd spring-boot:run
```

## Authentication

Two roles, two mechanisms:

### Admin — JWT
1. `POST /api/auth/admin/login` with `{ "username": "admin", "password": "admin" }` → returns a JWT.
2. Send it on admin calls: `Authorization: Bearer <token>`.
3. `/api/admin/**` requires role `ADMIN`.

### Student — request headers
Send the student's unique code and date of birth as headers on every call to `/api/students/**`:

```
X-Student-Code: STU-1
X-Student-Dob:  2000-01-01
```

The headers are validated against the `students` table. A student may only access
their **own** `{studentId}` — accessing another student's id returns `403`.

### Response codes
| Situation                                  | Status |
|--------------------------------------------|--------|
| Missing / invalid credentials              | `401`  |
| Authenticated but wrong role / not owner   | `403`  |

## API endpoints

### Auth
| Method | Path                     | Description        |
|--------|--------------------------|--------------------|
| POST   | `/api/auth/admin/login`  | Admin login → JWT  |

### Admin (`ROLE_ADMIN`)
| Method | Path                                                       | Description                |
|--------|------------------------------------------------------------|----------------------------|
| POST   | `/api/admin/student`                                       | Create a student           |
| GET    | `/api/admin/student/{id}`                                  | Get a student by id        |
| GET    | `/api/admin/student/find?name=`                            | Find students by name      |
| POST   | `/api/admin/courses`                                       | Create a course            |
| GET    | `/api/admin/courses`                                       | List all courses           |
| POST   | `/api/admin/student/{studentId}/course/{courseId}/enroll`  | Enroll a student           |

### Student (`ROLE_STUDENT`, header auth, own id only)
| Method | Path                                            | Description                  |
|--------|-------------------------------------------------|------------------------------|
| PUT    | `/api/students/{id}`                            | Update own profile           |
| GET    | `/api/students/{studentId}/courses`             | List own enrolled courses    |
| DELETE | `/api/students/{studentId}/course/{courseId}`   | Unenroll from a course       |

## Testing with Postman

An importable collection lives in [`postman/admin-login.postman_collection.json`](postman/admin-login.postman_collection.json).
Import it in Postman, then run **Admin Login - success** (it auto-saves the token to the
`adminToken` collection variable for reuse on admin requests). Set the `baseUrl` variable
if the app is not on `http://localhost:8080`.

## Project structure

```
mgmt/src/main/java/com/student/mgmtsys
├── config        # Spring Security, JWT + student-header auth filters
├── controller    # REST controllers
├── dto           # request/response objects
├── entity        # JPA entities
├── exception     # custom exceptions + global handler
├── mapper        # entity <-> dto mapping
├── repository    # Spring Data JPA repositories
└── service       # service interfaces + impl/ implementations
```
