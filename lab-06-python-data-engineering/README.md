# Lab-06 - Foundation + Easy Data Engineering

Lab Docker-first de ingesta y reporting de pagos. El estado candidato contiene tres defectos deliberados e independientes. El objetivo es ejecutar, observar, formular una hipotesis, corregir la causa minima y verificarla con evidencia.

## Identidad y objetivos

- Dificultad: Easy implementado; Intermediate y Advanced pendientes.
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

Elasticsearch: servicio preparado y comprobable; no participa aun en el flujo.
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

Hay 12 tests: 9 PASS y 3 FAIL deliberados. No hay fallos accidentales.

| Ticket | Test                                              | Resultado baseline | Sintoma                                       |
| ------ | ------------------------------------------------- | ------------------ | --------------------------------------------- |
| E1     | `test_e1_invalid_dataset_row_is_rejected`         | FAIL deliberado    | falta `amount` y la fila termina persistida   |
| E2     | `test_e2_reingesting_same_events_is_idempotent`   | FAIL deliberado    | el replay inserta otra vez los mismos eventos |
| E3     | `test_e3_report_aggregates_only_requested_status` | FAIL deliberado    | el reporte mezcla statuses                    |

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

## Guided debugging

1. Arranca PostgreSQL y Elasticsearch y confirma sus healthchecks.
2. Ejecuta el test focalizado y anota esperado frente a actual.
3. Busca la primera divergencia, desde Pydantic/HTTP hasta SQL.
4. Formula una hipotesis falsable antes de editar.
5. Aplica el cambio minimo y ejecuta el test focalizado.
6. Ejecuta la suite completa y revisa que no cambie el contrato de los otros endpoints.
7. Explica una alternativa y un riesgo de produccion.

## Learning, Interview y Review

**Learning:** usa los hints, logs y breakpoints; no abras spoilers hasta tener una hipotesis.

**Interview:** respeta 20-60 minutos para Easy, explica los supuestos y muestra el test que prueba la causa. La documentacion del repositorio esta disponible.

**Review:** presenta el diff, la evidencia RED/GREEN/RED baseline, la frontera de validacion, la garantia de idempotencia, el plan SQL y una alternativa justificada.

## Tests como evidencia

Los tests son reproduccion, acceptance criteria y regresion. Los tres rojos estan documentados y son intencionales. No cambies expectativas para ocultar el defecto ni uses una respuesta fija para pasar el reporte.

## Mentor / AI spoilers

<details>
<summary>Abrir solo para mentoring, review o solucion explicita</summary>

### Root causes verificadas

- E1: `PaymentRecord.amount` admite `None` y la ingesta convierte el valor ausente en `Decimal("0.00")`.
- E2: no hay comprobacion durable ni restriccion unica para `source_event_id`; cada replay ejecuta otro `insert`.
- E3: el `select` filtra por `merchant_id` pero descarta `status`, por lo que count y sum incluyen todos los estados.

### Soluciones temporales validadas

Se aplicaron temporalmente las tres correcciones y la suite alcanzo `12 passed`:

- E1: `amount` requerido y estrictamente mayor que cero mediante Pydantic; sin conversion de ausencia a cero.
- E2: ignorar eventos ya existentes por `source_event_id`; en una solucion de produccion, respaldar la regla con constraint unica y manejo de conflicto.
- E3: anadir `Payment.status == status` al `where` de la agregacion.

Despues se restauraron los tres defects y se verifico `3 failed, 9 passed`.

### Errores comunes

- aceptar una fila invalida y corregirla con un importe inventado;
- deduplicar solo en una lista Python;
- filtrar el resultado despues de una agregacion ya mezclada;
- cambiar el esperado del test deliberado;
- convertir dinero a float.

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

| Area           | Comando/evidencia                             | Resultado verificado                                                          |
| -------------- | --------------------------------------------- | ----------------------------------------------------------------------------- |
| Compose        | `docker compose config`                       | valido                                                                        |
| PostgreSQL     | healthcheck Compose                           | healthy                                                                       |
| Alembic        | `alembic upgrade head` en app/test            | revision `0001` aplicada                                                      |
| Elasticsearch  | healthcheck `_cluster/health`                 | healthy, single-node                                                          |
| FastAPI        | logs de `app`                                 | startup complete                                                              |
| Health         | `curl http://localhost:18086/health`          | pendiente de comprobacion manual en cada arranque; endpoint incluido en tests |
| Ingestion      | `POST /api/ingest`                            | dataset reproducible; baseline E1 visible                                     |
| Baseline       | `docker compose --profile test run --rm test` | 12 = 9 PASS + 3 FAIL deliberados                                              |
| Verde temporal | misma suite con E1/E2/E3 corregidos           | 12 PASS                                                                       |
| Restauracion   | misma suite tras revertir correcciones        | 9 PASS + 3 FAIL deliberados                                                   |

## PENDING - Intermediate

No se implementan ni se presentan como evidencia en esta fase:

- ETL incremental.
- retries/replay como challenge separado.
- reconciliation/data-quality mismatch.
- PostgreSQL reporting avanzado.
- Elasticsearch synchronization.
- Text/search issues, si procede.

## Agent Continuity

El alcance es exclusivamente `lab-06-python-data-engineering/**`. El siguiente agente debe conservar PostgreSQL sin puerto host, API en `127.0.0.1:18086`, Elasticsearch sin puerto host y el baseline de 12 tests con 9 PASS y E1/E2/E3 rojos. Para resolver Easy, abrir los tests focalizados, validar temporalmente, ejecutar la suite verde y restaurar los defects. Intermediate permanece PENDING y no debe inventarse evidencia de sus tickets.

## Checkpoint

**LAB-06 - PHASE 1 COMPLETE**

- STACK
  - Python/FastAPI: Python 3.12.11, FastAPI 0.116.1, Pydantic 2.11.7.
  - PostgreSQL/Alembic: PostgreSQL 17.5-alpine, SQLAlchemy 2.0.43, Alembic 1.16.4.
  - Elasticsearch: 9.1.2 single-node, heap 256 MB, healthcheck validado.
  - Docker: Compose v5.5.0, imagen Python fijada.
- EASY
  - E1: validacion de fila sin amount, deliberadamente FAIL.
  - E2: replay duplica source_event_id, deliberadamente FAIL.
  - E3: agregacion ignora status, deliberadamente FAIL.
- BASELINE
  - tests: 12.
  - PASS: 9.
  - deliberate FAIL: 3.
  - accidental FAIL: 0.
- TEMPORARY GREEN
  - tests: 12.
  - PASS: 12.
- RUNTIME
  - API: contenedor arrancado y startup complete; endpoint `/health` probado por suite.
  - ingestion: dataset versionado y endpoint reproducible.
  - PostgreSQL: healthy y migrado.
  - Elasticsearch: healthy y sin puerto publicado.
- README / CONTINUITY
  - hints: 1/2/3 por ticket.
  - modes: Learning, Interview, Review.
  - mentor context: spoilers con causas y soluciones temporales verificadas.
  - validation matrix: incluida.
  - Agent Continuity: incluida.
- PENDING INTERMEDIATE
  - ETL incremental, retries/replay, reconciliation mismatch, reporting PostgreSQL avanzado, sincronizacion Elasticsearch y text/search.
- GIT
  - commit: pendiente de turno Git.
  - owned paths clean: pendiente de comprobacion final; no se tocaron README raiz, docs globales ni otros Labs.
