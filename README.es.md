# StackQuest Labs

[English](README.md)

StackQuest Labs reúne retos técnicos basados en repositorios y orientados a Docker. Cada Lab es un proyecto desconocido que hay que ejecutar, inspeccionar, depurar, probar, refactorizar y explicar. El objetivo es aprender nuevos stacks y tecnologías, practicar ingeniería cercana a producción, resolver problemas reales y preparar entrevistas técnicas.

Los nueve Labs actuales son el contenido inicial. Incluyen fallos deliberados y observables: reproduce el síntoma, formula una hipótesis, usa tests y logs, haz un cambio pequeño y explica sus trade-offs. Cada README local contiene reproducción, criterios de aceptación, Hint 1/2/3, debugging guiado, spoilers de mentor, troubleshooting, matriz de validación y Agent Continuity.

## Catálogo

| Lab | Stack | Contenido / objetivos |
|---|---|---|
| [Lab 01 — Java/Spring Debugging](./lab-01-java-spring-debugging/) | Java 21, Spring Boot, Maven, PostgreSQL, JUnit | Depuración de una API de pagos desconocida, contratos HTTP, nullabilidad, validación, persistencia y tests. |
| [Lab 02 — Java Legacy & Refactoring](./lab-02-java-legacy-refactoring/) | Java 21, Spring Boot, Maven, PostgreSQL, JPA/Flyway, JUnit/Mockito | Tests de caracterización, efectos laterales legacy, refactor incremental, TDD, refunds y concurrencia. |
| [Lab 03 — React + Spring Full-Stack](./lab-03-react-spring-fullstack/) | React, TypeScript, Java, Spring Boot, PostgreSQL | Debugging entre capas, DTOs, estado async, contratos REST, transacciones y reglas de negocio. |
| [Lab 04 — Angular + Spring Enterprise](./lab-04-angular-spring-enterprise/) | Angular, TypeScript, Java, Spring Boot, PostgreSQL | Reactive Forms, RxJS, estado HTTP, validación, autorización, transacciones JPA y debugging de integración. |
| [Lab 05 — Vue + Laravel/PHP Full-Stack](./lab-05-vue-laravel-php/) | Vue 3, TypeScript, PHP, Laravel, MySQL | Flujos reactivos, formularios, errores API, autorización, reglas de negocio, persistencia y transacciones. |
| [Lab 06 — Python Data Engineering](./lab-06-python-data-engineering/) | Python, FastAPI, PostgreSQL, Elasticsearch, pytest | Ingesta, calidad de datos, idempotencia, reporting, sincronización, búsqueda y recuperación operativa. |
| [Lab 07 — Applied AI Engineering](./lab-07-applied-ai-engineering/) | Python, FastAPI, Pydantic, provider determinista, SQLite | Extracción estructurada, evals, retrieval, grounding, Text-to-SQL seguro, tools y defensa ante prompt injection. |
| [Lab 08 — AWS Cloud & DevOps](./lab-08-aws-cloud-devops/) | Docker, conceptos AWS, Terraform, GitHub Actions, PostgreSQL | Contenedores, CI/CD, IaC, mínimo privilegio, RDS/ECR, observabilidad, diagnóstico y teardown. |
| [Lab 09 — Frontend Experience Engineering](./lab-09-frontend-experience-engineering/) | Vue 3, Angular, TypeScript, Playwright, axe | Convertir un frontend mediocre alimentado por un CMS en uno profesional: implementación visual, UI responsive, evaluación de librerías de terceros, WCAG 2.2, SEO, Schema.org y QA frontend. |

## Cómo usar un Lab

Lee su README, ejecuta la configuración Compose documentada y empieza por un reto focalizado. Separa los fallos del reto de los fallos de dependencias, base de datos o Docker. Una solución temporal verde sirve para aprender; después restaura el baseline deliberadamente rojo. La ruta local principal no requiere servicios cloud de pago y los secretos deben quedar fuera de Git.

## Setup de desarrollo versionado

Los Labs que necesitan debugging visual incluyen `.devcontainer/devcontainer.json` y `.vscode/launch.json` versionados. Abre el directorio del Lab en VS Code, instala la extensión Dev Containers, ejecuta **Dev Containers: Reopen in Container**, espera a que terminen de instalarse las extensiones del stack y elige la configuración indicada en su README. Estos archivos se suben intencionadamente: solo contienen configuración reproducible del editor/contenedor; nunca deben incluir tokens, rutas personales, claves privadas ni credenciales de producción. Lab 08 usa diagnóstico Docker y no añade un Dev Container/debugger artificial.

## Contribuir

Estos nueve Labs son el contenido inicial, no un currículo cerrado. Cualquier contribuidor puede añadir retos Easy, Intermediate, Advanced o especializados a cualquier Lab. El nuevo contenido debe mantener estructura canónica, ejecución local determinista, defectos independientes y observables, pistas progresivas, criterios de aceptación, tests, troubleshooting, guía de mentor y Agent Continuity según [LAB_SPEC](docs/LAB_SPEC.md). Consulta [CONTRIBUTING.md](CONTRIBUTING.md) y [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

La base editorial está en [Interview Research & Editorial Guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md). [ROADMAP.md](docs/ROADMAP.md) queda reservado para ideas futuras reales, no para historial de construcción.

## Seguridad y alcance

Ejecuta los comandos desde la raíz salvo indicación contraria. No subas credenciales, estado de proveedores, dependencias generadas, dumps ni volúmenes locales. Los Labs cloud requieren revisión de costes, credenciales de mínimo privilegio y teardown obligatorio antes de cualquier prueba live opcional.
