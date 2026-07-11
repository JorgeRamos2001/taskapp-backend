# Plan de Acción - TaskApp Backend

## Estado Actual
- **Stack**: Java 21, Spring Boot 4.1.0, PostgreSQL, Flyway, Spring Data JPA, Spring Security + JWT, Lombok
- **Lo que está bien**: Dominio completo (entidades, enums, repositorios, servicios, DTOs), Seguridad JWT, Manejo de excepciones global, Validaciones, Migraciones Flyway, Perfiles dev/test/prod
- **Lo que falta**: **Controladores REST** (no existe ningún controller)

## Problemas Detectados (Refactoring Necesario)

| # | Problema | Severidad |
|---|----------|-----------|
| 1 | Arquitectura **package-by-layer** en lugar de **package-by-feature** | 🔴 Crítica |
| 2 | Entidades con `@Setter/@Getter` públicos (no encapsuladas) | 🔴 Crítica |
| 3 | `User implements UserDetails` incompleto (faltan 4 métodos) | 🔴 Crítica |
| 4 | `PersonalTask` importa enums equivocados (`BoardTaskPriority/State`) | 🔴 Crítica (Bug) |
| 5 | **Problema N+1** en `BoardTaskServiceImpl.convertToResponse()` | 🟡 Alta |
| 6 | Sin tests de integración (Testcontainers + RestTestClient) | 🟡 Alta |
| 7 | Sin validación de arquitectura (ArchUnit/Taikai) | 🟢 Media |
| 8 | Sin Docker Compose para desarrollo local | 🟢 Media |
| 9 | Sin documentación OpenAPI/Swagger UI | 🟢 Media |

---

## FASE 1: Refactoring Arquitectónico (Base Sólida)

### 1.1 Reorganizar a Package-by-Feature
```
com.taskapp/
├── TaskAppBackendApplication.java
├── config/                          # SecurityConfig, GlobalExceptionHandler, OpenAPIConfig
├── shared/                          # ExceptionResponse, exceptions base, mappers comunes
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java / AuthServiceImpl.java
│   ├── dto/request/ (LoginRequest, RegisterRequest, RefreshTokenRequest, LogoutRequest)
│   ├── dto/response/ (AuthResponse, UserResponse)
│   ├── entity/ (User, RefreshToken)
│   ├── repository/ (UserRepository, RefreshTokenRepository)
│   └── security/ (JwtService, JwtAuthFilter, UserDetailsServiceImpl, JwtEntryPoint, JwtAccessDenied)
├── board/
│   ├── BoardController.java
│   ├── BoardService.java / BoardServiceImpl.java
│   ├── dto/request/ (BoardRequest, AddBoardMember, RemoveBoardMember)
│   ├── dto/response/ (BoardResponse, BoardMemberResponse)
│   ├── entity/ (Board, BoardMember, BoardMemberRole)
│   └── repository/ (BoardRepository, BoardMemberRepository)
├── board-task/
│   ├── BoardTaskController.java
│   ├── BoardTaskService.java / BoardTaskServiceImpl.java
│   ├── dto/request/ (BoardTaskRequest, UpdateBoardTaskRequest, AssignBoardTaskRequest)
│   ├── dto/response/ (BoardTaskResponse)
│   ├── entity/ (BoardTask, BoardTaskState, BoardTaskPriority)
│   └── repository/ (BoardTaskRepository)
├── sub-task/
│   ├── SubTaskController.java
│   ├── SubTaskService.java / SubTaskServiceImpl.java
│   ├── dto/request/ (SubTaskRequest)
│   ├── dto/response/ (SubTaskResponse)
│   ├── entity/ (SubTask)
│   └── repository/ (SubTaskRepository)
├── comment/
│   ├── CommentController.java
│   ├── CommentService.java / CommentServiceImpl.java
│   ├── dto/request/ (CommentRequest)
│   ├── dto/response/ (CommentResponse)
│   ├── entity/ (Comment)
│   └── repository/ (CommentRepository)
├── personal-task/
│   ├── PersonalTaskController.java
│   ├── PersonalTaskService.java / PersonalTaskServiceImpl.java
│   ├── dto/request/ (PersonalTaskRequest, UpdatePersonalTaskRequest)
│   ├── dto/response/ (PersonalTaskResponse)
│   ├── entity/ (PersonalTask, PersonalTaskState, PersonalTaskPriority)
│   └── repository/ (PersonalTaskRepository)
└── user/
    ├── UserController.java
    ├── UserService.java / UserServiceImpl.java
    ├── dto/request/ (ChangePasswordRequest)
    ├── dto/response/ (UserResponse)
    ├── entity/ (User, UserProvider)
    └── repository/ (UserRepository)
```

### 1.2 Encapsular Entidades
- Quitar `@Setter/@Getter` públicos
- Usar **package-private** + `@Builder` + constructor package-private
- Solo exponer getters necesarios

### 1.3 Completar `User implements UserDetails`
```java
@Override public boolean isAccountNonExpired() { return true; }
@Override public boolean isAccountNonLocked() { return true; }
@Override public boolean isCredentialsNonExpired() { return true; }
@Override public boolean isEnabled() { return true; }
```

### 1.4 Fix Bug en `PersonalTask`
```java
// ANTES (incorrecto)
import com.taskapp.entity.enums.BoardTaskPriority;
import com.taskapp.entity.enums.BoardTaskState;

// DESPUÉS (correcto)
import com.taskapp.entity.enums.PersonalTaskPriority;
import com.taskapp.entity.enums.PersonalTaskState;
```

### 1.5 Optimizar N+1 en BoardTaskRepository
```java
@EntityGraph(attributePaths = {"assignee", "subTasks", "comments", "comments.user"})
List<BoardTask> findByBoardId(UUID boardId);

@EntityGraph(attributePaths = {"assignee", "subTasks", "comments", "comments.user"})
List<BoardTask> findByAssigneeId(UUID assigneeId);
```

### 1.6 Docker Compose para Desarrollo
- Agregar dependencia `spring-boot-docker-compose` (runtime)
- Crear `compose.yml`:
  ```yaml
  services:
    postgres:
      image: 'postgres:16-alpine'
      environment:
        POSTGRES_USER: postgres
        POSTGRES_PASSWORD: postgres
        POSTGRES_DB: taskapp_db
      ports:
        - '5433:5432'  # Puerto 5433 en host para no colisionar con BD local
  ```

### 1.7 OpenAPI / Swagger UI
- Agregar `springdoc-openapi-starter-webmvc-ui`
- Configurar `OpenAPIConfig` con info del proyecto

---

## FASE 2: Controladores REST

| Controlador | Endpoints Principales |
|-------------|----------------------|
| `AuthController` | `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`, `POST /api/v1/auth/logout` |
| `BoardController` | `POST /api/v1/boards`, `GET /api/v1/boards`, `GET /api/v1/boards/{id}`, `PUT /api/v1/boards/{id}`, `DELETE /api/v1/boards/{id}`, `POST /api/v1/boards/{id}/members`, `DELETE /api/v1/boards/{id}/members/{userId}` |
| `BoardTaskController` | `POST /api/v1/boards/{boardId}/tasks`, `GET /api/v1/boards/{boardId}/tasks`, `GET /api/v1/tasks/{id}`, `PUT /api/v1/tasks/{id}`, `DELETE /api/v1/tasks/{id}`, `POST /api/v1/tasks/{id}/assign`, `DELETE /api/v1/tasks/{id}/assign` |
| `SubTaskController` | `POST /api/v1/tasks/{taskId}/subtasks`, `PUT /api/v1/subtasks/{id}/complete`, `DELETE /api/v1/subtasks/{id}` |
| `CommentController` | `POST /api/v1/tasks/{taskId}/comments`, `DELETE /api/v1/tasks/{taskId}/comments/{id}` |
| `PersonalTaskController` | `POST /api/v1/personal-tasks`, `GET /api/v1/personal-tasks`, `GET /api/v1/personal-tasks/{id}`, `PUT /api/v1/personal-tasks/{id}`, `DELETE /api/v1/personal-tasks/{id}` |
| `UserController` | `PUT /api/v1/users/password`, `GET /api/v1/users/me` |

**Convenciones:**
- `ResponseEntity<?>`
- Package-private (sin `public` en la clase)
- Validación en DTOs (`@Valid`)
- Delegación completa a Service

---

## FASE 3: Testing & Calidad

### 3.1 Dependencias Testcontainers 2.x
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

### 3.2 Base de Tests de Integración
- `TestcontainersConfig` con `@ServiceConnection` PostgreSQLContainer
- `BaseIT` abstracta con `RestTestClient` + helper `getAuthToken()`

### 3.3 Tests por Controlador
- Happy path (200/201)
- Validación (400 con `ExceptionResponse` mapa de campos)
- Auth (401), Forbidden (403)
- Not Found (404)

### 3.4 ArchUnit + Taikai
```xml
<dependency>
    <groupId>com.enofex</groupId>
    <artifactId>taikai</artifactId>
    <version>1.60.0</version>
    <scope>test</scope>
</dependency>
```
- `ArchUnitTests.shouldFulfillConstraints()` validando:
  - No `@Autowired` en campos
  - Controladores: `@RestController`, terminan en `Controller`, package-private
  - Servicios: `@Service`, terminan en `Service`, no dependen de controllers
  - Repositorios: terminan en `Repository`, no dependen de services
  - Sin ciclos de imports

---

## FASE 4: CI/CD y Pulido

- Verificar/Mejorar GitHub Actions workflow (`.github/workflows/ci.yml`)
- Swagger UI accesible en `/swagger-ui.html`
- Documentación README completa

---

## Orden de Ejecución Confirmado

1. **FASE 1** completa (refactor arquitectura + fixes + Docker + OpenAPI)
2. **FASE 2** completa (todos los controladores)
3. **FASE 3** completa (tests + ArchUnit)
4. **FASE 4** (CI/CD + README final)

---

*Generado automáticamente - TaskApp Backend Action Plan*