# Lab-3 — Full Stack Integration Challenge

## Purpose

Prueba técnica Full Stack sobre un Merchant Payments Dashboard existente. El objetivo es levantar una aplicación desconocida, seguir peticiones desde el navegador hasta PostgreSQL, usar tests y logs como evidencia, corregir bugs entre capas e implementar una feature que atraviese frontend y backend.

## Difficulty

Intermediate

## Skills trained

React, TypeScript, Java, Spring Boot, REST, PostgreSQL, HTTP, debugging, async, state, transactions, tests e integración.

## Stack

- Backend: Java 21, Maven 3.9.9, Spring Boot 3.5.4, Spring Web, Spring Data JPA, Bean Validation, Flyway, PostgreSQL 17-alpine, JUnit 5, Mockito y Spring Boot Test.
- Frontend: React 19, TypeScript, Vite, Node.js 22 LTS, npm, Vitest, React Testing Library y `@testing-library/user-event`.
- Infraestructura: Docker y Docker Compose.

## Architecture

```text
Browser
   │ HTTP
   ↓
React + TypeScript
   │ fetch
   ↓
Spring Boot REST
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

### Frontend architecture

```text
Page
 ↓
Component / Hook
 ↓
API module
 ↓
HTTP
```

### Backend architecture

```text
Controller
 ↓
Service
 ↓
Repository
 ↓
PostgreSQL
```

## Docker architecture

```text
Host
│
├── localhost:13003
│        ↓
│     frontend
│        │ browser fetch
│        ↓
├── localhost:18083
│        ↓
│     backend
│        ↓
│     postgres
│
└── localhost:15007
         ↓
    backend-debug JDWP

test profile:

backend-test → postgres-test

frontend-test
```

El navegador llama a `http://localhost:18083`, nunca al hostname Docker `backend`.

## Docker services

- `postgres`: PostgreSQL de desarrollo y Flyway.
- `backend`: Spring Boot en modo desarrollo.
- `frontend`: Vite con React y TypeScript.
- `postgres-test`: PostgreSQL efímero del profile `test`.
- `backend-test`: suite Maven completa contra `postgres-test`.
- `frontend-test`: suite Vitest aislada del backend real.
- `backend-debug`: Spring Boot con JDWP.

## Volumes

`postgres_data`, `maven_cache`, `frontend_node_modules`.

## Ports

- Frontend: `127.0.0.1:13003` → `http://localhost:13003`.
- Backend: `127.0.0.1:18083` → `http://localhost:18083`.
- JDWP: `127.0.0.1:15007` → `localhost:15007`.
- PostgreSQL no publica puertos al host.

## Commands

Ejecutar desde `Lab-3/` o anteponer `-f Lab-3/compose.yml`.

```bash
docker compose up --build
docker compose down
docker compose ps
docker compose config
docker compose logs -f backend
docker compose logs -f frontend
docker compose --profile test run --rm backend-test
docker compose --profile test run --rm frontend-test
docker compose --profile test run --rm backend-test mvn test -Dspring.profiles.active=test --no-transfer-progress
docker compose --profile test run --rm frontend-test npm test -- --run
docker compose --profile debug up --build backend-debug postgres
```

Para psql sin exponer PostgreSQL al host:

```bash
docker compose exec postgres psql -U lab3 -d lab3
```

No se necesitan Java, Maven, Node, npm ni PostgreSQL instalados globalmente. `.env.example` documenta los defaults; una copia `.env` no es necesaria.

## Project structure

```text
backend/src/main/java/com/lab3/
├── config  controller  domain  dto  exception
├── mapper  repository  service
└── Lab3Application.java
backend/src/main/resources/db/migration/
frontend/src/
├── api  components  hooks  pages  test  types
├── App.tsx  main.tsx  style.css
```

## Domain

- `Merchant`: `id`, `name`, `active`.
- `Payment`: UUID, merchant, amount, status, `createdAt`, description nullable.
- `PaymentStatus`: `PENDING`, `CAPTURED`, `FAILED`, `REFUNDED`.
- `PaymentAudit`: payment UUID, estado anterior, estado nuevo y fecha.

## API contracts

- `GET /api/merchants`
- `GET /api/merchants/{merchantId}/payments?status&page&size` (page zero-based; size por defecto 5)
- `GET /api/payments/{paymentId}`
- `PATCH /api/payments/{paymentId}/status` con `{ "status": "CAPTURED" }`; responde `204 No Content`.
- `GET /api/payments/{paymentId}/audit`

La respuesta paginada contiene `content`, `page`, `size`, `totalElements` y `totalPages`. CORS permite únicamente `http://localhost:13003`.

## Seed data

Flyway crea tres merchants y 16 payments. IDs útiles:

- M1 (`Northstar Market`): 12 payments, incluidos `00000000-0000-0000-0000-000000000003`, `...0007` y `...0010` en estado pendiente.
- M2 (`Cedar & Co`): payments `...0013`, `...0014`, `...0015`.
- M3 (`Atlas Goods`): payment `...0016`.
- Payment `...0001` sirve para reproducir el detalle capturado; `...0005` es un payment fallido.

## Initial test state

```text
Backend:
18 tests
15 passing
3 failing

Frontend:
14 tests
11 passing
3 failing

Total:
32 tests
26 passing
6 failing
```

Son fallos deliberados y constituyen la evidencia inicial del challenge.

## Tickets 0-7

### Ticket 0 — Orientación

Levanta frontend y backend, localiza entrypoints, identifica ambas URLs, describe el flujo React → API → Controller → Service → Repository → PostgreSQL, ejecuta las dos suites, observa los seis fallos y usa la aplicación manualmente.

### Ticket 1 — Paginación

- Síntoma: cambiar de página presenta un conjunto incorrecto o repetido.
- Expected: la página visual siguiente muestra los siguientes cinco payments y respeta el contrato zero-based del API.
- Habilidad: contratos HTTP, paginación, Network y estado frontend.

### Ticket 2 — Status actualizado pero UI dice error

- Síntoma: el backend actualiza correctamente, pero la interfaz informa de fallo.
- Expected: una actualización HTTP exitosa se muestra como exitosa sin pedir un payload inexistente.
- Habilidad: HTTP, status codes, fetch, Network y Console.

### Ticket 3 — UI no refleja status

- Síntoma: tras una operación válida, la vista conserva el estado anterior hasta refrescar o consultar de nuevo.
- Expected: el detalle refleja inmediatamente el nuevo estado.
- Habilidad: inmutabilidad, estado React y render.

### Ticket 4 — Merchant name incorrecto

- Síntoma: el detalle de Payment presenta un nombre de merchant que no coincide.
- Expected: `merchantId` y `merchantName` representan el merchant correcto.
- Habilidad: DTO, mapper, capas y contrato API.

### Ticket 5 — Inconsistencia Payment/Audit

- Síntoma: ante un fallo durante auditoría, Payment puede quedar parcialmente actualizado.
- Expected: actualización y auditoría son una operación atómica.
- Habilidad: transacciones, rollback, service boundaries y PostgreSQL.

### Ticket 6 — Transición inválida

- Síntoma: una transición que debería rechazarse es aceptada.
- Expected: solo `PENDING → CAPTURED` y `PENDING → FAILED`; las demás responden `409 Conflict`.
- Habilidad: reglas de negocio, status HTTP y tests.

### Ticket 7 — Merchant Summary

Implementa después de corregir Tickets 1-6. Añade `GET /api/merchants/{merchantId}/summary` con `merchantId`, `totalPayments`, `capturedCount`, `capturedAmount`, `pendingCount`, `failedCount` y `refundedCount`. La UI debe mostrar una card al seleccionar merchant con loading, error y datos correctos.

Añade como mínimo cuatro tests nuevos: un unit e integration test backend, y un API/hook test y component test frontend. Esta feature no está implementada en el challenge entregado.

## Debugging tools

Están disponibles logs Docker, healthchecks, Spring logs, debugger Java con JDWP en `localhost:15007`, breakpoints JUnit, DevTools del navegador (Network, Console, Sources), estado React observable por comportamiento y salida Vitest.

## Completion criteria

- Tickets 1-6 corregidos.
- Los 32 tests originales verdes.
- TypeScript y build Vite correctos.
- Ticket 7 implementado con mínimo cuatro tests nuevos.
- Suite completa verde, flujo manual correcto y sin regresiones.

## Continuity context

Lab-3 es un laboratorio independiente de integración Full Stack. Usa React 19/TypeScript/Vite en `localhost:13003`, Spring Boot 3.5.4/Java 21 en `localhost:18083`, PostgreSQL 17 interno y JDWP en `localhost:15007`. El baseline es 32 tests: 26 pass y 6 fallan deliberadamente, repartidos en tres tickets frontend y tres backend. Los seis tickets iniciales describen síntomas sin soluciones. Ticket 7 añade Merchant Summary de extremo a extremo y cuatro tests mínimos. Los comandos principales son `docker compose up --build`, `docker compose --profile test run --rm backend-test`, `docker compose --profile test run --rm frontend-test` y `docker compose --profile debug up --build backend-debug postgres`.
