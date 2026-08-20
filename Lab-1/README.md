# Lab-1 — Java Spring Boot Debugging Challenge

## Objetivo

Este laboratorio simula una prueba técnica de backend en la que el candidato recibe una aplicación pequeña, funcionalmente cercana al estado real de una API de pagos, pero con varios defectos deliberados sembrados en el flujo de negocio y en la capa de validación. El objetivo no es reescribir el sistema, sino comprenderlo, diagnosticarlo y corregirlo con pruebas y debugging sobre una stack real.

Este ejercicio entrena:

- comprensión de un proyecto desconocido;
- Java 21 y Spring Boot;
- tests automatizados;
- debugging y lectura de stack traces;
- navegación entre capas de una aplicación web;
- PostgreSQL básico;
- mantenimiento de un flujo HTTP → Controller → Service → Repository → DB;
- corrección de bugs y validación de regresiones;
- trabajo con Docker-first y perfiles de entorno.

## Stack

Versiones reales utilizadas por este laboratorio:

- Java: 21
- Maven: 3.9.9
- Spring Boot: 3.5.4
- PostgreSQL: 17-alpine
- JUnit: 5 (incluido con Spring Boot starter test)
- Mockito: incluido con Spring Boot starter test
- Flyway: 10.x (gestionado por Spring Boot starter)
- Spring Data JPA: incluida con spring-boot-starter-data-jpa

## Arquitectura

La aplicación sigue este flujo funcional:

HTTP
↓
Controller
↓
Service
↓
Repository
↓
PostgreSQL

Responsabilidades reales:

- Controller: expone endpoints HTTP y transforma llamadas a la capa de servicio.
- Service: implementa la lógica de negocio, validaciones de dominio y coordinación del flujo.
- Repository: acceso a datos con Spring Data JPA y consultas específicas.
- Entity/domain: modela Merchant y Payment con sus atributos y relaciones.
- DTO: transporta payloads de entrada y salida entre HTTP y aplicación.
- Mapper: convierte entidades a respuestas públicas sin mezclar capas.
- Exception handling: centraliza errores HTTP y mensajes de negocio.
- Flyway: aplica esquema y seed inicial sobre PostgreSQL.

## Árbol del proyecto

```text
Lab-1/
├── Dockerfile
├── README.md
├── compose.yml
├── pom.xml
├── .dockerignore
├── .env.example
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/com/lab1/
│   │   │   ├── config/
│   │   │   │   └── RestExceptionHandler.java
│   │   │   ├── controller/
│   │   │   │   └── PaymentController.java
│   │   │   ├── domain/
│   │   │   │   ├── Merchant.java
│   │   │   │   ├── Payment.java
│   │   │   │   └── PaymentStatus.java
│   │   │   ├── dto/
│   │   │   │   ├── CapturedTotalResponse.java
│   │   │   │   ├── PaymentCreateRequest.java
│   │   │   │   └── PaymentResponse.java
│   │   │   ├── exception/
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── mapper/
│   │   │   │   └── PaymentMapper.java
│   │   │   ├── repository/
│   │   │   │   ├── MerchantRepository.java
│   │   │   │   └── PaymentRepository.java
│   │   │   ├── service/
│   │   │   │   └── PaymentService.java
│   │   │   └── Lab1Application.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           ├── V1__schema.sql
│   │           └── V2__seed.sql
│   └── test/
│       ├── java/com/lab1/
│       │   ├── Lab1ApplicationTests.java
│       │   ├── PaymentAmountCalculationTest.java
│       │   ├── PaymentBugBigDecimalTest.java
│       │   ├── PaymentBugNegativeAmountTest.java
│       │   ├── PaymentBugPendingNullProviderTest.java
│       │   ├── PaymentBugStatusFilterTest.java
│       │   ├── PaymentControllerSmokeTest.java
│       │   ├── PaymentCreateRequestValidationTest.java
│       │   ├── PaymentCreateTest.java
│       │   ├── PaymentExceptionTest.java
│       │   ├── PaymentListTest.java
│       │   ├── PaymentMapperTest.java
│       │   ├── PaymentRepositoryTest.java
│       │   ├── PaymentRepositoryTotalTest.java
│       │   ├── PaymentServiceTest.java
│       │   └── PaymentStatusEnumTest.java
│       └── resources/
│           └── application-test.yml
```

## Arquitectura Docker

El laboratorio está pensado para ejecutarse en Docker desde el primer momento. No está orientado a que el alumno instale Java, Maven o PostgreSQL en el host.

Servicios principales:

- postgres: base de datos principal de desarrollo.
- app: API Spring Boot de desarrollo.
- postgres-test: base de datos aislada para tests.
- test: ejecuta la suite automatizada sobre postgres-test.
- debug: arranca la API en modo debug con JDWP.

Perfiles:

- default: app + postgres
- test: postgres-test + test
- debug: postgres + debug

Redes y almacenamiento:

- red Compose por defecto para los servicios del laboratorio;
- volumen postgres_data para persistencia del entorno de desarrollo;
- volumen maven_cache para cache de dependencias Maven;
- test usa postgres-test con almacenamiento efímero para aislar la DB de pruebas;
- las bases de datos no exponen puertos al host salvo el puerto HTTP y JDWP de la app/debug.

Healthchecks:

- PostgreSQL se valida con pg_isready antes de permitir arranque de la app o tests.

Puertos y debugging:

- host → localhost:18081 → app/debug
- host → localhost:15005 → JDWP dentro del contenedor del servicio debug

Diagrama:

```text
Host
│
├── localhost:18081
│       ↓
│     app/debug
│       ↓
│     postgres
│
└── localhost:15005
        ↓
       JDWP

Tests:

test
 ↓
postgres-test
```

Por qué PostgreSQL no publica puerto al host:

- la aplicación se comunica por red Docker interna;
- el acceso al servicio de base de datos se gestiona desde los contenedores;
- la intención del laboratorio es aislar la capa de persistencia y no depender de un puerto de host para el flujo normal.

## Operación

Los comandos que se usan para operar el laboratorio son Docker/Compose y no requieren instalación local de Java, Maven o PostgreSQL.

```bash
# levantar la app en desarrollo
cd Lab-1
docker compose up --build postgres app

# detener la app
cd Lab-1
docker compose down

# ejecutar la suite de tests
cd Lab-1
docker compose --profile test up --build --abort-on-container-exit test

# arrancar modo debug
cd Lab-1
docker compose --profile debug up --build postgres debug

# ver logs de la app
cd Lab-1
docker compose logs -f app

# ver logs del debug
cd Lab-1
docker compose --profile debug logs -f debug

# ver logs de tests
cd Lab-1
docker compose --profile test logs -f test

# inspeccionar estado de contenedores
cd Lab-1
docker compose ps

# inspeccionar configuración del compose
cd Lab-1
docker compose config

# entrar a PostgreSQL de desarrollo
cd Lab-1
docker compose exec postgres psql -U lab1 -d lab1
```

## Dominio

Entidades principales:

Merchant

- id: identificador principal de merchant
- name: nombre del comerciante
- relación: un merchant puede tener varios payments

Payment

- id: UUID
- merchant: relación con Merchant
- amount: importe monetario en BigDecimal
- status: enum PaymentStatus
- providerReference: referencia del proveedor, puede ser nula en algunos estados
- createdAt: timestamp UTC de creación

PaymentStatus

- PENDING
- CAPTURED
- FAILED
- REFUNDED

## Endpoints

La API disponible incluye, al menos:

- POST /api/payments
- GET /api/payments/{paymentId}
- GET /api/merchants/{merchantId}/payments
- GET /api/merchants/{merchantId}/captured-total

El flujo principal de negocio se centra en Merchant y Payment; los endpoints se usan para crear, listar y consultar pagos por comercio.

## Datos seed

La base de datos inicial usa los siguientes datos relevantes para reproducir los tickets:

Merchants:

- M1 — Merchant One
- M2 — Merchant Two

Payments seed:

- 11111111-1111-4111-8111-111111111111 — M1 — 100.00 — CAPTURED — PROVIDER-123
- 22222222-2222-4222-8222-222222222222 — M1 — 50.00 — CAPTURED — PROVIDER-456
- 33333333-3333-4333-8333-333333333333 — M1 — 30.00 — FAILED — PROVIDER-789
- 44444444-4444-4444-8444-444444444444 — M2 — 200.00 — CAPTURED — PROVIDER-999
- 55555555-5555-4555-8555-555555555555 — M1 — 25.00 — PENDING — NULL

Estos datos permiten reproducir los síntomas del challenge sobre la suma de pagos capturados, el tratamiento de null en providerReference y el filtro por estado.

## Estado inicial del challenge

La suite inicial del laboratorio está diseñada para esperar este resultado:

```text
16 tests
12 passing
4 failing
```

Los cuatro errores forman parte del challenge y son deliberados.

## Tickets

### Ticket 0 — Arranque y orientación

Síntoma: la aplicación no se entiende de inmediato y el alumno debe localizar la capa correcta, ejecutar la suite y observar el estado real del proyecto.
Comportamiento esperado: arranque funcional, contexto Spring correcto, base de datos preparada por Flyway y pruebas ejecutándose con Docker.
Competencia entrenada: comprensión del proyecto y del flujo de ejecución.

### Ticket 1 — cálculo monetario incorrecto

Síntoma: el total CAPTURED de M1 no coincide con el esperado.
Comportamiento esperado: la suma de pagos capturados debe dar 150.00.
Competencia entrenada: cálculo monetario y validación con BigDecimal.

### Ticket 2 — tratamiento de providerReference nulo

Síntoma: un pago PENDING válido puede provocar un error al serializar o consultar la entidad.
Comportamiento esperado: la API y la serialización deben manejar correctamente un providerReference nulo sin romper la consulta.
Competencia entrenada: null handling y navegación entre capas.

### Ticket 3 — filtro de estado ignorado

Síntoma: al consultar pagos por merchant + status, se devuelven elementos de otros estados.
Comportamiento esperado: la consulta debe devolver únicamente los pagos del estado solicitado.
Competencia entrenada: navegación entre capas y filtrado en Repository/Service.

### Ticket 4 — validación de amount positivo

Síntoma: la API acepta un amount negativo.
Comportamiento esperado: una solicitud con amount negativo debe rechazarse con error 400.
Competencia entrenada: validación de entrada y contrato HTTP.

### Ticket 5 — resumen de merchant

Contrato de la feature temporalmente añadida:

```json
{
  "merchantId": "M1",
  "capturedCount": 2,
  "capturedTotal": 150.0,
  "failedCount": 1
}
```

Endpoint objetivo:

- GET /api/merchants/{merchantId}/summary

Criterios:

- debe devolver el total capturado para un merchant;
- debe contar pagos CAPTURED;
- debe contar pagos FAILED;
- debe estar cubierto por al menos un unit test y un integration test.

No se incluye la solución en este README; solo se documenta el contrato y la intención de la feature.

## Debugging disponible

El laboratorio permite:

- debugging remoto JDWP;
- debugging de tests;
- logs de Spring Boot;
- stack traces y errores de capa HTTP;
- verificación de estado de contenedores y healthchecks.

No se trata todavía de un tutorial de breakpoints o Step Into.

## Criterios de finalización

El laboratorio se considera terminado solo cuando:

- Tickets 1-4 están corregidos;
- los 16 tests originales vuelven a estar verdes;
- Ticket 5 está implementado;
- existen pruebas adicionales para Ticket 5;
- la suite completa queda verde sin regresiones.

## Contexto de continuidad

Lab-1 es un laboratorio de backend Java con Spring Boot, PostgreSQL y Docker. Está diseñado como una prueba técnica de debugging y corrección de defectos en un flujo de pagos. La aplicación usa una arquitectura simple HTTP → Controller → Service → Repository → PostgreSQL, se ejecuta principalmente con Docker Compose y se valida a través de perfiles de app, tests y debug.

El estado inicial esperado del challenge es:

- 16 tests
- 12 passing
- 4 failing
- 4 errores deliberados del challenge
- Ticket 5 no incorporado en el estado base

Los comandos más importantes para retomar el trabajo son:

- docker compose up --build postgres app
- docker compose --profile test up --build --abort-on-container-exit test
- docker compose --profile debug up --build postgres debug
- docker compose logs -f app
- docker compose ps
- docker compose config
- docker compose exec postgres psql -U lab1 -d lab1

Con este contexto, una IA o agente puede retomar el trabajo sin necesidad de inspeccionar el proyecto completo.
