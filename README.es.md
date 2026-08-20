# Real-World Technical Interview Labs

[English](README.md) | [Español](README.es.md)

Proyectos Docker deliberadamente defectuosos para practicar debugging, código legacy, testing, refactoring y entrevistas técnicas.

[![Licencia: MIT](https://img.shields.io/github/license/eChrls/Labs)](LICENSE) [![Estrellas](https://img.shields.io/github/stars/eChrls/Labs)](https://github.com/eChrls/Labs/stargazers) [![Forks](https://img.shields.io/github/forks/eChrls/Labs)](https://github.com/eChrls/Labs/forks) [![Contributors](https://img.shields.io/github/contributors/eChrls/Labs)](https://github.com/eChrls/Labs/graphs/contributors) [![Issues](https://img.shields.io/github/issues/eChrls/Labs)](https://github.com/eChrls/Labs/issues) [![Integridad](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml/badge.svg?branch=main)](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml)

Estos proyectos deliberadamente imperfectos simulan los repositorios recibidos durante una prueba técnica o al incorporarse a un equipo. Son aplicaciones reales —no katas algorítmicas— y cubren backend, frontend, datos, tests y código legacy en entornos reproducibles.

## ¿Por qué este proyecto?

Muchas pruebas técnicas no empiezan con un proyecto vacío. Piden ejecutar una aplicación desconocida, entender su código, seguir el comportamiento entre archivos y capas, localizar bugs, interpretar tests, depurar fallos, modificar código legacy y añadir funcionalidades sin regresiones. Estos Labs permiten practicar exactamente ese trabajo.

## Filosofía

- **Docker-first y reproducible.** Git y Docker son las únicas dependencias esperadas en el host.
- **Un Lab, un entorno aislado.** Runtimes, servicios, bases de datos y dependencias pertenecen a cada Lab.
- **Fallos deliberados.** Un test fallando puede ser evidencia del challenge, no una rotura del repositorio.
- **Debugging primero.** Investiga síntomas y sigue evidencias antes de cambiar código.
- **Código realista.** Aplicaciones por capas, persistencia, efectos secundarios y diseño legacy imperfecto.
- **Tests como evidencia.** Revelan comportamiento, protegen contratos y guían el refactoring.
- **Sin runtimes globales ocultos.** Una máquina limpia puede reconstruir cada Lab.
- **Sin soluciones publicadas.** Los baselines quedan sin resolver; no se guardan soluciones, parches ni copias.

Docker proporciona la infraestructura; no es necesariamente la materia evaluada.

## Catálogo de Labs

| Lab | Stack y enfoque | Dificultad | Estado |
| --- | --- | --- | --- |
| [Lab 1](Lab-1/README.md) | Debugging con Java + Spring Boot | Beginner | 🟢 **Available** |
| [Lab 2](Lab-2/README.md) | Java legacy + refactoring + TDD | Advanced | 🟢 **Available** |
| Lab 3 | React + TypeScript + Java/Spring Boot | Intermediate | 🟡 **In progress** |
| Lab 4 | Angular + TypeScript + NestJS | Intermediate | ⚪ **Planned** |
| Lab 5 | Vue 3 + TypeScript + Laravel | Intermediate | ⚪ **Planned** |
| Lab 6 | Python + FastAPI + PostgreSQL + Elasticsearch/Data | Advanced | ⚪ **Planned** |

Consulta la fuente de verdad en el [roadmap](docs/ROADMAP.md).

## Cómo funciona

```text
clonar → elegir Lab → Docker construye → ejecutar proyecto/tests
       → investigar fallos → depurar → corregir/refactorizar/implementar
```

El README de cada Lab documenta su dominio, servicios, comandos, baseline y criterios de finalización.

## ¿Para quién es?

- Desarrolladores junior y early-mid.
- Personas que preparan entrevistas técnicas.
- Desarrolladores que practican con bases de código desconocidas.
- Contributors que quieren diseñar challenges realistas.

## Dificultad

- **Beginner:** alcance guiado, código pequeño y fundamentos de debugging.
- **Intermediate:** varias capas o tecnologías y mayor riesgo de regresión.
- **Advanced:** restricciones legacy, decisiones arquitectónicas o refactoring seguro considerable.

La dificultad describe el challenge, no un puesto laboral requerido.

## Contribuir

Lee [CONTRIBUTING.md](CONTRIBUTING.md) para proponer un Lab, mejorar un challenge, informar de uno roto, mejorar documentación, añadir tests o mejorar portabilidad. Los Labs nuevos deben seguir la [especificación](docs/LAB_SPEC.md).

## Comunidad

- [Issues](https://github.com/eChrls/Labs/issues)
- [Discussions](https://github.com/eChrls/Labs/discussions)
- [Código de conducta](CODE_OF_CONDUCT.md)
- [Política de seguridad](SECURITY.md)
- [Soporte](SUPPORT.md)

## Licencia

Publicado bajo la [Licencia MIT](LICENSE).
