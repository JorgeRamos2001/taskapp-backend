# TaskApp Backend

> **API REST para una aplicación tipo Trello simplificada** — Tableros colaborativos, tareas, subtareas, comentarios y tareas personales.

---

## 🚀 Stack Tecnológico

| Capa | Tecnología | Versión |
|------|------------|---------|
| Lenguaje | Java | 21 |
| Framework | Spring Boot | 4.1.x |
| Base de Datos | PostgreSQL | 16+ |
| ORM | Spring Data JPA / Hibernate | 6.x |
| Migraciones | Flyway | 11.x |
| Seguridad | Spring Security + JWT (jjwt) | 6.x / 0.13.x |
| Validación | Bean Validation (Hibernate Validator) | 8.x |
| Testing | Testcontainers + RestTestClient + JUnit 5 | 2.x / 3.x |
| Arquitectura | Package-by-Feature | — |
| Docs API | SpringDoc OpenAPI (Swagger UI) | 2.x |
| Build | Maven | 3.9+ |

---

## ✨ Funcionalidades

### 🔐 Autenticación
- Registro local (email + password + BCrypt)
- Login con JWT (Access Token + Refresh Token)
- Refresh token rotativo con expiración
- Logout (revocación de refresh token)

### 📋 Tableros (Boards)
- CRUD completo de tableros
- Roles de miembro: `OWNER`, `ADMIN`, `MEMBER`
- Invitar/eliminar miembros
- Listar mis tableros y tableros accesibles

### 📝 Tareas de Tablero (Board Tasks)
- CRUD de tareas dentro de un tablero
- Estados: `UNASSIGNED`, `IN_PROGRESS`, `DONE`, `OVERDUE`
- Prioridades: `LOW`, `MEDIUM`, `HIGH`, `URGENT`
- Asignación/desasignación a miembros del tablero
- Fecha de vencimiento (`dueDate`)

### ✅ Subtareas
- Crear subtareas bajo una tarea
- Marcar como completada (con `completedAt` automático)
- Eliminar subtarea

### 💬 Comentarios
- Añadir comentarios a tareas
- Eliminar comentarios propios

### 👤 Tareas Personales
- CRUD independiente (no pertenecen a tableros)
- Estados: `IN_PROGRESS`, `DONE`, `OVERDUE`
- Prioridades: `LOW`, `MEDIUM`, `HIGH`, `URGENT`

### 👤 Usuario
- Cambio de contraseña
- Perfil (avatar, nombre, email)

---

## 🏗️ Arquitectura

```
com.taskapp/
├── TaskAppBackendApplication.java
├── config/                    # Configuración transversal
│   ├── SecurityConfig.java
│   ├── GlobalExceptionHandler.java
│   └── OpenApiConfig.java
├── shared/                    # Utilidades compartidas
├── auth/                      # Autenticación & Seguridad
├── board/                     # Tableros y miembros
├── board-task/                # Tareas de tablero
├── sub-task/                  # Subtareas
├── comment/                   # Comentarios
├── personal-task/             # Tareas personales
└── user/                      # Perfil de usuario
```

**Principios:**
- **Package-by-Feature**: Todo lo relacionado a una funcionalidad vive en su paquete
- **Encapsulamiento**: Entidades package-private, solo `@Builder` y getters necesarios
- **Inyección por constructor**: `@RequiredArgsConstructor` + `final` fields
- **DTOs como `record`**: Validaciones en el constructor del record
- **Transacciones en Service**: `@Transactional` a nivel de caso de uso
- **Excepciones de dominio**: `EntityNotFoundException`, `ValidationException`, `StateTransitionException`, `DuplicateException`, `BusinessRuleViolationException`
- **Manejo global**: `GlobalExceptionHandler` → `ExceptionResponse` (formato custom, **no** RFC 7807)

---

## 📦 Requisitos Previos

- **JDK 21+**
- **Maven 3.9+**
- **Docker** (para PostgreSQL via Testcontainers / Docker Compose)
- **PostgreSQL 16+** (opcional si usas Docker Compose)

---

## ⚙️ Configuración

### Variables de Entorno (`.env` en raíz del proyecto)

```bash
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=taskapp_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=tu-clave-base64-de-al-menos-256-bits
JWT_ACCESS_EXPIRATION_MS=900000          # 15 min
JWT_REFRESH_EXPIRATION_MS=604800000      # 7 días

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173

# Servidor
SERVER_PORT=8080
```

> **Nota**: Spring Boot importa automáticamente `.env` como propiedades (`spring.config.import: optional:file:.env[.properties]`).

### Perfiles
| Perfil | Uso | `ddl-auto` | SQL Logging |
|--------|-----|------------|-------------|
| `dev` (default) | Desarrollo local | `validate` | DEBUG |
| `test` | Tests (Testcontainers) | `validate` | WARN |
| `prod` | Producción | `validate` | WARN (JSON) |

---

## 🐳 Desarrollo Local con Docker Compose

```bash
# Levantar solo PostgreSQL
docker compose up -d postgres

# La app se conecta automáticamente (spring-boot-docker-compose)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Puertos:**
- App: `http://localhost:8080`
- PostgreSQL: `localhost:5433` (mapeado a 5432 del contenedor)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 🧪 Testing

```bash
# Tests unitarios + integración (levanta Testcontainers automáticamente)
./mvnw test

# Solo tests de integración
./mvnw test -Dtest=*IT
```

**Estructura de tests:**
- `BaseIT` → Configura `RestTestClient` + PostgreSQL real + helper `getAuthToken()`
- `*ControllerIT` → Un test class por controlador (happy path + validación + 401/403/404)
- `ArchUnitTests` → Valida reglas arquitectónicas (Taikai)

---

## 📚 API Endpoints (Resumen)

| Módulo | Base Path | Principales Endpoints |
|--------|-----------|----------------------|
| Auth | `/api/v1/auth` | `POST /register`, `POST /login`, `POST /refresh`, `POST /logout` |
| Boards | `/api/v1/boards` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/members`, `DELETE /{id}/members/{userId}` |
| Board Tasks | `/api/v1/boards/{boardId}/tasks`, `/api/v1/tasks/{id}` | `POST`, `GET (list)`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`, `POST /{id}/assign`, `DELETE /{id}/assign` |
| Subtasks | `/api/v1/tasks/{taskId}/subtasks`, `/api/v1/subtasks/{id}` | `POST`, `PUT /{id}/complete`, `DELETE /{id}` |
| Comments | `/api/v1/tasks/{taskId}/comments` | `POST`, `DELETE /{commentId}` |
| Personal Tasks | `/api/v1/personal-tasks` | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` |
| User | `/api/v1/users` | `PUT /password`, `GET /me` |

> **Documentación completa**: Swagger UI en `http://localhost:8080/swagger-ui.html`

---

## 📂 Migraciones de Base de Datos

Flyway gestiona el esquema en `src/main/resources/db/migration/`:

```
V1__init_scheme.sql  -- Esquema completo (tablas, FKs, índices, checks)
```

Ejecución automática al arrancar la app (`spring.jpa.hibernate.ddl-auto=validate`).

---

## 🔒 Seguridad

- **JWT**: HS256, claims `sub` (email), `userId` (UUID)
- **Access Token**: 15 min (configurable)
- **Refresh Token**: 7 días, rotativo, almacenado en BD con hash
- **Password**: BCrypt (cost 12)
- **CORS**: Configurable via `CORS_ALLOWED_ORIGINS`
- **Stateless**: Sin sesiones en servidor

---

## 📝 Convenciones de Commit

[Conventional Commits](https://www.conventionalcommits.org/)

```
<tipo>(<scope opcional>): <descripción corta ≤ 100 chars>

- Detalle 1
- Detalle 2
```

| Tipo | Uso |
|------|-----|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de bug |
| `refactor` | Refactor sin cambio de comportamiento |
| `test` | Tests |
| `docs` | Documentación |
| `chore` | Mantenimiento (deps, build, etc.) |
| `build` | Sistema de build |
| `ci` | CI/CD |

**Ejemplos:**
```
feat(board-task): agregar endpoint para asignar tarea a miembro
fix(auth): corregir expiración de refresh token en logout
refactor(board): encapsular entidad Board y mover a package-by-feature
test(board-controller): agregar test de integración para GET /boards/{id}
```

---

## 📄 Licencia

Proyecto personal — uso libre para aprendizaje y portfolio.

---

## 🤝 Contribuciones

Es un proyecto personal, pero sugerencias y issues son bienvenidos.