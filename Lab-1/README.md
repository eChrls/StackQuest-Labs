# Lab 1

## Contexto
Se incorpora a un pequeño backend de pagos existente.

## Objetivo
Investigar y resolver tickets sin reescribir el sistema.

## Entorno
Este laboratorio se ejecuta enteramente con Docker Compose. No necesitas Java, Maven ni PostgreSQL instalados localmente.

## Comandos

### Arrancar aplicación
```bash
docker compose up --build postgres app
```

### Ejecutar tests
```bash
docker compose --profile test up --build --abort-on-container-exit test
```

### Modo debug
```bash
docker compose --profile debug up --build postgres debug
```

JDWP: localhost:15005
HTTP: localhost:18081

### Ver logs
```bash
docker compose logs -f app
docker compose --profile debug logs -f debug
docker compose --profile test logs -f test
```

### Entrar en PostgreSQL
```bash
docker compose exec postgres psql -U lab1 -d lab1
```

## Tickets

### Ticket 0 — Arranque y orientación
El candidato debe levantar la aplicación, localizar el entry point, identificar Controller/Service/Repository, ejecutar toda la suite y observar qué pasa y qué falla.

### Ticket 1
Síntoma: captured total de M1 debería ser 150.00 pero no lo es.

### Ticket 2
Síntoma: un pago PENDING válido provoca HTTP 500 al consultarlo.
Incluye el UUID seed necesario para reproducirlo: 55555555-5555-4555-8555-555555555555.

### Ticket 3
Síntoma: filtrar M1 por CAPTURED devuelve estados que no deberían aparecer.

### Ticket 4
Síntoma: la API acepta un payment con amount negativo.

### Ticket 5
Implementar /summary con el contrato indicado.
