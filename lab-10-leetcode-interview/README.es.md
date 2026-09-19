# Lab 10 — Simulación de entrevista LeetCode

[English](README.md)

Entrevista portable Docker-first: Easy → Intermediate → Advanced. Cada fase termina en **PASS** o **FAIL**. Un FAIL se repite con otro ticket; la selección aleatoria registra y evita el ticket inmediatamente anterior.

## Modos y honor system

En **Interview Mode**, verbaliza continuamente y no consultes `INTERVIEWER_GUIDE.md`, `interviewer/`, `reference/`, tests evaluator ni evidence hasta su desbloqueo. El entrevistador pregunta de forma adaptativa, registra hints/evidencia y puntúa cada fase. En **Practice / Review Mode**, todo el material open source se puede consultar. Nada se oculta técnicamente.

Explica comprensión, investigación, hipótesis, evidencia, propuesta antes de programar, alternativas, trade-offs, qué prueba cada test, riesgos y self-review. La IA interrumpe el código silencioso con Level 0.

## Runner Docker-first

Solo hacen falta Git, Docker y Docker Compose:

```powershell
.\lab.ps1 doctor
.\lab.ps1 start easy -Random
.\lab.ps1 test easy -Ticket E3
.\lab.ps1 start intermediate -Random
.\lab.ps1 test intermediate -Ticket I5
.\lab.ps1 test intermediate -Ticket I6
.\lab.ps1 test intermediate -Ticket I7
.\lab.ps1 start advanced -Scenario A1
.\lab.ps1 test advanced -Scenario A2
.\lab.ps1 reset advanced -Scenario A1
```

Linux/macOS: `./lab doctor|start|test|reset nivel ticket`. UI: <http://localhost:18102>; API: <http://localhost:18101>. La ruta local opcional ejecuta los mismos proyectos con Maven/Java 21, Python 3.12 o Node 22.

### VS Code sin toolchains locales

Abre `lab-10-leetcode-interview/` en VS Code y ejecuta **Dev Containers: Reopen in Container**. El TypeScript Server, Vitest y el frontend se ejecutan en el servicio Compose `frontend`; el backend Java/Maven se inicia como servicio compañero. Los runners `algorithms-java` y `python` siguen disponibles con `docker compose --profile tools run --rm ...`. No necesitas instalar Node, npm, JDK, Maven ni Python en el host.

## Pools

- Easy: E1 Pair transactions (hash map, O(n)); E2 Transaction summary (parsing/decimal); E3 Balanced events (stack, O(n)).
- Intermediate: I1 Growth streak (DP/LIS, O(n²)); I4 Fraud clusters (BFS/DFS, O(rows × cols)); I5 Scheduling (greedy/intervalos, O(n log n)); I6 Content Recommendation (maps/agrupación/ordenación, O(w + v + c log c + v log v)); I7 Shuffle Playlist (greedy/maps/colas, O(n log a)).
- Advanced: A1 Duplicate Transfer (idempotencia/concurrencia); A2 Invalid State Transition (consistencia de dominio).

I2 Bitonic DP e I3 Coin Change quedan como práctica adicional. Easy/Intermediate agotan tres variantes antes de reutilizar; Advanced alterna dos. `.interview-state.json` conserva la última selección.

E1 devuelve índices ascendentes de dos importes; E2 procesa `timestamp|user|amount|STATUS` con decimal exacto; E3 valida `() [] {} <>`; I1 calcula LIS estricta; I4 cuenta componentes en cuatro direcciones y es pool principal; I5 maximiza intervalos semiabiertos compatibles y exige justificar el greedy por fin más temprano.

### I6 — Content Recommendation

Implementa en Java 21 el método `Solutions.getRecommendations(List<String> watchedCategories, List<Integer> watchedCategoryRelevance, List<String> availableVideoTitles, List<String> availableVideoCategories)`. Las listas de categorías vistas y relevancias son paralelas; también lo son las listas de títulos disponibles y sus categorías.

Agrupa los títulos disponibles por categoría. Sitúa primero las categorías vistas, por relevancia descendente; después las no vistas, por nombre de categoría ascendente. Dentro de cada categoría ordena los títulos alfabéticamente y devuelve una única lista plana de títulos.

Ejemplo: categorías vistas `[Backend, Music]`, relevancias `[300, 120]`, títulos `[Docker, Jazz, API, Yoga, Piano, Running]` y categorías de los títulos `[Backend, Music, Backend, Fitness, Music, Health]` producen `[API, Docker, Jazz, Piano, Yoga, Running]`.

Restricciones: entre 1 y 200.000 categorías vistas y contenidos; relevancias únicas entre 1 y 1.000.000.000; nombres y títulos alfanuméricos de hasta 10 caracteres. Evita búsquedas lineales repetidas en el historial.

El ejercicio se ejecuta de forma independiente, sin IA ni servicios externos:

```powershell
.\lab.ps1 test intermediate -Ticket I6
.\lab.ps1 test intermediate -Ticket I6 -Evaluator
```

Con Java 21 y Maven locales también puedes ejecutar `mvn test "-Dgroups=I6 & public"` desde `algorithms-java/`.

Hints: **1)** decide qué información necesita cada categoría antes de ordenar; **2)** usa mapas para asociar relevancia y agrupar títulos; **3)** ordena categorías y después los títulos de cada grupo antes de aplanar. Follow-up: analiza complejidad temporal/espacial, la elección de `Map`, 200.000 vídeos, la alternativa de un único `Comparator`, relevancias repetidas y títulos repetidos.

### I7 — Shuffle Playlist

El enunciado candidato completo y el código inicial están comentados directamente en `algorithms-java/src/main/java/com/stackquest/lab10/ShufflePlaylist.java`, con el formato `class Result`, la firma `getShuffledPlaylist(...)` y un `class Solution` ejecutable. El runner local lee `n` y después `n` líneas `title artist`; escribe en stdout o en `OUTPUT_PATH` si existe.

Ejecuta únicamente este ejercicio con `.\lab.ps1 test intermediate -Ticket I7`; añade `-Evaluator` para el evaluador completo o `-Reference` para la referencia separada. No necesita IA, red, base de datos ni servicios externos.

Hints: **1)** identifica el estado necesario por artista; **2)** una cola por artista conserva el orden relativo; **3)** la prioridad combina canciones restantes y primera aparición, reteniendo temporalmente al artista anterior cuando exista otra opción. El follow-up cubre corrección greedy, desempates, complejidad, 200.000 canciones, actualizaciones de `PriorityQueue` y alternativas de producto.

## Práctica SQL independiente

| Ticket | Ejercicio | Motor | Objetivos |
| --- | --- | --- | --- |
| S1 | [Wishlist Products Summary for E-commerce Dashboard](exercises/sql/S1-wishlist-products-summary/exercise.md) | MySQL | `INNER JOIN`, `CASE`, `GROUP BY`, `COUNT`, `COUNT(DISTINCT ...)` |
| S2 | [Identifying High-Impact Delays in Order Fulfillment](exercises/sql/S2-high-impact-delivery-delays/exercise.md) | MySQL | CTE, `DATEDIFF`, agregación mensual y filtrado dinámico |

Los tickets SQL son ejercicios autónomos y no forman parte de la rotación aleatoria de algoritmos. Lab 10 no dispone de un runner reutilizable de ejercicios MySQL, por lo que no añaden infraestructura de base de datos; resuelve cada enunciado de forma independiente y consulta su solución separada únicamente en modo Practice / Review.

## Evaluación

Hints: **Level 0** preguntas; **Level 1** dirección conceptual; **Level 2** técnica/lugar explícito sin código completo. Se registran; Level 1 no implica FAIL y Level 2 penaliza claramente.

Easy/Intermediate reciben PASS/GOOD/EXCELLENT por ticket y `PHASE RESULT: PASS|FAIL`. PASS exige comportamiento público y enfoque defendible; GOOD añade evaluator cases y complejidad objetivo sin Level 2; EXCELLENT añade edge cases, prueba o alternativas.

Advanced usa React → cliente HTTP tipado → controller Spring → service/domain → repository → H2. La UI crea, muestra loading/error/success, lista historial y cambia estados. A1 reproduce duplicados ante retries. A2 acepta incorrectamente `COMPLETED → PENDING`. Reset elimina el volumen y reconstruye la app común.

Sus 11 fases son Exploration, Architecture, Reproduction, Root Cause, Design, Implementation, Testing, New Evidence, Self Review, System Design y Product/Ownership. Cada una recibe PASS/FAIL. El PASS global exige 70/100, 8/11 y ningún FAIL en Root Cause, Implementation o Testing. Pesos: Understanding 10, Investigation 15, Reasoning 15, Design 15, Implementation 15, Testing 10, Debugging 5, Product/UX 5, Trade-offs 5, Ownership 5.

`algorithms-java/` es Java/JUnit puro; `track-python/` es alternativo; `track-java/` y `frontend/` forman Advanced; `exercises/` contiene metadatos; `interviewer/` guarda logs y evidencia. Los evaluator tests están ocultos solo por convención. Los fallos Docker/build/DB/puerto son de infraestructura, no del candidato.
