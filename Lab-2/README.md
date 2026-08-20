# Lab-2 - Java Legacy Challenge

## Objetivo

Este laboratorio simula una prueba tecnica sobre una aplicacion heredada de pagos. Entrena la orientacion en un proyecto desconocido, comprension de codigo existente, debugging con evidencia, tests de caracterizacion, correccion de bugs, refactoring seguro y desarrollo mediante TDD.

Legacy aqui describe principalmente la historia y mantenibilidad del codigo, no una version antigua de Java. El objetivo no es reescribir el proyecto: los cambios deben ser incrementales, conservar contratos y demostrar ausencia de regresiones.

Senioridad objetivo: perfil senior o semi-senior avanzado con experiencia en Java/Spring y capacidad para investigar antes de modificar.

## Stack efectivo

- Java 21
- Maven 3.9.9, ejecutado dentro de Docker
- Spring Boot 3.5.4
- PostgreSQL 17-alpine
- Spring Data JPA e Hibernate
- Bean Validation
- Flyway
- JUnit 5, Mockito y Spring Boot Test
- REST, Docker y Docker Compose

No se usa H2, MySQL, Testcontainers, frontend, Redis, Kafka, RabbitMQ, Elasticsearch, Kubernetes ni Gradle.

## Arquitectura

```text
HTTP
  |
Controller
  |
LegacyPaymentProcessor / Services
  |
Repositories
  |
PostgreSQL
       \\ Notification
       \\ Audit
```

El `LegacyPaymentProcessor` concentra deliberadamente reglas, validaciones, persistencia, auditoria, notificacion y efectos secundarios. La aplicacion es un unico backend Spring Boot. El procesador se inyecta con `PaymentRepository`, `MerchantRepository`, `ProcessingAuditRepository` y `MerchantNotificationService`.

## Estructura

```text
Lab-2/
|- compose.yml, Dockerfile, .dockerignore, .env.example
|- pom.xml
|- README.md
`- src/
   |- main/java/com/example/lab2/
   |  |- controller/PaymentController.java
   |  |- service/LegacyPaymentProcessor.java
   |  |- domain/{Merchant,Payment,Refund,ProcessingAudit}.java
   |  |- domain/repository/*Repository.java
   |  |- notification/MerchantNotificationService.java
   |  `- exception/DomainException.java
   |- main/resources/application.yml
   `- main/resources/db/migration/V1__schema.sql
```

Los tests estan en `src/test/java/com/example/lab2`: 8 unit tests con Mockito y 12 integration tests con Spring Boot Test y PostgreSQL real.

## Docker-first

El alumno no necesita Java, Maven ni PostgreSQL instalados globalmente. El workspace se monta en `/workspace`, y Maven usa el volumen logico `maven_cache` en `/root/.m2`.

Servicios obligatorios:

- `postgres`: desarrollo, `postgres:17-alpine`, base `lab2`, usuario `lab2`, password local `lab2_dev`, volumen `postgres_data`, sin puerto host y con healthcheck `pg_isready`.
- `app`: construye con el Dockerfile, depende de `postgres` healthy y publica solo `127.0.0.1:18082:8080`.
- `postgres-test`: profile `test`, base `lab2_test`, password `lab2_test`, sin puerto ni volumen compartido; es efimero.
- `test`: profile `test`, depende de `postgres-test` healthy y ejecuta exclusivamente Maven tests sin puertos.
- `debug`: profile `debug`, reutiliza la app y publica HTTP en `127.0.0.1:18082:8080` y JDWP en `127.0.0.1:15006:5005`.

Todos usan logging local Compose con driver `json-file`, `max-size=10m` y `max-file=3`. No se modifica la configuracion Docker global.

```text
Host
|
|- localhost:18082 -> app/debug -> postgres
`- localhost:15006 -> JDWP

Tests:
test -> postgres-test
```

La base de desarrollo persiste en `postgres_data`; la base de tests no comparte ese volumen. PostgreSQL no publica ningun puerto al host.

## Comandos operativos

Ejecutados desde `Lab-2`:

```bash
docker compose config
docker compose up --build postgres app
docker compose --profile test run --rm test
docker compose --profile debug up --build postgres debug
docker compose ps
docker compose logs app
docker compose logs debug
docker compose logs test
docker compose logs postgres
docker compose exec postgres psql -U lab2 -d lab2
docker compose down
```

Para adjuntar un debugger de tests se puede depurar la JVM de Maven en el contenedor; para la app debug el attach es `host=localhost`, `port=15006`. JDWP usa `transport=dt_socket,server=y,suspend=n,address=*:5005`.

## Dominio

`Merchant` tiene `id: String`, `name: String` y `active: boolean`. `Payment` tiene `id: UUID`, merchant, amount, `status: String`, `providerReference` nullable, `processed` y `createdAt`. Los estados legacy existentes son `PENDING`, `CAPTURED`, `FAILED` y `REFUNDED`; no se convierten inicialmente a enum.

`ProcessingAudit` registra `paymentId`, action y timestamp. `Refund` tiene id, payment, amount y timestamp, y su repository y tabla ya existen para el Ticket 6, pero no existe aun la operacion de refund.

## Endpoints disponibles

- `GET /api/payments/{id}`
- `POST /api/payments/{id}/process`
- `GET /api/merchants/{merchantId}/payments`
- `GET /api/payments/{id}/audit`

No implementes todavia el endpoint definitivo de refund.

## Seed

- `M1`: Acme Shop, activo.
- `M2`: Example Store, activo.
- `M3`: Old Merchant, inactivo.
- `11111111-1111-1111-1111-111111111111`: CAPTURED, M1, amount 100.00, provider reference presente.
- `22222222-2222-2222-2222-222222222222`: PENDING, M1, amount 50.00, provider reference null.
- `33333333-3333-3333-3333-333333333333`: FAILED, M2, amount 75.00.
- `44444444-4444-4444-4444-444444444444`: REFUNDED, M2, amount 25.00, provider reference presente.
- `55555555-5555-5555-5555-555555555555`: CAPTURED, M3, amount 80.00, provider reference presente.

Flyway crea schema, datos y tabla de `refund` al arrancar cada base.

## Estado inicial

```text
20 tests
16 passing
4 failing
```

Los cuatro fallos son deliberados y corresponden exclusivamente a Tickets 1-4. Los code smells no cubiertos no deben tratarse automaticamente como bugs.

## Tickets 0-4

### Ticket 0 - Orientacion

Levanta el entorno, ejecuta la suite, localiza el entry point, sigue el flujo HTTP, identifica el procesador, sus dependencias y los datos seed. Usa logs, stack traces y debugger como evidencia.

### Ticket 1 - CAPTURED que no se procesa

Sintoma: un payment aparentemente CAPTURED no sigue el flujo esperado. Reproduce con `11111111-1111-1111-1111-111111111111` y `POST /api/payments/{id}/process`.

Esperado: el pago valido sigue la rama de procesamiento y queda correctamente tratado. Actual: la operacion no sigue esa rama. Competencia: inspeccion de valores, referencias y debugging.

### Ticket 2 - Notificacion duplicada

Sintoma: una operacion valida notifica dos veces al merchant. Esperado: una notificacion por operacion. Usa el unit test y Mockito para observar interacciones y seguir el flujo.

### Ticket 3 - Estado procesado tras fallo interno

Sintoma: un fallo interno reproducible mediante test deja el payment marcado como procesado. Esperado: no considerarlo correctamente procesado si una parte necesaria falla. Competencia: excepciones, logs, call stack y efectos parciales.

### Ticket 4 - Merchant inactivo

Sintoma: un payment asociado a M3 puede terminar procesandose. Esperado: rechazo controlado y ningun cambio que lo marque procesado. Competencia: razonamiento booleano y reglas de negocio.

El README no proporciona causa raiz, archivo, linea, operador ni fix de ninguno de los cuatro tickets.

## Ticket 5 - Refactoring legacy

Antes de cambios estructurales, anade tests de caracterizacion para comportamientos que no esten cubiertos. Trabaja en pasos pequenos y deja la suite verde despues de cada fase. No hagas un big-bang rewrite, no cambies los contratos HTTP ni el schema, no conviertas el proyecto en microservicios y no introduzcas patrones sin necesidad.

El objetivo es reducir complejidad, duplicacion y acoplamiento, separar reglas de side effects, mejorar nombres y aumentar testabilidad. No se impone una arquitectura exacta; varias soluciones incrementales son validas.

## Ticket 6 - Partial Refund mediante TDD

Despues del refactoring, implementa:

`POST /api/payments/{paymentId}/refunds`

con request:

```json
{"amount": 25.00}
```

La suma total de refunds nunca puede superar el importe originalmente capturado. Empieza siempre con tests y cubre: refund parcial valido, segundo refund mientras quede saldo, exceso sobre saldo restante, payment no CAPTURED, amount cero y amount negativo. Un refund invalido debe producir HTTP 400 o un conflicto de dominio coherente con la convencion elegida.

Proceso requerido:

```text
RED -> GREEN -> REFACTOR
```

No hay solucion de Ticket 6 en la entrega inicial.

## Debugging

El laboratorio prepara JDWP, breakpoints, inspeccion de variables, call stack, logs, stack traces y fallos de Mockito. No presupone una configuracion de IDE concreta.

## Criterios de finalizacion

El laboratorio se considera resuelto cuando Tickets 1-4 estan corregidos, los 20 tests originales estan verdes, existen tests de caracterizacion, el procesador esta refactorizado sin regresiones, Ticket 6 se implementa mediante TDD y los tests nuevos dejan la suite completa verde.

## Contexto de continuidad

Lab-2 es un backend monolitico Spring Boot 3.5.4, Java 21, Maven 3.9.9 y PostgreSQL 17-alpine. Docker Compose ofrece `postgres`, `app`, `postgres-test`, `test` y `debug`; HTTP usa `localhost:18082`, JDWP `localhost:15006`, y PostgreSQL nunca se publica al host. Flyway crea un seed de merchants y payments. El baseline tiene exactamente 20 tests, 16 passing y 4 failing deliberados, cuatro tickets de debugging, un refactoring incremental pendiente y un Partial Refund que debe nacer mediante RED/GREEN/REFACTOR. Los comandos principales son `docker compose config`, `docker compose up --build postgres app`, `docker compose --profile test run --rm test` y `docker compose --profile debug up --build postgres debug`.
