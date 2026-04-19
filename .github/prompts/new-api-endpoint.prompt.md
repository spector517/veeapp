---
description: "Add a new API endpoint across all three layers: backend → SDK → frontend"
agent: "agent"
argument-hint: "<description of the new endpoint>"
---

# Add New API Endpoint

Implement a new API endpoint across all three layers of VeeApp, keeping them in sync.

## Steps

1. **Backend** (in `backend/src/main/java/com/github/spector517/veeapp/backend/`):
   - Create request/response **Java records** in `dto/shared/request/` and `dto/shared/response/` (use `Optional<T>` for optional fields)
   - Add the endpoint method to the appropriate controller in `controller/` (use `@Tag`, `@Operation` annotations)
   - Add the service method in `service/xrayctl/XrayCtlService.java`
   - Add the CLI command builder method in `service/xrayctl/XrayCtlCommand.java`
   - Add a stub response in `command/stub/StubCommandExecutor.java` for dev profile

2. **SDK regeneration**:
   - Run `cd backend && mvn verify` to generate updated `sdk/openapi.yaml`
   - Run `cd sdk && npm run generate` to regenerate TypeScript client

3. **Frontend** (in `frontend/src/`):
   - Import the new API method in `api/index.ts` (if a new API group was added)
   - Add store method in `stores/xray.ts` using the `exec()` helper pattern
   - Add UI components/views as needed (Composition API, `<script setup lang="ts">`, Russian UI strings)

## Conventions

- Backend: Jackson 3.x (`tools.jackson.*`), Lombok `@RequiredArgsConstructor`, `@Accessors(chain = true, fluent = true)`
- DTOs: Java records, `Optional<T>` for optional fields, `@NotBlank`/`@NotNull` for required
- Frontend: all mutation store methods use `exec(fn, successMsg, key, showOverlay)` which handles loading, errors, `restartRequired`, and `refreshAll()`
- SDK files in `sdk/src/` are auto-generated — never edit manually
