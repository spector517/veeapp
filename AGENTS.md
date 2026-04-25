# AGENTS.md

## Project Overview

VeeApp is a web management panel for a VPN server powered by [Xray](https://github.com/xtls/xray-core) / [VeePeeNET](https://github.com/spector517/veepeenet). The backend is a REST wrapper around the `xrayctl` CLI tool (provided by VeePeeNET). The frontend is a Vue SPA for managing VPN clients, outbounds, and routing rules. Deployed as a `.deb` package on Ubuntu.

```
Browser (Vue SPA)  →  Nginx (:8443, TLS + Basic Auth)  →  Spring Boot (:9099, localhost only)  →  sudo xrayctl (CLI)
```

No database — all state lives in Xray's config file (`/usr/local/etc/xray/config.json`), managed by `xrayctl`.

## Modules

| Module | Path | Stack |
|---|---|---|
| Backend | `backend/` | Java 25, Spring Boot 4.1.0-M3, Lombok, Jackson 3.x (`tools.jackson`), SpringDoc OpenAPI |
| Frontend | `frontend/` | Vue 3.5, Vite 8, TypeScript 5.8, Vuetify 3.8 (dark theme), Pinia 3, Vue Router 4 |
| SDK | `sdk/` | Auto-generated TypeScript Axios client from OpenAPI spec |
| Deploy | `deploy/` | `.deb` packaging, Nginx, Fail2ban, systemd, certbot |

## Build & Run

### Backend
```bash
cd backend
mvn clean package                                     # Build JAR
mvn spring-boot:run -Dspring-boot.run.profiles=dev    # Local dev (stub CLI executor)
mvn spring-boot:run                                   # Prod (requires xrayctl installed)
mvn test                                              # Run tests
mvn verify                                            # Full cycle: build + generate openapi.yaml into sdk/
```

### Frontend
```bash
cd frontend
npm install
npm run dev       # Dev server (Vite proxies /api → localhost:9099)
npm run build     # Production build → deploy/resources/frontend/dist/
```

### SDK (auto-generated — do NOT edit manually)
```bash
cd sdk
npm install
npm run generate  # Regenerate from sdk/openapi.yaml (overwrites sdk/src/)
```

Full regeneration cycle: `backend/mvn verify` → generates `sdk/openapi.yaml` → `sdk/npm run generate`.

### Deployment
```bash
cd backend && mvn clean package
cd frontend && npm run build
```

## Backend Conventions

### Package Structure

```
com.github.spector517.veeapp.backend
├── command/             # Command abstraction & execution
│   ├── Command          # Base class (fluent Lombok @Accessors)
│   ├── CommandExecutor  # Interface
│   ├── local/           # LocalCommandExecutor (@Profile("!dev"), ProcessBuilder)
│   └── stub/            # StubCommandExecutor (@Profile("dev"), hardcoded JSON)
├── controller/          # REST controllers (@Tag per API group)
├── dto/shared/          # Request/response records
│   ├── request/
│   └── response/
├── exception/           # GlobalExceptionHandler (@RestControllerAdvice)
└── service/xrayctl/     # XrayCtlService + XrayCtlCommand builder
```

### Key Patterns

- **DTOs are Java records** with `Optional<T>` for optional fields. Enums use fluent accessors to map to CLI string values.
- **Single service layer**: `XrayCtlService` builds CLI commands via `XrayCtlCommand` → executes through `CommandExecutor`.
- **Dev profile** (`dev`): `StubCommandExecutor` returns hardcoded JSON responses. **Always use `-Dspring-boot.run.profiles=dev` for local development** — without it, the app tries to run `sudo xrayctl`.
- **No database**: Spring Boot explicitly excludes DataSource/Hibernate/Flyway auto-configuration.
- Controllers use `@RequiredArgsConstructor` with `final` field injection (Lombok).
- **Error handling**: `GlobalExceptionHandler` catches `CommandExecutionException`, `XrayCtlExecutionException`, validation errors, and generic exceptions — returns `ErrorResponse` with timestamp, status, message, and path.
- Virtual threads enabled (`spring.threads.virtual.enabled: true`).
- **Spring Boot 4.1.0-M3** (milestone) uses Jackson 3.x — imports are `tools.jackson.*`, not `com.fasterxml.jackson.*`.
- OpenAPI spec is generated during `mvn verify` (integration-test phase) via `springdoc-openapi-maven-plugin`.
- Config: `application.yaml` with `app.xray.config-path` and `app.command.timeout-ms` (default 60s).

## Frontend Conventions

- **Composition API** (`<script setup lang="ts">`) exclusively.
- SDK is imported via path alias `veeapp-sdk` → `../sdk/src` (not an npm package).
- Path alias `@/` → `./src/`.
- **All UI strings are in Russian** (hardcoded, no i18n).
- Pinia store (`stores/xray.ts`) uses a single `exec(fn, successMsg?, key?, showOverlay?)` helper that wraps loading state, error handling, sets `restartRequired`, and calls `refreshAll()` after mutations. All mutation methods delegate to `exec()`.
- `restartRequired` flag — after any mutation, the UI shows an "Применить" (Apply) button to restart Xray.
- Vuetify dark theme: primary = `#FF9800` (orange), secondary = `#42A5F5` (blue), background = `#1E1E1E`, surface = `#2A2A2A`.
- `__APP_VERSION__` is injected by Vite from `package.json` version.
- 4 routes: `/service` (default), `/clients`, `/routing`, `/outbounds`.
- Notification store (`stores/notification.ts`) — auto-dismiss after 5 seconds; used by all error/success flows.

## SDK Conventions

- Generated by `openapi-generator-cli` 7.20.0 with `typescript-axios` template.
- **Never edit files in `sdk/src/` manually** — they are overwritten on regeneration.
- 5 API groups: Service, Clients, Config, Outbounds, Routing (all under `/api/xray/`).

## API Structure

| Group | Base Path | Key Operations |
|---|---|---|
| Service | `/api/xray/service` | GET `/status`, POST `/start`, `/stop`, `/restart` |
| Clients | `/api/xray/clients` | GET (list), POST (add), DELETE (remove) |
| Config | `/api/xray` | GET/POST `/config`, PUT `/update-geodata`, GET `/update-xray/versions`, PUT `/update-xray` |
| Outbounds | `/api/xray/outbounds` | POST (add), POST `/from-url`, PATCH/DELETE `/{name}` |
| Routing | `/api/xray/routing` | GET (list), PUT `/domain-strategy`, POST/DELETE/PATCH `/rules/...` |

## Deployment Architecture

- Backend listens on `127.0.0.1:9099` only — no direct external access.
- Nginx handles TLS termination (Let's Encrypt, port 8443) and Basic Auth (`.htpasswd`).
- Fail2ban protects against brute-force (5 attempts / 10 min → 30 min ban; repeat → 7 day ban).
- `.deb` package depends on: `nginx`, `fail2ban`, `openjdk-25-jre-headless`, `python3-venv`, `apache2-utils`, `openssl`, `veepeenet (>= 2.4.1)`.

## CI/CD

- **Release pipeline** (`deploy/release.jenkinsfile`): builds JAR, frontend, assembles `.deb`, creates GitHub release. Agent requirements: `git`, `mvn`, `npm`, `dpkg-deb`.
- **Deploy pipeline** (`deploy/deploy-playbook.yml` + `deploy/deploy.jenkinsfile`): Ansible-based deployment to remote hosts.
- Deployment requires `/etc/default/veeapp/init.conf` with `IP_ADDRESS` variable on the target host before `.deb` install. Credentials are auto-generated on first install.

## Pitfalls

1. **Dev profile is required locally** — without `profiles=dev`, backend calls `sudo xrayctl` via ProcessBuilder.
2. **SDK is auto-generated** — manual edits to `sdk/src/` will be lost.
3. **Version 0.1.0** is hardcoded in `pom.xml`, both `package.json` files, and `DEBIAN/control` — update all when bumping.
4. **Spring Boot milestone** — 4.1.0-M3 is not a stable release; Jackson 3.x uses `tools.jackson.*` package.
5. **No tests beyond context loads** — there are no unit/integration tests for business logic.
6. **Adding a new API endpoint** requires: backend controller + DTO → `mvn verify` → `npm run generate` (SDK) → frontend integration. All three layers must stay in sync.

## Related Project

VeePeeNET (`xrayctl` CLI) is the upstream dependency. See [VeePeeNET AGENTS.md](https://raw.githubusercontent.com/spector517/veepeenet/refs/heads/main/AGENTS.md) for its conventions, model patterns, and command structure.
