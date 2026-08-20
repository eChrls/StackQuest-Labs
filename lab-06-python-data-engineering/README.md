# Lab-06 - Python/FastAPI + PostgreSQL + ETL + Elasticsearch Data Engineering

Lab Docker-first de ingesta y reporting de pagos. El estado candidato contiene ocho defectos deliberados e independientes repartidos en Easy, Intermediate y Advanced. El objetivo es ejecutar, observar, formular una hipotesis, corregir la causa minima y verificarla con evidencia.

## Identidad y objetivos

- Dificultad: Easy, Intermediate y Advanced implementados.
- Tiempo orientativo Easy: 20-60 minutos; Intermediate: 45-90 minutos; Advanced: 60-180 minutos.
- Dominio: pequenos eventos de pagos con calidad de datos imperfecta, mas reporting agregado y recuperacion ante fallos parciales a escala.
- Resultados: validar registros, persistir en PostgreSQL, repetir ingestas de forma segura, calcular reportes por merchant/status, escalar reportes agregados sobre un dataset grande con indices correctos y recuperar una proyeccion Elasticsearch tras un fallo parcial sin duplicar ni dejar datos stale.

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

Elasticsearch participa en la sincronizacion/resync de Intermediate y en la reconciliacion/repair de Advanced (A2). PostgreSQL participa ademas en el reporting agregado a escala de Advanced (A1).
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

Hay 17 tests: 9 PASS y 8 FAIL deliberados. No hay fallos accidentales.

| Ticket | Test                                                                | Resultado baseline | Sintoma                                                          |
| ------ | -------------------------------------------------------------------- | ------------------- | ----------------------------------------------------------------- |
| E1     | `test_e1_invalid_dataset_row_is_rejected`                             | FAIL deliberado      | falta `amount` y la fila termina persistida                       |
| E2     | `test_e2_reingesting_same_events_is_idempotent`                       | FAIL deliberado      | el replay inserta otra vez los mismos eventos                     |
| E3     | `test_e3_report_aggregates_only_requested_status`                     | FAIL deliberado      | el reporte mezcla statuses                                        |
| I1     | `test_i1_mixed_replay_inserts_only_new_events`                        | FAIL deliberado      | el replay mixto informa demasiados inserts                        |
| I2     | `test_i2_reconciliation_reports_status_and_total_mismatch`            | FAIL deliberado      | falta una discrepancia de status persistido                       |
| I3     | `test_i3_postgres_payment_is_readable_from_elasticsearch`             | FAIL deliberado      | PostgreSQL se actualiza pero ES queda stale                       |
| A1     | `test_a1_leaderboard_report_scales_with_index_and_single_query`       | FAIL deliberado      | el reporte hace una query por merchant y escanea la tabla entera  |
| A2     | `test_a2_partial_sync_failure_recovers_without_duplication_or_stale_data` | FAIL deliberado   | la reconciliacion nunca detecta una proyeccion ES desactualizada  |

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

## Advanced track

### A1 - PostgreSQL reporting performance

**Type:** SQL / Data / Production incident
**Suggested interview time:** 90-120 min

**Context:** finance necesita un leaderboard de los merchants con mas importe capturado en una ventana de fechas, sobre la tabla `payments` a escala de produccion (cientos de miles de filas, no el dataset de cinco registros del resto del Lab).

**Observed behaviour:** `GET /api/reports/merchant-leaderboard` devuelve el resultado correcto, pero primero obtiene todos los `merchant_id` distintos y despues ejecuta una consulta agregada adicional por cada uno (un patron N+1 clasico). Ademas, incluso una unica consulta agregada bien escrita hace un `Seq Scan` completo de la tabla porque no existe un indice que sirva al filtro `status` + rango de `created_at`.

**Expected behaviour:** el endpoint ejecuta una unica consulta agregada (`GROUP BY merchant_id` con `ORDER BY` y `LIMIT`), y esa consulta usa un indice para el filtro en lugar de recorrer toda la tabla.

**Reproduction:** `pytest -q tests/test_advanced_a1_reporting_performance.py` dentro del servicio test. El test siembra ~200.000 filas sinteticas con `generate_series` (rapido y determinista, sin depender de Python) y comprueba dos cosas de forma independiente:

1. cuenta las consultas SQL reales ejecutadas por el endpoint durante una peticion (debe ser 1, no una por merchant);
2. ejecuta `EXPLAIN (ANALYZE, FORMAT JSON)` sobre la consulta agregada filtrada por `status` + rango de fechas y comprueba que ningun nodo del plan sea `Seq Scan` sobre `payments`.

**Constraints:** no reescribas el dataset de negocio (`app/data/payments.json`) ni el resto de endpoints; el dataset grande es exclusivo de este test/benchmark.

**Acceptance criteria:** una unica query agregada por peticion; el plan de ejecucion de la consulta filtrada usa un indice (no `Seq Scan`); el resultado (merchant, count, total) es identico al de la version N+1.

Hint 1: cuenta cuantas consultas SQL reales dispara una peticion al endpoint, no solo si la respuesta es correcta.

Hint 2: la cardinalidad de `status` por si sola es baja (25% de las filas); combinada con un rango de fechas estrecho, la selectividad real es mucho mayor. Piensa que columnas debe cubrir el indice y en que orden (igualdad antes que rango).

Hint 3: `EXPLAIN (ANALYZE, BUFFERS)` te dice si Postgres esta leyendo la tabla entera o solo las paginas relevantes; compara los buffers antes y despues del indice.

### A2 - Recovery / consistency under failure

**Type:** Production incident / Data
**Suggested interview time:** 90-150 min

**Context:** un job de sincronizacion escribe pagos confirmados en PostgreSQL hacia Elasticsearch. El job puede fallar a mitad de camino (timeout, restart del worker, error de red) dejando algunos documentos sin indexar; ademas, una correccion posterior sobre un pago ya sincronizado (ej. un ajuste de status) no siempre se propaga, dejando un documento indexado pero desactualizado.

**Observed behaviour:** `POST /api/elasticsearch/reconcile` compara cada pago de PostgreSQL contra Elasticsearch, pero solo detecta documentos ausentes (`missing`). Un documento que existe en ES con datos desactualizados nunca aparece en `stale`, asi que `POST /api/elasticsearch/repair` nunca lo corrige: la recuperacion "declara exito" y dice `skipped` sobre un pago con datos incorrectos.

**Expected behaviour:** `reconcile` detecta tanto documentos ausentes como documentos presentes cuyo contenido difiere del registro actual en PostgreSQL (`stale`); `repair` reindexa todos los `missing` + `stale`; repetir `repair` sobre un estado ya reparado es un no-op seguro (no duplica, no reindexa de mas); una busqueda posterior devuelve el dato corregido.

**Reproduction:** `pytest -q tests/test_advanced_a2_recovery.py` con Elasticsearch healthy. El test simula un fallo parcial: un pago indexado correctamente, un pago nunca sincronizado (`missing`) y un pago indexado con un snapshot antiguo que ya no coincide con PostgreSQL (`stale`). Llama `reconcile` (debe listar el missing y el stale), `repair` (debe reparar ambos y dejar el ya-correcto intacto), `reconcile` otra vez (debe quedar en cero) y `repair` otra vez (debe ser un no-op), y termina comprobando `GET /api/search` para el pago antes stale.

**Constraints:** no introduzcas una cola de mensajes, un segundo servicio ni orquestacion distribuida; la recuperacion es un resync dirigido, local y sincrono, sobre el mismo esquema PostgreSQL/Elasticsearch que ya usa I3.

**Acceptance criteria:** `reconcile` distingue `missing` de `stale`; `repair` corrige exactamente los pagos con drift y deja los correctos sin tocar (evidenciado en `skipped`); repetir la reconciliacion tras un repair da `missing=[]` y `stale=[]`; repetir el repair da `repaired=0`; la proyeccion ES coincide con PostgreSQL campo a campo tras la recuperacion.

Hint 1: "el documento existe" y "el documento esta actualizado" son dos comprobaciones distintas; revisa cual de las dos falta.

Hint 2: compara el documento indexado contra la misma representacion que usa `/api/sync` para escribir (`payment_document`), no solo su existencia.

Hint 3: idempotencia significa que reconciliar y reparar dos veces seguidas produce el mismo estado final que una vez; verifica el segundo `repair` explicitamente en tu prueba.

## Guided debugging

1. Arranca PostgreSQL y Elasticsearch y confirma sus healthchecks.
2. Ejecuta el test focalizado y anota esperado frente a actual.
3. Busca la primera divergencia, desde Pydantic/HTTP hasta SQL.
4. Formula una hipotesis falsable antes de editar.
5. Aplica el cambio minimo y ejecuta el test focalizado.
6. Ejecuta la suite completa y revisa que no cambie el contrato de los otros endpoints.
7. Para I2 compara expected/persisted por status antes de corregir datos.
8. Para I3 comprueba PostgreSQL, la respuesta de indexacion, el refresh y la lectura ES.
9. Para A1 cuenta las queries reales por peticion antes de mirar `EXPLAIN`; corrige primero el N+1 y despues el indice, y vuelve a leer el plan tras cada cambio.
10. Para A2 separa la deteccion (`reconcile`) de la correccion (`repair`); verifica primero que `missing` y `stale` sean correctos antes de tocar el codigo de reparacion.
11. Explica una alternativa y un riesgo de produccion.

## Learning, Interview y Review

**Learning:** usa los hints, logs y breakpoints; no abras spoilers hasta tener una hipotesis.

**Interview:** respeta 20-60 minutos para Easy, 45-90 para Intermediate y 60-180 para Advanced; explica los supuestos y muestra el test o la evidencia (`EXPLAIN`, reconcile/repair) que prueba la causa. La documentacion del repositorio esta disponible.

**Review:** presenta el diff, la evidencia RED/GREEN/RED baseline, la frontera de validacion, la garantia de idempotencia, la reconciliacion SQL, la consistencia de la proyeccion ES, el plan de ejecucion antes/despues del indice en A1, la recuperacion antes/despues en A2 y una alternativa justificada.

## Tests como evidencia

Los tests son reproduccion, acceptance criteria y regresion. Los ocho rojos estan documentados y son intencionales. No cambies expectativas para ocultar el defecto ni uses una respuesta fija para pasar el reporte.

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
- A1: `merchant_leaderboard` obtiene los `merchant_id` distintos y ejecuta una consulta agregada por cada uno (N+1); ademas no existe indice sobre `(status, created_at)`, asi que incluso una consulta agregada unica hace `Seq Scan` sobre la tabla completa.
- A2: `reconcile_elasticsearch_state` solo marca `missing` cuando el documento no existe en Elasticsearch; nunca compara el contenido del documento existente contra el pago actual en PostgreSQL, asi que un documento presente pero desactualizado nunca se marca `stale` y `repair` nunca lo corrige.

### Soluciones temporales validadas

Se aplicaron temporalmente las ocho correcciones y la suite alcanzo `17 passed` (verificado dos veces seguidas sobre los mismos contenedores, sin recrear volumenes, para confirmar que era reproducible):

- E1: `amount` requerido y estrictamente mayor que cero mediante Pydantic; sin conversion de ausencia a cero.
- E2: ignorar eventos ya existentes por `source_event_id`; en una solucion de produccion, respaldar la regla con constraint unica y manejo de conflicto.
- E3: anadir `Payment.status == status` al `where` de la agregacion.
- I1: devolver el contador real de filas nuevas (`inserted`), manteniendo `duplicated` separado.
- I2: agrupar todos los estados persistidos y comparar count, total y status.
- I3: hacer resync de todos los pagos, usar `source_event_id` como `_id` y escribir con `refresh=wait_for`; repetir el resync es seguro.
- A1: reescribir el endpoint como una unica consulta `GROUP BY merchant_id` con `ORDER BY sum(amount) DESC LIMIT :limit`, y anadir una migracion Alembic con un indice compuesto `(status, created_at)` (igualdad antes que rango, siguiendo la convencion de indices B-tree).
- A2: comparar el documento indexado contra `payment_document(payment)` y anadir a `stale` cuando difieren; `repair` ya reindexaba `missing | stale`, asi que no necesita cambios ahi.

Despues se restauraron los ocho defects (incluyendo eliminar la migracion del indice compuesto) y se verifico `8 failed, 9 passed`, reproducible en dos ejecuciones consecutivas.

#### Evidencia real A1 (PostgreSQL, `EXPLAIN (ANALYZE, BUFFERS)`)

Dataset: 200.000 filas sembradas con `generate_series` (400 merchants, 4 statuses uniformes, fechas repartidas en 397 dias), `ANALYZE payments` ejecutado antes de cada plan. Consulta: `status='CAPTURED'` + rango de 10 dias (~1260 filas relevantes de 200.000, selectividad real ~0.63%).

Antes del indice (`Seq Scan`, sin indice sobre `status, created_at`):

```
Parallel Seq Scan on payments (actual time=0.084..18.512 rows=630 loops=2)
  Filter: (created_at >= ... AND created_at < ... AND status = 'CAPTURED')
  Rows Removed by Filter: 99370
  Buffers: shared hit=2062
Execution Time: 31.493 ms
```

Despues de `CREATE INDEX ix_payments_status_created_at ON payments (status, created_at)` + `ANALYZE`:

```
Bitmap Heap Scan on payments (actual time=2.551..11.691 rows=1260 loops=1)
  Recheck Cond: (status = 'CAPTURED' AND created_at >= ... AND created_at < ...)
  ->  Bitmap Index Scan on ix_payments_status_created_at (actual time=2.430..3.195 rows=1260 loops=1)
  Buffers: shared hit=537 read=4
Execution Time: 16.490 ms
```

El plan pasa de `Parallel Seq Scan` (2062 buffers, recorre toda la tabla) a `Bitmap Heap Scan` sobre el indice (541 buffers, ~4x menos paginas leidas); la mejora de buffers es la evidencia estable (no depende del hardware), el tiempo de ejecucion es evidencia secundaria. El endpoint corregido (`GET /api/reports/merchant-leaderboard?status=CAPTURED&date_from=2024-06-01T00:00:00Z&date_to=2024-06-11T00:00:00Z&limit=5`) devolvio el leaderboard correcto en una sola query, ej. `M-BENCH-0045` con `count=14`, `total=4101.00`.

#### Evidencia real A2 (fallo parcial -> recuperacion -> consistencia)

Con el dataset base ingerido (`evt-100..104`) se simulo un sync que indexo `evt-100` correctamente y dejo `evt-102` con un snapshot antiguo (`status=PENDING`, cuando PostgreSQL ya tiene `status=FAILED`), sin indexar `evt-101`, `evt-103`, `evt-104`:

```
POST /api/elasticsearch/reconcile -> {"checked": 5, "missing": ["evt-101","evt-103","evt-104"], "stale": ["evt-102"]}
POST /api/elasticsearch/repair    -> {"repaired": 4, "skipped": 1}
POST /api/elasticsearch/reconcile -> {"checked": 5, "missing": [], "stale": []}
POST /api/elasticsearch/repair    -> {"repaired": 0, "skipped": 5}
GET  /api/search?source_event_id=evt-102 -> {"count": 1, "payments": [{"status": "FAILED", "amount": "30.00", ...}]}
```

`skipped=1` en el primer repair confirma que `evt-100` (ya correcto) no se reescribio de mas; el segundo `repair` es un no-op (`repaired=0`); la busqueda final devuelve exactamente un documento con los datos corregidos, sin duplicados.

### Errores comunes

- aceptar una fila invalida y corregirla con un importe inventado;
- deduplicar solo en una lista Python;
- filtrar el resultado despues de una agregacion ya mezclada;
- cambiar el esperado del test deliberado;
- convertir dinero a float.
- reconciliar sobrescribiendo datos sin conservar la evidencia de la discrepancia;
- sincronizar solo los pagos capturados o asumir que indexar equivale a ser visible en una busqueda inmediata;
- introducir una cola distribuida para resolver un resync local;
- "arreglar" A1 anadiendo solo el indice sin quitar el bucle N+1 (o al reves): el ticket exige ambos, y el test los comprueba por separado;
- crear un indice sobre `status` solo, o sobre `(created_at, status)`: el orden importa para un predicado de igualdad + rango;
- en A2, marcar `stale` comparando solo el `amount` o solo el `status` en vez de comparar el documento completo, dejando pasar otras divergencias (`merchant_id`, `currency`, `created_at`);
- en A2, hacer que `repair` reindexe siempre todos los pagos (un resync completo) en vez de solo `missing | stale`: funciona pero no demuestra recuperacion dirigida y no escala.

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
| A1 tarda varios segundos | revisa que el `INSERT ... generate_series` termine antes del `EXPLAIN` | esperado; sembrar 200k filas via SQL es rapido, pero no instantaneo |
| A2 falla con `missing` vacio inesperadamente | Elasticsearch no se limpia entre ejecuciones de pytest como si hace `postgres-test` | el propio test borra los ids conocidos al inicio; si anades datos nuevos, limpia el indice o usa ids unicos |

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
| Reporting A1      | `GET /api/reports/merchant-leaderboard` + `EXPLAIN (ANALYZE, BUFFERS)` en `postgres` | una query agregada; `Seq Scan` (2062 buffers) -> `Bitmap Heap Scan` sobre `ix_payments_status_created_at` (541 buffers) tras el indice |
| Recovery A2       | `POST /api/elasticsearch/reconcile`, `POST /api/elasticsearch/repair`, `GET /api/search` | fallo parcial detectado (`missing`+`stale`), reparado (`repaired=4, skipped=1`), replay idempotente (`repaired=0`), dato final consistente |
| Baseline          | `docker compose --profile test run --rm test` | 17 = 9 PASS + 8 FAIL deliberados, reproducible en 2 ejecuciones seguidas |
| Verde temporal    | misma suite con los 8 defects corregidos      | 17 PASS, reproducible en 2 ejecuciones seguidas        |
| End-to-end        | health, ingest, sync, search, report, leaderboard, reconcile/repair | `5 accepted / 1 rejected`, `synced=5`, `150.00`, leaderboard sobre 200k filas, recovery evt-101/102/103/104 |
| Restauracion      | misma suite tras revertir correcciones y borrar la migracion del indice | 9 PASS + 8 FAIL deliberados, reproducible en 2 ejecuciones seguidas |

## Agent Continuity

El alcance es exclusivamente `lab-06-python-data-engineering/**`. El siguiente agente debe conservar PostgreSQL sin puerto host, API en `127.0.0.1:18086`, Elasticsearch sin puerto host y el baseline de 17 tests con 9 PASS y E1/E2/E3/I1/I2/I3/A1/A2 rojos. Para resolver un ticket, abrir su test focalizado, validar temporalmente, ejecutar la suite verde y restaurar los defects (incluyendo eliminar cualquier migracion Alembic temporal creada solo para validar A1: la migracion `0002` con el indice compuesto NO debe existir en el repositorio, es evidencia temporal, no una solucion permanente). Elasticsearch no se limpia entre ejecuciones de pytest: los tests que dependen de ids fijos (A2) deben borrar sus propios documentos al inicio para seguir siendo reproducibles sobre contenedores calientes. Lab-06 esta completo: Easy, Intermediate y Advanced tienen baseline rojo documentado, correccion temporal verificada y evidencia real capturada.

## Checkpoint

**LAB-06 ADVANCED - COMPLETE**

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
- ADVANCED
  - A1
    - scenario: leaderboard de merchants por importe capturado sobre ~200.000 filas; resultado correcto pero N+1 (una query por merchant) y sin indice para el filtro `status`+rango de fechas.
    - baseline evidence: `test_a1_leaderboard_report_scales_with_index_and_single_query` FAIL deliberado (401 queries en vez de 1; plan usa `Seq Scan`).
    - EXPLAIN/ANALYZE before: `Parallel Seq Scan on payments`, `Buffers: shared hit=2062`, `Execution Time: 31.493 ms`, `Rows Removed by Filter: 99370` (x2 workers).
    - temporary solution: endpoint reescrito a una unica query `GROUP BY merchant_id` + migracion Alembic temporal con indice compuesto `(status, created_at)`.
    - EXPLAIN/ANALYZE after: `Bitmap Heap Scan on payments` sobre `Bitmap Index Scan on ix_payments_status_created_at`, `Buffers: shared hit=537 read=4`, `Execution Time: 16.490 ms`.
    - acceptance: 1 query por peticion (verificado con contador de queries reales) + plan sin `Seq Scan` sobre `payments`; mismo resultado (merchant, count, total) en ambas versiones.
  - A2
    - failure scenario: sync que confirma en PostgreSQL, indexa dos pagos en Elasticsearch (uno correcto, uno con snapshot antiguo tras una correccion posterior en PostgreSQL) y deja tres pagos sin indexar (crash a mitad de camino).
    - inconsistent state reproduced: `reconcile` baseline solo detecta los 3 `missing`; el pago `stale` (status desactualizado) nunca se reporta ni se repara.
    - recovery strategy: `reconcile` compara documento existente vs `payment_document(payment)` (no solo existencia) para clasificar `missing`/`stale`; `repair` reindexa `missing | stale` con `refresh=wait_for`, dejando el resto intacto (idempotente: repetir sobre estado sano da `repaired=0`).
    - recovery evidence: `reconcile` -> `{"checked":5,"missing":["evt-101","evt-103","evt-104"],"stale":["evt-102"]}`; `repair` -> `{"repaired":4,"skipped":1}`; `reconcile` tras repair -> `{"missing":[],"stale":[]}`; `repair` de nuevo -> `{"repaired":0,"skipped":5}`; `search evt-102` -> `status=FAILED, amount=30.00, count=1` (sin duplicados).
    - final consistency: PostgreSQL y Elasticsearch coinciden campo a campo tras un unico ciclo reconcile+repair; el replay es un no-op seguro.
- SUITE
  - total tests: 17.
  - PASS: 9.
  - deliberate FAIL: 8 (E1, E2, E3, I1, I2, I3, A1, A2).
  - accidental FAIL: 0.
  - temporary green: 17 PASS, verificado en 2 ejecuciones consecutivas sobre los mismos contenedores (sin recrear volumenes) para confirmar reproducibilidad.
- RUNTIME
  - API: contenedor arrancado y startup complete; endpoint `/health` probado por suite.
  - ingestion: dataset versionado y endpoint reproducible.
  - PostgreSQL: healthy y migrado (revision `0001`; la migracion temporal `0002` del indice se creo, valido y elimino, no queda en el repositorio).
  - Elasticsearch: healthy, sin puerto publicado y consultado tras sync/reconcile/repair real.
  - PostgreSQL reconciliation: expected/persisted por status y total verificados.
  - PostgreSQL reporting a escala: 200.000 filas sembradas por SQL, `EXPLAIN (ANALYZE, BUFFERS)` capturado antes y despues del indice.
  - Elasticsearch recovery: fallo parcial simulado y recuperado con evidencia real de las 5 llamadas HTTP.
  - end-to-end: dataset -> FastAPI -> PostgreSQL -> Elasticsearch -> search/report/leaderboard/reconcile/repair verificado.
- README / CONTINUITY
  - hints: 1/2/3 por ticket, incluyendo A1/A2.
  - modes: Learning, Interview, Review.
  - mentor context: spoilers con causas, soluciones temporales y evidencia real (SQL plans, HTTP responses) verificadas.
  - validation matrix: incluida con filas A1/A2.
  - Agent Continuity: incluida y actualizada para Advanced.
- FINAL
  - baseline restored: si, verificado en 2 ejecuciones consecutivas (`8 failed, 9 passed` ambas veces).
  - Easy: E1/E2/E3 deliberadamente FAIL.
  - Intermediate: I1/I2/I3 deliberadamente FAIL.
  - Advanced: A1/A2 deliberadamente FAIL.
  - Agent Continuity: incluida.
  - Lab-06 100%: si, si toda la evidencia de este README se reproduce.
- GIT
  - files: `app/main.py`, `app/schemas.py` modificados; `tests/test_advanced_a1_reporting_performance.py`, `tests/test_advanced_a2_recovery.py` nuevos; `README.md` actualizado.
  - commit: `feat(lab-06): add advanced data engineering challenges`.
  - owned paths clean: si; no se tocaron README raiz, docs globales ni otros Labs.
