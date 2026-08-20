# Lab 02 — Java Legacy & Refactoring

## Contrato

Backend monolítico Spring Boot 3.5.4, Java 21, Maven 3.9.9, PostgreSQL 17-alpine, JPA/Flyway, JUnit 5, Mockito y Docker Compose. Investiga, caracteriza y refactoriza incrementalmente; no hagas big-bang rewrite, no cambies contratos HTTP/schema ni uses Error Lens como verdad.

Arquitectura: `HTTP -> PaymentController -> LegacyPaymentProcessor -> repositories -> PostgreSQL`, con auditoría y notificación. Comandos:

```bash
docker compose config
docker compose up --build postgres app       # HTTP 127.0.0.1:18082
docker compose --profile test run --rm test
docker compose --profile debug up --build postgres debug # JDWP 127.0.0.1:15006
docker compose down
```

`postgres-test` es efímero/aislado; Flyway aplica V1, crea `refund` y carga seed. PostgreSQL no publica puertos.

## Baseline y Easy

Baseline deliberado: **20 tests, 16 PASS, 4 FAIL**, solo T1–T4.

- **T1 CAPTURED**: comparación de referencias (`==`) en `LegacyPaymentProcessor`; solución validada: `CAPTURED.equals(currentStatus)`.
- **T2 notificación duplicada**: dos llamadas `notifyProcessed`; dejar una (verificar con Mockito).
- **T3 auditoría fallida**: el `catch` deja `processed=true`; no confirmar estado si falla una operación necesaria.
- **T4 merchant inactivo**: ruta legacy muta antes de validar `active=false`; validar merchant primero.

Starting point: ejecuta suite, sigue controller→processor→repository y reproduce con seed/tests. Hints: 1) stack trace/valores, 2) breakpoint en cada `if`, 3) verifica interacciones y estado persistido. Error común: editar el test deliberado.

## Intermediate — characterization/refactoring

Añade characterization tests para estados, amount, merchant, provider reference, auditoría y notificación antes de mover código. Flujo obligatorio **RED → GREEN → REFACTOR**: cambio mínimo, suite tras cada paso, diff reversible. Extrae validación/decisión/side-effects sin cambiar contratos. Hints: 1) extrae una regla, 2) protege cada extracción, 3) compara HTTP, auditorías, estado e interacciones. No mezcles corrección y reescritura.

## Advanced — Partial Refund y concurrencia

TDD para `POST /api/payments/{paymentId}/refunds`, body `{"amount":25.00}`: solo CAPTURED, amount > 0, suma ≤ captured, primer parcial, segundo hasta completar y over-refund rechazado; cubrir inexistente/no CAPTURED, cero y negativo. Debe demostrarse RED/GREEN/REFACTOR.

Advanced real: caracteriza dos refunds concurrentes y valida antes de incorporar locking/aislamiento/idempotencia; la invariantes monetaria no puede romperse. Hints: 1) BigDecimal/saldo, 2) persistencia atómica, 3) carrera y reintento. No lo incorpores al baseline sin diseño.

## Learning / Interview / Review

Learning usa hints y debugger; Interview exige explicar evidencia, trade-offs y compatibilidad; Review exige caracterización, diff pequeño, límites transaccionales y matriz verde.

### Mentor / AI spoilers

Raíces verificadas: T1 igualdad de referencias; T2 llamada duplicada; T3 mutación en catch; T4 validación tardía. Las correcciones temporales dieron 20/20. T5/T6 admiten soluciones incrementales múltiples; no cambies challenge tests, schema o contratos.

## Validation matrix y troubleshooting

| Área | Evidencia |
|---|---|
| Compose | `docker compose config`; postgres healthy |
| Compile | Maven compile/testCompile Java 21 |
| App/Flyway | Spring Boot arriba, V1/seed consultables |
| JDWP | localhost:15006 y log dt_socket |
| Baseline | 20 = 16 PASS + 4 FAIL deliberados |
| Green temporal | T1–T4, characterization y refund pasan |
| Refactor | contratos/schema/comportamiento preservados |

Revisa healthcheck/puertos ante fallos Compose; el warning Byte Buddy dynamic-agent es tooling/JDK, no challenge. Si cambia el conteo, restaura tests deliberados. Elimina solo volúmenes locales del laboratorio si Flyway queda obsoleto.

## Agent Continuity

Alcance exclusivo de este directorio. Mantener baseline 16/20. T1–T4 deliberados; T5 characterization + refactor incremental; T6 Partial Refund TDD; Advanced puede explorar concurrencia/aislamiento. Antes de entregar: suite Docker, `git diff -- lab-02-java-legacy-refactoring` y ausencia de soluciones temporales.

## Criterio Full / 100%

Easy, Intermediate, Advanced y Agent Continuity documentados. Validación temporal ejecutada el 2026-08-20: con una implementación mínima de `RefundService` y siete tests adicionales (seis de negocio y uno concurrente), la suite alcanzó **27/27 PASS**. La secuencia TDD quedó evidenciada como RED (los cuatro defectos base seguían fallando), GREEN (T1–T4 corregidos temporalmente + refund), y REFACTOR (validación sincronizada de suma/refund y prueba concurrente). Casos verificados: parcial válido, segundo hasta completar, over-refund, no CAPTURED, cero y negativo.

El Advanced validado fue la carrera de dos refunds de 60 sobre un payment CAPTURED de 100: exactamente uno prospera y la suma persistida queda en 60, mediante sección crítica transaccional en la implementación temporal. No se conserva esa solución: el baseline vuelve a ser 20 tests, 16 PASS y 4 FAIL deliberados T1–T4.
