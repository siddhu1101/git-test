# Campus App (Java 21, Spring Boot, PostgreSQL, JWT)

## Run locally

1. Start PostgreSQL (or use Docker):

```bash
docker run --name campusdb -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=campusdb -p 5432:5432 -d postgres:16
```

2. Build and run:

```bash
mvn -DskipTests package
java -jar target/campus-app-0.0.1-SNAPSHOT.jar
```

Environment variables (optional): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXP_MINUTES`.

## Default users
- superadmin / admin123 (must change password on first login)

## Auth flow
- POST `/api/auth/login` with `{ "username", "password" }` -> returns `accessToken` and `mustChangePassword` flag.
- If `mustChangePassword=true`, only `/api/auth/change-password` is allowed until changed.

## RBAC and scoping
- SUPER_ADMIN: full access.
- ADMIN: can create campus users and access data for their campus.
- USER: can access data for their campus.

## Key endpoints
- POST `/api/admins` (SUPER_ADMIN) -> create admin for campus `{ username, password, campusId }`.
- POST `/api/users` (ADMIN) -> create user for own campus `{ username, password }`.
- GET/POST `/api/metrics/campus/{campusId}` -> campus-scoped metrics.
- GET/POST `/api/buildings/campus/{campusId}` -> campus-scoped buildings.
- GET `/api/campuses/{id}` -> campus detail (scoped).