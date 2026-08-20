# Lab-06 - Foundation + Easy Data Engineering

Lab Docker-first de ingesta y reporting de pagos. El estado candidato contiene tres defectos deliberados e independientes. El objetivo es ejecutar, observar, formular una hipotesis, corregir la causa minima y verificarla con evidencia.

## Identidad y objetivos

- Dificultad: Easy e Intermediate implementados; Advanced pendiente.
- Tiempo orientativo Easy: 20-60 minutos.
- Dominio: pequenos eventos de pagos con calidad de datos imperfecta.
- Resultados: validar registros, persistir en PostgreSQL, repetir ingestas de forma segura y calcular un reporte por merchant/status.
- Restriccion: no se implementa aun busqueda, sincronizacion ni challenges de Elasticsearch.

## Stack y arquitectura

- Python 3.12.11, FastAPI 0.116.1, Pydantic 2.11.7.
- SQLAlchemy 2.0.43, Alembic 1.16.4, PostgreSQL 17.5-alpine.
- pytest 8.4.1, httpx 0.28.1.
- Elasticsearch 9.1.2 single-node, sin Kibana y con heap de 256 MB.

```text
POST /api/ingest
        |
        v
 Pydantic -> ingestion service -> SQLAlchemy -> PostgreSQL
                                      |
GET /api/merchants/{id}/report?status=...

Elasticsearch participa en la sincronizacion/resync de Intermediate; no hay aun challenges Advanced.
```

## Docker

Desde este directorio:

```bash
docker compose config
docker compose up --build postgres elasticsearch app
```

La API queda en `http://localhost:18086`. PostgreSQL y Elasticsearch no publican puertos al host. Para tests aislados:

```bash
docker compose --profile test up --build --abort-on-container-exit --exit-code-from test test
docker compose --profile test down
```

El servicio `test` usa `postgres-test`, una base efimera separada. Alembic se ejecuta antes de arrancar la API y antes de pytest.

Comprobaciones utiles:

```bash
docker compose ps
docker compose logs app
docker compose exec postgres psql -U lab6 -d lab6
```

## Dataset

`app/data/payments.json` es original y versionado. Contiene cinco registros validos y uno invalido sin `amount`, con merchants `M-ALPHA` y `M-BETA`, importes en EUR, statuses `CAPTURED`, `FAILED`, `PENDING` y `REFUNDED`. Los `source_event_id` son estables para reproducir replay y duplicados.

Ingesta del dataset:

```bash
curl -X POST http://localhost:18086/api/ingest
curl http://localhost:18086/api/merchants/M-ALPHA/report?status=CAPTURED
```

## Baseline esperado

Hay 15 tests: 9 PASS y 6 FAIL deliberados. No hay fallos accidentales.

| Ticket | Test                                                       | Resultado baseline | Sintoma                                       |
| ------ | ---------------------------------------------------------- | ------------------ | --------------------------------------------- |
| E1     | `test_e1_invalid_dataset_row_is_rejected`                  | FAIL deliberado    | falta `amount` y la fila termina persistida   |
| E2     | `test_e2_reingesting_same_events_is_idempotent`            | FAIL deliberado    | el replay inserta otra vez los mismos eventos |
| E3     | `test_e3_report_aggregates_only_requested_status`          | FAIL deliberado    | el reporte mezcla statuses                    |
| I1     | `test_i1_mixed_replay_inserts_only_new_events`             | FAIL deliberado    | el replay mixto informa demasiados inserts    |
| I2     | `test_i2_reconciliation_reports_status_and_total_mismatch` | FAIL deliberado    | falta una discrepancia de status persistido   |
| I3     | `test_i3_postgres_payment_is_readable_from_elasticsearch`  | FAIL deliberado    | PostgreSQL se actualiza pero ES queda stale   |

Los otros nueve tests comprueban health, migracion, validacion de status, persistencia decimal, ingesta valida, reporte simple y merchant inexistente.

## Easy track

### E1 - Input/Data validation

Contexto: una fila de origen no tiene `amount`.

- Observado: se acepta y se persiste con valor cero.
- Esperado: se rechaza, no se persiste y el resultado informa `accepted=5`, `rejected=1`, `inserted=5`.
- Reproduccion: `pytest -q tests/test_easy_e1_validation.py` dentro del servicio test.
- Starting Point: `PaymentRecord` y el bloque de validacion de `/api/ingest`.
- Acceptance: validar campos requeridos, no fabricar importes y conservar los cinco registros validos.

Hint 1: compara el contrato Pydantic con la fila que carece de `amount`.

Hint 2: observa que ocurre despues de `model_validate` cuando el campo no esta presente.

Hint 3: el valor por defecto de un campo no sustituye una regla de calidad de datos.

### E2 - Duplicate ingestion

Contexto: el mismo lote puede llegar por replay o retry.

- Observado: la segunda peticion vuelve a insertar los eventos.
- Esperado: `source_event_id` identifica el evento y el segundo lote no crea nuevas filas.
- Reproduccion: `pytest -q tests/test_easy_e2_duplicates.py`.
- Starting Point: el bucle de insercion de `/api/ingest` y la migracion de `payments`.
- Acceptance: dos ingestas producen dos filas totales, la segunda respuesta informa `inserted=0` y la regla queda protegida contra regresion.

Hint 1: identifica el hecho estable que permite reconocer el mismo evento.

Hint 2: sigue la ruta entre validar el record y llamar a `db.add`.

Hint 3: una comprobacion en memoria no es suficiente para varios workers; piensa en la garantia durable de PostgreSQL.

### E3 - Incorrect aggregation

Contexto: operaciones solicita un resumen de un merchant para un status concreto.

- Observado: `M-AGG` con dos capturados de 100 y 50 y un fallido de 30 devuelve `count=3`, `total=180.00`.
- Esperado: para `status=CAPTURED`, devuelve `count=2`, `total=150.00`.
- Reproduccion: `pytest -q tests/test_easy_e3_aggregation.py`.
- Starting Point: la expresion SQL de `merchant_report`.
- Acceptance: filtrar por merchant y status, conservar Decimal y devolver 404 para merchant sin pagos.

Hint 1: compara el parametro HTTP con las condiciones reales del `select`.

Hint 2: inspecciona el `where` y los valores que llegan a `func.count` y `func.sum`.

Hint 3: el status en la respuesta no demuestra que el status se haya usado para agregar.

## Intermediate track

### I1 - Incremental ETL / replay

Contexto: un lote puede mezclar eventos nuevos con eventos ya procesados despues de un replay o retry.

- Observado: `/api/ingest/incremental` no duplica las filas, pero informa como insertados tambien los duplicados.
- Esperado: un lote con un evento nuevo y uno repetido informa `accepted=1`, `duplicated=1`, `inserted=1` y deja dos filas totales.
- Reproduccion: `pytest -q tests/test_intermediate_i1_incremental.py` dentro del servicio test.
- Starting Point: el contador de resultado en `ingest_incremental`; sigue `source_event_id` hasta la consulta PostgreSQL.
- Acceptance: replay seguro, conteos separados de accepted/rejected/duplicated/inserted y ningun duplicado nuevo.

Hint 1: separa la cantidad de registros validos recibidos de la cantidad de filas nuevas.

Hint 2: el `source_event_id` decide si se inserta; revisa que el contador use la misma decision.

Hint 3: un replay correcto puede aceptar el input y no insertar una fila nueva.

### I2 - Data reconciliation

Contexto: operaciones necesita comparar el dataset esperado con el estado persistido y localizar una divergencia, no solo conocer un total.

- Observado: `/api/reconcile` no incluye todos los statuses persistidos en su comparacion.
- Esperado: el reporte expone count, total y desglose por status para expected y persisted, y lista discrepancias cuando corresponda.
- Reproduccion: `pytest -q tests/test_intermediate_i2_reconciliation.py`.
- Starting Point: la agregacion `GROUP BY Payment.status` de `reconcile`.
- Acceptance: localizar la diferencia `FAILED 30.00` esperado frente a `FAILED 10.00` persistido y conservar evidencia del dataset.

Hint 1: compara primero `expected_statuses` con `persisted_statuses`.

Hint 2: revisa si la consulta SQL agrupa todos los estados o aplica un filtro heredado del reporte de merchant.

Hint 3: reconciliar no significa sobrescribir PostgreSQL; significa explicar la discrepancia antes de decidir una correccion.

### I3 - Elasticsearch synchronization

Contexto: PostgreSQL es la fuente persistente y Elasticsearch una proyeccion consultable. Una proyeccion stale no debe confundirse con una escritura perdida en PostgreSQL.

- Observado: `/api/sync` cuenta solo algunos pagos y una lectura inmediata puede no encontrar el documento actualizado.
- Esperado: el resync completo escribe los pagos de PostgreSQL con `source_event_id` como id estable, espera visibilidad y `/api/search` puede leerlos.
- Reproduccion: `pytest -q tests/test_intermediate_i3_elasticsearch.py` con Elasticsearch healthy.
- Starting Point: `sync_to_elasticsearch` y `search_elasticsearch`.
- Acceptance: un pago `FAILED` persistido aparece en ES, la sincronizacion es repetible/idempotente y la lectura devuelve el documento esperado.

Hint 1: compara los estados que selecciona PostgreSQL con los estados que necesita la proyeccion.

Hint 2: una respuesta HTTP 200 de indexacion no siempre implica que una busqueda inmediata vea el documento.

Hint 3: usa `source_event_id` como `_id` y un resync completo sencillo antes de introducir colas o arquitectura distribuida.

## Guided debugging

1. Arranca PostgreSQL y Elasticsearch y confirma sus healthchecks.
2. Ejecuta el test focalizado y anota esperado frente a actual.
3. Busca la primera divergencia, desde Pydantic/HTTP hasta SQL.
4. Formula una hipotesis falsable antes de editar.
5. Aplica el cambio minimo y ejecuta el test focalizado.
6. Ejecuta la suite completa y revisa que no cambie el contrato de los otros endpoints.
7. Para I2 compara expected/persisted por status antes de corregir datos.
8. Para I3 comprueba PostgreSQL, la respuesta de indexacion, el refresh y la lectura ES.
9. Explica una alternativa y un riesgo de produccion.

## Learning, Interview y Review

**Learning:** usa los hints, logs y breakpoints; no abras spoilers hasta tener una hipotesis.

**Interview:** respeta 20-60 minutos para Easy y 45-90 para Intermediate, explica los supuestos y muestra el test que prueba la causa. La documentacion del repositorio esta disponible.

**Review:** presenta el diff, la evidencia RED/GREEN/RED baseline, la frontera de validacion, la garantia de idempotencia, la reconciliacion SQL, la consistencia de la proyeccion ES y una alternativa justificada.

## Tests como evidencia

Los tests son reproduccion, acceptance criteria y regresion. Los tres rojos estan documentados y son intencionales. No cambies expectativas para ocultar el defecto ni uses una respuesta fija para pasar el reporte.

## Mentor / AI spoilers

<details>
<summary>Abrir solo para mentoring, review o solucion explicita</summary>

### Root causes verificadas

- E1: `PaymentRecord.amount` admite `None` y la ingesta convierte el valor ausente en `Decimal("0.00")`.
- E2: no hay comprobacion durable ni restriccion unica para `source_event_id`; cada replay ejecuta otro `insert`.
- E3: el `select` filtra por `merchant_id` pero descarta `status`, por lo que count y sum incluyen todos los estados.
- I1: la persistencia incremental distingue duplicados correctamente, pero el response usa la cantidad de registros validos como `inserted`.
- I2: la consulta de estados persistidos hereda un filtro `CAPTURED` y omite estados como `FAILED`.
- I3: la sincronizacion selecciona solo `CAPTURED` y no espera el refresh de Elasticsearch, por lo que un pago valido puede quedar ausente o stale en la lectura.

### Soluciones temporales validadas

Se aplicaron temporalmente las tres correcciones y la suite alcanzo `12 passed`:

- E1: `amount` requerido y estrictamente mayor que cero mediante Pydantic; sin conversion de ausencia a cero.
- E2: ignorar eventos ya existentes por `source_event_id`; en una solucion de produccion, respaldar la regla con constraint unica y manejo de conflicto.
- E3: anadir `Payment.status == status` al `where` de la agregacion.
- I1: devolver el contador real de filas nuevas (`inserted`), manteniendo `duplicated` separado.
- I2: agrupar todos los estados persistidos y comparar count, total y status.
- I3: hacer resync de todos los pagos, usar `source_event_id` como `_id` y escribir con `refresh=wait_for`; repetir el resync es seguro.

Despues se restauraron los seis defects y se verifico `6 failed, 9 passed`.

### Errores comunes

- aceptar una fila invalida y corregirla con un importe inventado;
- deduplicar solo en una lista Python;
- filtrar el resultado despues de una agregacion ya mezclada;
- cambiar el esperado del test deliberado;
- convertir dinero a float.
- reconciliar sobrescribiendo datos sin conservar la evidencia de la discrepancia;
- sincronizar solo los pagos capturados o asumir que indexar equivale a ser visible en una busqueda inmediata;
- introducir una cola distribuida para resolver un resync local.

</details>

## Troubleshooting

| Sintoma                  | Primera comprobacion                    | Interpretacion                                  |
| ------------------------ | --------------------------------------- | ----------------------------------------------- |
| Compose no resuelve      | `docker compose config`                 | error de infraestructura/configuracion          |
| PostgreSQL no conecta    | `docker compose ps` y logs              | espera a `healthy` antes de depurar la app      |
| Alembic falla            | logs de `app` o `test`                  | migracion o entorno, no ticket Easy             |
| Elasticsearch no healthy | logs y `docker compose ps`              | servicio local; no hay busqueda que depurar aun |
| API no responde          | puerto `18086`, logs de `app`           | startup/puerto antes que dominio                |
| pytest tiene mas rojos   | ejecuta suite limpia en `postgres-test` | posible regresion accidental                    |

## Validation matrix

| Area              | Comando/evidencia                             | Resultado verificado                                   |
| ----------------- | --------------------------------------------- | ------------------------------------------------------ |
| Compose           | `docker compose config`                       | valido                                                 |
| PostgreSQL        | healthcheck Compose                           | healthy                                                |
| Alembic           | `alembic upgrade head` en app/test            | revision `0001` aplicada                               |
| Elasticsearch     | healthcheck `_cluster/health`                 | healthy, single-node                                   |
| FastAPI           | logs de `app`                                 | startup complete                                       |
| Health            | `curl http://localhost:18086/health`          | `status=ok` y URL interna de Elasticsearch verificados |
| Ingestion         | `POST /api/ingest`                            | dataset reproducible; baseline E1 visible              |
| Incremental I1    | `POST /api/ingest/incremental`                | replay mixto y conteos separados                       |
| Reconciliation I2 | `POST /api/reconcile`                         | expected/persisted por status y discrepancias          |
| Sync I3           | `POST /api/sync`, `GET /api/search`           | PostgreSQL proyectado y lectura ES                     |
| Baseline          | `docker compose --profile test run --rm test` | 15 = 9 PASS + 6 FAIL deliberados                       |
| Verde temporal    | misma suite con E1/E2/E3/I1/I2/I3 corregidos  | 15 PASS                                                |
| End-to-end        | health, ingest, sync, search y report         | `5 accepted / 1 rejected`, `synced=5`, `150.00`        |
| Restauracion      | misma suite tras revertir correcciones        | 9 PASS + 6 FAIL deliberados                            |

## PENDING - Advanced

No se implementan ni se presentan como evidencia en esta fase:

- PostgreSQL reporting avanzado y query plans.
- retries concurrentes y garantias de entrega mas fuertes.
- reconciliation avanzada de grandes volumenes.
- Elasticsearch mappings, rendimiento y sincronizacion distribuida.
- Text/search issues avanzados, si procede.

## Agent Continuity

El alcance es exclusivamente `lab-06-python-data-engineering/**`. El siguiente agente debe conservar PostgreSQL sin puerto host, API en `127.0.0.1:18086`, Elasticsearch sin puerto host y el baseline de 15 tests con 9 PASS y E1/E2/E3/I1/I2/I3 rojos. Para resolver un ticket, abrir su test focalizado, validar temporalmente, ejecutar la suite verde y restaurar los defects. Advanced permanece PENDING y no debe inventarse evidencia.

## Checkpoint

**LAB-06 INTERMEDIATE - COMPLETE**

- STACK
  - Python/FastAPI: Python 3.12.11, FastAPI 0.116.1, Pydantic 2.11.7.
  - PostgreSQL/Alembic: PostgreSQL 17.5-alpine, SQLAlchemy 2.0.43, Alembic 1.16.4.
  - Elasticsearch: 9.1.2 single-node, heap 256 MB, healthcheck validado.
  - Docker: Compose v5.5.0, imagen Python fijada.
- EASY
  - E1: validacion de fila sin amount, deliberadamente FAIL.
  - E2: replay duplica source_event_id, deliberadamente FAIL.
  - E3: agregacion ignora status, deliberadamente FAIL.
- INTERMEDIATE
  - I1: replay mixto informa inserts incorrectos, deliberadamente FAIL.
  - I2: reconciliacion omite un status persistido, deliberadamente FAIL.
  - I3: Elasticsearch queda stale/no visible para un pago FAILED, deliberadamente FAIL.
- BASELINE
  - tests: 15.
  - PASS: 9.
  - deliberate FAIL: 6.
  - accidental FAIL: 0.
- TEMPORARY GREEN
  - tests: 15.
  - PASS: 15.
- RUNTIME
  - API: contenedor arrancado y startup complete; endpoint `/health` probado por suite.
  - ingestion: dataset versionado y endpoint reproducible.
  - PostgreSQL: healthy y migrado.
  - Elasticsearch: healthy, sin puerto publicado y consultado tras sync real.
  - PostgreSQL reconciliation: expected/persisted por status y total verificados.
  - end-to-end: dataset -> FastAPI -> PostgreSQL -> Elasticsearch -> search/report verificado.
- README / CONTINUITY
  - hints: 1/2/3 por ticket.
  - modes: Learning, Interview, Review.
  - mentor context: spoilers con causas y soluciones temporales verificadas.
  - validation matrix: incluida.
  - Agent Continuity: incluida.
- PENDING ADVANCED
  - PostgreSQL reporting avanzado, retries concurrentes, reconciliation a gran escala, mappings/rendimiento ES y text/search avanzado.
- GIT
  - commit: pendiente de turno Git.
  - owned paths clean: pendiente de comprobacion final; no se tocaron README raiz, docs globales ni otros Labs.
