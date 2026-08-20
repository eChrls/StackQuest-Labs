# Labs

Este repositorio contiene una colección de laboratorios independientes diseñados para practicar pruebas técnicas de desarrollo de software en entornos realistas.

Los laboratorios priorizan:

- comprensión de proyectos desconocidos;
- debugging;
- lectura y modificación de código existente;
- resolución de bugs;
- testing;
- interpretación de logs, stack traces y fallos de tests;
- refactoring;
- implementación de nuevas funcionalidades;
- adaptación a stacks diferentes;
- integración frontend/backend;
- bases de datos;
- mantenimiento de código legacy;
- razonamiento técnico.

El objetivo no es aprender Docker. Docker se utiliza para disponer de entornos reproducibles y portables. El alumno debe poder comprender conceptualmente la infraestructura y leer su configuración, mientras que la construcción, ejecución y operación de los contenedores puede realizarla un agente.

---

# Filosofía del workspace

Cada laboratorio:

- vive en su propio directorio `Lab-N`;
- es independiente del resto;
- dispone de su propio `compose.yml`;
- contiene sus propios runtimes y dependencias;
- utiliza sus propias bases de datos;
- utiliza redes y volúmenes Docker propios;
- no depende de instalaciones globales de Java, Maven, Node, PHP, Python, PostgreSQL, MySQL, Elasticsearch, etc.;
- contiene su propio `README.md` con arquitectura, Docker, dominio, objetivos, tickets, tests y contexto de continuidad;
- debe poder reconstruirse en otro equipo mediante Git + Docker;
- utiliza tests como parte del proceso de desarrollo y debugging;
- mantiene secretos locales fuera de Git.

Los laboratorios se ejecutan de forma independiente. Normalmente solo debe estar activo el laboratorio que se esté utilizando.

---

# Plan global

| Lab                                | Stack principal                                    | Enfoque                         | Tecnologías / problemas principales                                                                                                                                |
| ---------------------------------- | -------------------------------------------------- | ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Lab-1 — Backend Java Debugging** | Java 21 + Spring Boot + Maven + PostgreSQL         | Debugging + comprensión + fixes | Proyecto desconocido, Spring, REST, `BigDecimal`, nulls, Collections, logs, stack traces, debugger, PostgreSQL básico, unit/integration tests                      |
| **Lab-2 — Java Legacy Challenge**  | Java 21 + Spring Boot + PostgreSQL                 | Legacy + refactoring + TDD      | Código heredado, spaghetti code controlado, métodos grandes, strings mágicos, coupling, side effects, Mockito, tests de caracterización, refactor incremental, TDD |
| **Lab-3 — Full Stack principal**   | React + TypeScript + Java/Spring Boot + PostgreSQL | Integración frontend/backend    | React, HTTP, DTOs, validación, JPA, SQL, async, errores entre capas, debugging navegador/backend, tests frontend/backend                                           |
| **Lab-4 — TypeScript Stack**       | Angular + TypeScript + Node.js/TypeScript + NestJS | Adaptación a otro stack         | Angular, NestJS, DI, controllers/services, Promises, async, APIs, errores runtime, debugging Node/browser y tests                                                  |
| **Lab-5 — Web Stack alternativo**  | Vue 3 + TypeScript + PHP/Laravel + MySQL           | Mantenimiento + adaptación      | Laravel MVC, ORM, Vue, formularios, validación, API, código existente, debugging PHP/JS y tests                                                                    |
| **Lab-6 — Python / Data**          | Python + FastAPI + PostgreSQL + Elasticsearch      | Backend + Data/ETL              | FastAPI, Python, SQL, ETL, reporting, datos duplicados/perdidos, jobs, retries, idempotencia, Elasticsearch, sincronización y debugging                            |

Los Labs 1, 2 y 3 tienen mayor peso porque Java + Spring Boot constituye el stack backend principal de la preparación.

Los Labs 4, 5 y 6 buscan además comprobar la capacidad de trasladar fundamentos de programación, debugging y arquitectura a tecnologías menos familiares.

---

# Docker-first

El host necesita principalmente:

```text
Git
Docker Engine / Docker Desktop
Docker Compose
```

Los runtimes específicos pertenecen a cada laboratorio.

Ejemplo:

```text
Repositorio Git
      │
      ├── Lab-1
      │    ├── Dockerfile
      │    ├── compose.yml
      │    └── ...
      │
      ├── Lab-2
      │    └── ...
      │
      └── Lab-N
           └── ...
```

Al clonar el repositorio en otro equipo, Docker debe poder descargar/construir los componentes necesarios y reconstruir el estado inicial del laboratorio.

Los datos iniciales necesarios para los challenges deben generarse mediante mecanismos reproducibles como migraciones y seeds. No se depende de copiar volúmenes Docker entre ordenadores.

---

# Visión global de contenedores

La composición concreta pertenece al `compose.yml` de cada Lab.

Esta es la arquitectura general prevista:

| Lab       | Aplicación / runtimes                | Base de datos / infraestructura | Test / Debug                                        |
| --------- | ------------------------------------ | ------------------------------- | --------------------------------------------------- |
| **Lab-1** | `app` — Spring Boot                  | `postgres`                      | `test`, `postgres-test`, `debug`                    |
| **Lab-2** | `app` — Spring Boot legacy           | `postgres`                      | `test`, `postgres-test`, `debug`                    |
| **Lab-3** | backend Spring Boot + frontend React | PostgreSQL                      | servicios/perfiles específicos de tests y debugging |
| **Lab-4** | backend NestJS + frontend Angular    | DB definida por el propio Lab   | servicios/perfiles específicos de tests y debugging |
| **Lab-5** | backend Laravel + frontend Vue       | MySQL                           | servicios/perfiles específicos de tests y debugging |
| **Lab-6** | FastAPI                              | PostgreSQL + Elasticsearch      | servicios/perfiles específicos de tests y debugging |

Los nombres exactos de servicios de los Labs 3-6 se definirán cuando se construya cada laboratorio. Esta tabla representa únicamente su arquitectura prevista.

---

# Patrón de los Labs Java

Lab-1 y Lab-2 utilizan aproximadamente:

```text
Host
│
├── HTTP localhost
│       ↓
│      app
│       ↓
│    postgres
│
├── JDWP localhost
│       ↓
│     debug
│
└── profile test
        │
        ├── test
        │    ↓
        └── postgres-test
```

Desarrollo y tests utilizan bases de datos independientes.

PostgreSQL no necesita exponerse directamente al host.

---

# Servicios Docker

Según el laboratorio pueden aparecer varios tipos de servicio.

## Aplicación

Ejemplos:

```text
app
backend
frontend
```

Contienen los runtimes y dependencias correspondientes.

### Base de datos

Ejemplos:

```text
postgres
mysql
```

Permanecen normalmente dentro de la red Docker del Lab.

### Base de datos de tests

Ejemplo:

```text
postgres-test
```

Permite ejecutar integration tests sin modificar los datos utilizados por la aplicación normal.

### Tests

Ejemplo:

```text
test
```

Ejecuta la suite utilizando el runtime y dependencias del propio laboratorio.

### Debug

Ejemplo:

```text
debug
```

Arranca la aplicación preparada para conectar un debugger desde el IDE.

### Infraestructura especializada

Solo cuando el laboratorio la necesita.

Ejemplo en Lab-6:

```text
elasticsearch
```

No existe infraestructura global compartida entre todos los Labs.

---

# Persistencia

Cada laboratorio puede crear sus propios volúmenes.

Por ejemplo:

```text
Lab-1
├── postgres_data
└── maven_cache

Lab-2
├── postgres_data
└── maven_cache
```

Aunque los nombres lógicos sean similares, Docker Compose los mantiene asociados a proyectos diferentes.

Los volúmenes generados durante la práctica no forman parte del repositorio Git.

El estado reproducible inicial debe proceder del código, configuración, migraciones y seeds versionados.

---

# Tests

Los tests forman parte integral de los laboratorios.

No se utilizan únicamente como comprobación final.

Pueden servir para:

```text
ejecutar test
      ↓
observar fallo
      ↓
leer assertion / stack trace / logs
      ↓
reproducir
      ↓
debuggear
      ↓
localizar causa
      ↓
corregir
      ↓
volver a ejecutar
```

Algunos laboratorios comienzan deliberadamente con tests fallando.

Esos fallos forman parte del challenge.

---

# Debugging

El debugging es una competencia transversal a todos los Labs.

Según el stack se practicarán herramientas diferentes:

| Stack                   | Herramientas principales                                                    |
| ----------------------- | --------------------------------------------------------------------------- |
| Java / Spring           | debugger JVM, breakpoints, call stack, variables, stack traces, tests, logs |
| JavaScript / TypeScript | debugger Node, DevTools del navegador, network, console, tests              |
| PHP                     | logs, stack traces, debugger cuando proceda, tests                          |
| Python                  | debugger Python, breakpoints, exceptions, logging, tests                    |
| PostgreSQL              | queries de diagnóstico, datos, logs y posteriormente `EXPLAIN`              |
| Elasticsearch           | queries, mappings, documentos, indexación y sincronización                  |

Docker no sustituye al debugger: proporciona el entorno donde se ejecuta la aplicación.

---

# Baseline de cada laboratorio

No se mantienen copias paralelas denominadas `initial-lab` como mecanismo normal de recuperación.

El baseline de un laboratorio es el commit publicado que contiene su challenge inicial.

Por ejemplo:

```text
GitHub
  ↓
commit inicial Lab-2
  ↓
20 tests
16 passing
4 failing deliberadamente
```

Ese commit permite recuperar el laboratorio original aunque posteriormente se modifique durante la práctica.

No deben publicarse versiones resueltas, backups o patches que revelen las soluciones.

---

# Documentación de cada Lab

Cada `Lab-N/README.md` debe permitir comprender el laboratorio incluso después de varios días sin trabajar en él.

Debe incluir como mínimo:

- objetivo;
- stack y versiones;
- arquitectura;
- estructura;
- infraestructura Docker;
- servicios y profiles;
- puertos;
- bases de datos;
- comandos operativos;
- dominio;
- endpoints;
- datos seed;
- estado inicial de tests;
- tickets;
- bugs deliberados descritos únicamente por sus síntomas;
- criterios de finalización;
- contexto de continuidad para futuras sesiones o asistentes.

No debe revelar las causas raíz ni las soluciones de los bugs pendientes.

---

# Portabilidad

El objetivo es que el repositorio sea utilizable en otro ordenador mediante:

```text
clonar repositorio
        ↓
tener Docker + Compose
        ↓
entrar en el Lab
        ↓
construir/levantar el entorno
```

No debería ser necesario instalar manualmente los runtimes específicos de cada stack.

Las operaciones concretas de Docker se documentan dentro de cada Lab y pueden ser ejecutadas por el agente encargado de operar el entorno.
