# Real-World Technical Interview Labs

**Debug • Test • Refactor • Learn**

[English](README.md) | [Español](README.es.md)

[![Licencia: MIT](https://img.shields.io/github/license/eChrls/Labs)](LICENSE) [![Estrellas](https://img.shields.io/github/stars/eChrls/Labs)](https://github.com/eChrls/Labs/stargazers) [![Forks](https://img.shields.io/github/forks/eChrls/Labs)](https://github.com/eChrls/Labs/forks) [![Integridad](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml/badge.svg?branch=main)](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml)

![Social Preview de Real-World Technical Interview Labs](docs/assets/social-preview.png)

Colección open source de proyectos deliberadamente imperfectos y reproducibles que simulan trabajo y pruebas técnicas reales.

Esto no es LeetCode, algoritmos aislados, un tutorial de sintaxis ni una colección de katas. Es práctica con bases de código desconocidas, debugging, tests fallando, código legacy, refactoring, REST, bases de datos, integración frontend/backend, datos, razonamiento orientado a producción y entrevistas técnicas.

## El trabajo detrás de la entrevista

Muchas pruebas reales no entregan un editor vacío. Entregan esto:

```text
un proyecto desconocido
        ↓
ejecutarlo
        ↓
entenderlo
        ↓
reproducir el problema
        ↓
inspeccionar tests/logs
        ↓
depurar
        ↓
corregir/refactorizar
        ↓
verificar
        ↓
explicar la decisión
```

Ese flujo es la identidad del proyecto.

## Sistema de estados canónico

| Estado | Significado |
| --- | --- |
| `✅ DONE` | Existe, está validado y, cuando corresponde, sincronizado/publicado. |
| `🟡 IN PROGRESS` | Existe trabajo activo pero aún no cumple su Definition of Done. |
| `🧪 VALIDATION` | La implementación parece terminada, pero falta verificación obligatoria. |
| `⏳ PENDING` | Planeado pero no iniciado o todavía no disponible. |
| `🚫 NOT PLANNED` | Decisión consciente de no planificarlo actualmente. |

Crear archivos o código no basta para `✅ DONE`. Un Lab necesita evidencia de proyecto reproducible, tests en el estado esperado, solución temporal demostrada, documentación completa, Docker validado y commit/push cuando corresponda. La infraestructura open source debe estar aplicada, comprobada y sincronizada. Si falta evidencia, usa `🧪 VALIDATION` o `🟡 IN PROGRESS`.

## Catálogo de Labs

Los siete Labs permanecen visibles sea cual sea su estado. Que un Lab esté disponible no implica que existan todos sus tracks.

| Lab | Stack | Enfoque | Easy | Intermediate | Advanced | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| [Lab-1](Lab-1/README.md) | Java 21, Spring Boot, Maven, PostgreSQL, JUnit | Backend desconocido, Java/Spring, REST, PostgreSQL, tests, debugging | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `✅ DONE` |
| [Lab-2](Lab-2/README.md) | Java 21, Spring Boot, PostgreSQL, JUnit/Mockito | Legacy, characterization tests, refactoring, side effects, TDD | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `✅ DONE` |
| [Lab-3](Lab-3/README.md) | React, TypeScript, Java, Spring Boot, PostgreSQL | HTTP, DTO, estado async, JPA, transacciones, debugging entre capas | `⏳ PENDING` | `🟡 IN PROGRESS` | `⏳ PENDING` | `🟡 IN PROGRESS` |
| Lab-4 | Angular, TypeScript, Node.js, NestJS | Contratos frontend/backend y comportamiento asíncrono | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` (opcional/comunidad) | `⏳ PENDING` |
| Lab-5 | Vue 3, TypeScript, PHP, Laravel, MySQL | Features de producto, validación, persistencia e integración | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` (opcional/comunidad) | `⏳ PENDING` |
| Lab-6 | Python, FastAPI, PostgreSQL, Elasticsearch | Backend, ETL, reporting, calidad, SQL, sincronización y búsqueda | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` |
| Lab-7 | Python, FastAPI, Pydantic, PostgreSQL/pgvector, abstracción de provider AI | Applied AI Engineering: prompting, extracción, RAG, evals, tools, debugging | `⏳ PENDING` | `⏳ PENDING` | `🚫 NOT PLANNED` | `⏳ PENDING` |

Lab-1 y Lab-2 son Labs base validados, no una afirmación de que tengan su expansión completa. Lab-3 tiene baseline publicado, pero su modelo de tracks y experiencia global siguen incompletos. Consulta el [roadmap](docs/ROADMAP.md).

## Patrones de pruebas reales

Los challenges son originales, pero siguen patrones recurrentes observados en pruebas técnicas públicas. La investigación incluye GetYourGuide, Personio, Crewmeister, George, FACEIT, Mimo, Equal Experts, Primer, Flipdish y Wise. Lab-7 también podrá usar patrones públicos documentados del Inato AI Engineer test, Hex AI Engineering take-home y AI Engineering Field Guide: baseline más evaluación, extracción documental, RAG, tools, Text-to-SQL, evals ocultas y explicación de experimentos y trade-offs.

> Inspired by recurring patterns observed in publicly available European technical assessments.

Son referencias, no ejercicios para copiar ni afirmaciones de afiliación. Las reglas autoritativas de investigación, originalidad, copyright, dificultad, pistas y edición están en la [Guía editorial y de investigación](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md).

## Tracks de dificultad

### Easy

Finalmente en todos los Labs: alcance reducido, starting point visible, test/endpoint fallando conocido, pistas progresivas y fundamentos.

### Intermediate

Finalmente en todos los Labs: varios archivos/capas, debugging, DB/API, decisiones y riesgo de regresión.

### Advanced

Obligatorio finalmente en Lab-1, Lab-2, Lab-3 y Lab-6; expansión opcional futura/comunitaria en Lab-4 y Lab-5. Advanced para Lab-7 es `🚫 NOT PLANNED` en su alcance inicial. En los demás Labs debe incluir concurrencia, locking, límites transaccionales, rendimiento, SQL/query plans, idempotencia, consistencia, incidentes, sincronización o escalabilidad.

Docker no aumenta la dificultad; proporciona infraestructura.

## Formato estándar y pistas

```text
Context
Observed behaviour
Expected behaviour
Reproduction
Constraints
Acceptance criteria
Starting point / progressive hints
Follow-up discussion
```

Easy tiene starting point explícito. Intermediate ofrece reproducción y pistas progresivas. Advanced expone síntoma y aceptación con ayuda mínima. Nunca uses comentarios como `// BUG HERE`; deben parecer comentarios reales de producción. Reglas completas en la [guía editorial](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md).

## Modos de uso

| Modo | Experiencia prevista | Estado |
| --- | --- | --- |
| Learning Mode | Pistas, documentación y sin presión temporal. | `⏳ PENDING` |
| Interview Mode | Timebox sugerido, pistas según dificultad y razonamiento observable. | `⏳ PENDING` |
| Review Mode | Causa raíz, code review, alternativas, producción y follow-up. | `⏳ PENDING` |

Son el objetivo global y aún no están formalizados consistentemente en todos los Labs.

## Debugging como competencia central

| Stack | Debugging |
| --- | --- |
| Java/Spring | Debugger JVM, breakpoints, call stack, variables, JUnit, logs |
| React/Angular/Vue | Browser DevTools, Network, Console, Sources, tests |
| Node | Debugger, async stack, logs, tests |
| PHP | Stack traces, logs, tests/debugger |
| Python | Debugger, exceptions, logging, pytest |
| PostgreSQL | SQL de diagnóstico, `EXPLAIN`, locks, comparación de datos |
| Elasticsearch | Queries, mappings, documentos, estado de sincronización |

Docker es infraestructura, no objetivo de aprendizaje. Un agente puede ejecutar Docker para que el alumno se concentre en investigar y razonar.

## Los tests son evidencia

Los tests son criterios de aceptación, evidencia de debugging y protección contra regresiones, no una comprobación final. Los Labs pueden empezar con tests rojos; es el baseline del challenge y no significa que el repositorio esté roto.

## Docker-first y portable

```text
clonar repo
   ↓
Docker + Compose
   ↓
elegir Lab
   ↓
construir entorno aislado
```

No hace falta instalar globalmente Java/Maven, Node/npm, PHP/Composer, Python, PostgreSQL, MySQL ni Elasticsearch. Cada Lab define su entorno. Todo Lab debe reconstruirse usando esencialmente Git, Docker y Compose. Las DB iniciales salen de migrations, seeds o fixtures versionados, nunca de volúmenes personales. Portabilidad actual: `✅ DONE` para los baselines existentes, respaldada por setup versionado y Compose validado.

## Lab-7 — Applied AI Engineering

**Prompting • RAG • Evals • Tools • Debugging**

Estado: `⏳ PENDING` · Objetivo principal: **Self-learning** · Objetivo secundario: **Práctica realista de entrevistas de AI Engineer**.

A diferencia de Labs 1–6, su objetivo principal no es exclusivamente simular pruebas técnicas. Enseñará AI Engineering mediante problemas progresivos, originales y medibles, incorporando patrones de assessments públicos cuando resulte útil. Mantiene Docker-first, reproducibilidad, portabilidad, realismo, debugging y evidencia mediante tests/evals.

### Stack previsto y política de providers

El stack provisional incluye Python, FastAPI, Pydantic, PostgreSQL, pgvector al introducir RAG, pytest, Docker, Docker Compose, embeddings, RAG, evals y tool calling. Las versiones exactas se elegirán al implementarlo.

```text
AI Provider
├── deterministic fake/mock
├── external API
└── optional local model
```

Los tests y evals fundamentales deben funcionar sin API de pago, Internet ni comportamiento no determinista de un LLM. Se podrá configurar un provider real mediante `.env` y ofrecer opcionalmente un modelo local pequeño. Ollama es opcional y nunca será el baseline.

Debe ejecutarse razonablemente en Linux y Windows mediante Docker Desktop/WSL2 o equivalente, con CPU moderna estándar y RAM doméstica razonable. Nunca exigirá GPU dedicada, NVIDIA, CUDA, Apple Silicon, hardware de alta gama, modelos locales grandes ni hardware especializado. La ausencia de GPU no puede impedir completarlo.

### Progresión prevista

| Track | Etapa | Aprendizaje | Estado |
| --- | --- | --- | --- |
| Easy | E1 — Prompt engineering | Instrucciones system/user, precisión, constraints, structured output, JSON válido, iteración | `⏳ PENDING` |
| Easy | E2 — Extracción de CV | CV ficticios originales; tecnología, periodos, experiencia, términos, ausencias; primero búsqueda/regex/fake | `⏳ PENDING` |
| Easy | E3 — Fundamentos de evaluación | Dataset, salida esperada, precisión, falsos positivos/negativos; “looks good” no es evidencia fiable | `⏳ PENDING` |
| Intermediate | RAG | Documentos, chunking, embeddings, retrieval, top-k, citas, grounding, no-answer | `⏳ PENDING` |
| Intermediate | AI debugging | Trazar input → parsing → chunking → embedding → retrieval → context → prompt → generation → validation | `⏳ PENDING` |
| Intermediate | Evals | Dataset visible, regresiones, calidad de retrieval, respuesta y citas | `⏳ PENDING` |
| Intermediate | Tool calling | Búsqueda documental, SQL read-only, calculadora, consulta estructurada | `⏳ PENDING` |
| Intermediate | Text-to-SQL | Lenguaje natural → SQL seguro → PostgreSQL → respuesta; SQL destructivo bloqueado | `⏳ PENDING` |
| Intermediate | Seguridad AI básica | Prompt injection, permisos, límites de datos sensibles, validación de salida | `⏳ PENDING` |
| Integrado | Challenge aplicado final | Combinar fundamentos, retrieval, evals, tools, debugging y trade-offs justificados | `⏳ PENDING` |
| Advanced | Alcance inicial de Lab-7 | Sin training/fine-tuning grande, CUDA, ML distribuido, serving pesado, internals profundos, modelos locales grandes o multi-agent complejo | `🚫 NOT PLANNED` |

Principio rector: establecer primero un baseline, medirlo e introducir IA solo cuando mejore justificadamente el resultado. Futuras extensiones comunitarias podrán reconsiderar áreas excluidas si existe demanda.

### Lab-6 y Lab-7 son diferentes

- **Lab-6 — Data Engineering/backend:** ETL, PostgreSQL, Elasticsearch, calidad de datos, pipelines y reporting.
- **Lab-7 — AI Engineering:** prompts, extracción, embeddings, RAG, evals, tools y AI debugging.

Pueden compartir tecnologías, pero no objetivos de aprendizaje.

## Jerarquía documental

- **[README.md](README.md):** identidad, visión, catálogo, estado, roadmap resumido, objetivos, tracks y milestones.
- **[docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md):** fuente autoritativa de realismo, investigación, dificultad, diseño, pistas, criterios, originalidad y follow-up.
- **[docs/LAB_SPEC.md](docs/LAB_SPEC.md):** contrato técnico obligatorio para crear un Lab.
- **`Lab-N/README.md`:** contexto, operación, baseline y tickets específicos.
- **[docs/ROADMAP.md](docs/ROADMAP.md):** roadmap detallado y evolución futura.

## Contribuciones open source

Se buscan New Labs, challenges nuevos o Advanced, bug reports, tests, documentación, portabilidad, accesibilidad, stacks alternativos y escenarios realistas. Lee [CONTRIBUTING.md](CONTRIBUTING.md).

## Panel de estado comunitario

| Capacidad | Estado |
| --- | --- |
| Repositorio público | `✅ DONE` |
| Licencia MIT | `✅ DONE` |
| CONTRIBUTING | `✅ DONE` |
| Código de conducta | `✅ DONE` |
| Política de seguridad | `✅ DONE` |
| Issue Forms | `✅ DONE` |
| Plantilla de PR | `✅ DONE` |
| Discussions | `✅ DONE` |
| Topics | `✅ DONE` |
| Labels | `✅ DONE` |
| CODEOWNERS | `✅ DONE` |
| Community Profile 100% | `✅ DONE` |
| CI de integridad | `✅ DONE` |
| Social Preview | `✅ DONE` (configurado manualmente) |
| GitHub Pages | `⏳ PENDING` |
| Dominio propio | `🚫 NOT PLANNED` |
| Primer contributor externo | `⏳ PENDING` |
| Primer Lab comunitario | `⏳ PENDING` |
| Primera release | `⏳ PENDING` |

El [Social Preview](docs/assets/social-preview.png) se conserva para GitHub, LinkedIn y difusión. Pages se evaluará cuando los Labs centrales sean estables, la documentación madure y exista utilidad clara. GitHub es la plataforma principal; hoy no hace falta web/dominio.

## Milestones de discovery y comunidad

| Milestone | Estado |
| --- | --- |
| Primera star externa | `⏳ PENDING` |
| Primer fork externo | `⏳ PENDING` |
| Primer contributor externo | `⏳ PENDING` |
| Primera PR externa | `⏳ PENDING` |
| Primer Lab comunitario | `⏳ PENDING` |
| Primera release | `⏳ PENDING` |

No se fabrican métricas; un milestone solo cambia con evidencia pública.

## Roadmap maestro

| Área | Capacidad | Estado |
| --- | --- | --- |
| Foundation | Base open source | `✅ DONE` |
| Foundation | Documentación global | `✅ DONE` |
| Foundation | Infraestructura comunitaria | `✅ DONE` |
| Foundation | Investigación editorial | `✅ DONE` |
| Core Labs | Lab-1 | `✅ DONE` |
| Core Labs | Lab-2 | `✅ DONE` |
| Core Labs | Lab-3 | `🟡 IN PROGRESS` |
| Core Labs | Lab-4 | `⏳ PENDING` |
| Core Labs | Lab-5 | `⏳ PENDING` |
| Core Labs | Lab-6 | `⏳ PENDING` |
| Core Labs | Lab-7 | `⏳ PENDING` |
| Difficulty expansion | Easy en los siete Labs | `⏳ PENDING` |
| Difficulty expansion | Intermediate en los siete Labs | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-1 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-2 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-3 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-6 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-7 | `🚫 NOT PLANNED` |
| Learning experience | Pistas progresivas | `⏳ PENDING` |
| Learning experience | Learning Mode | `⏳ PENDING` |
| Learning experience | Interview Mode | `⏳ PENDING` |
| Learning experience | Review Mode | `⏳ PENDING` |
| Learning experience | Estimaciones de tiempo | `⏳ PENDING` |
| Learning experience | Preguntas follow-up | `⏳ PENDING` |
| Community | Good first issues | `⏳ PENDING` |
| Community | Primera contribución externa | `⏳ PENDING` |
| Community | Primer Lab comunitario | `⏳ PENDING` |
| Community | Release/versionado | `⏳ PENDING` |
| Future | GitHub Pages | `⏳ PENDING` |
| Future | Stacks/Labs comunitarios adicionales | `⏳ PENDING` |
| Future | Dominio propio | `🚫 NOT PLANNED` |

## Mantenimiento de este roadmap

> Las capacidades planificadas permanecen visibles aunque estén pendientes. Al completar un milestone, actualiza su estado en lugar de eliminarlo.

Todo agente que complete un milestone debe actualizar este README en la misma PR/commit cuando corresponda. Solo puede cambiar a `✅ DONE` con evidencia de finalización.

## Contexto de continuidad del proyecto

Proyecto open source y Docker-first de Labs originales y realistas. La [guía editorial](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md) gobierna investigación y calidad; [LAB_SPEC](docs/LAB_SPEC.md) gobierna la construcción. Hay siete Labs previstos: Labs 1–6 se centran en entrevistas técnicas y Lab-7 prioriza autoaprendizaje de Applied AI con práctica inspirada en entrevistas. Easy e Intermediate deben llegar a los siete; Advanced es obligatorio en Labs 1, 2, 3 y 6, opcional/comunitario en Labs 4 y 5, y `🚫 NOT PLANNED` inicialmente en Lab-7. Los Labs base 1 y 2 están completos; Lab-3 sigue en progreso; Labs 4–7, expansión, modos, milestones, releases y Pages siguen pendientes. Conserva todo lo planificado y usa solo los estados canónicos.

Publicado bajo la [Licencia MIT](LICENSE).
