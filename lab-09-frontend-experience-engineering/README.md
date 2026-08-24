# Lab 09 — Frontend Experience Engineering

Lab 09 hands you a working but mediocre CMS-driven storefront — three independent
mini-apps for a fictional home-goods brand, Terra & Co. — and a product/design brief
for each one. The apps run. The data is real. Nothing is visually or technically
refined. Your job is to turn requirements into a professional frontend: interpret a
visual brief, decide what a component needs to do, choose whether a third-party
library earns its place, and prove the result against real UX, accessibility and SEO
evidence — not just "does it render."

## Why this Lab exists

Most frontend exercises stop at "render a list." This one follows the loop a
professional actually works in:

    REQUIREMENTS
        ↓
    INTERPRET
        ↓
    DESIGN THE SOLUTION
        ↓
    DECIDE ON TOOLING
        ↓
    IMPLEMENT
        ↓
    VALIDATE UX
        ↓
    VALIDATE ACCESSIBILITY
        ↓
    VALIDATE SEO
        ↓
    REVIEW QUALITY

Learning outcomes:

- build with Vue 3 (Composition API, `<script setup>`) and Angular 20 (standalone
  components, signals) against the same kind of CMS data;
- turn a short visual brief into layout, spacing and type decisions without a
  pixel-perfect mock to trace;
- implement loading/empty/error states and stateful UI (view toggles, filters,
  search, disclosure/accordion patterns) with real keyboard and focus behaviour;
- evaluate whether a third-party frontend library is worth adding, and defend that
  call;
- work toward WCAG 2.2 AA with axe-core, then close the gap manual review always
  leaves;
- implement progressive SEO — metadata, canonical URLs, Open Graph, Schema.org
  JSON-LD — and prove it by parsing the actual output, not by checking a tag exists;
- read a pre-built Playwright/axe suite as executable acceptance criteria instead of
  writing test infrastructure yourself.

## Stack and architecture

- Vue 3.5 + TypeScript + Vite (Easy landing page, Advanced product page)
- Angular 20 + TypeScript, standalone components and signals (Intermediate catalog)
- Express + `json-server` as a deterministic, fixture-backed CMS API
- Playwright + `@axe-core/playwright` for QA, run from a shared npm workspace
- Docker Compose; a single Dev Container for all three apps plus QA

Each app is a self-contained storefront page for **Terra & Co.**, not a shared
product. They only share infrastructure:

    apps/landing-vue        Easy         home page, fed by GET /api/home
    apps/catalog-angular    Intermediate product catalog, fed by GET /api/products
    apps/product-vue        Advanced     product detail, fed by GET /api/products/:id
                 ↓ (all three, via each dev server's /api proxy)
    services/cms-api        json-server + Express, deterministic db.json fixtures
                 ↓
    qa/playwright           Playwright + axe-core suite, provided — not written by you

```
lab-09-frontend-experience-engineering/
├── compose.yml
├── Dockerfile                  (shared workspace image: Node, Angular CLI, Playwright)
├── package.json                (npm workspace root — one lockfile for the whole Lab)
├── .devcontainer/devcontainer.json
├── .vscode/launch.json
├── apps/
│   ├── landing-vue/
│   ├── catalog-angular/
│   └── product-vue/
├── services/cms-api/
│   ├── server.js, db.json, media/
└── qa/playwright/
    ├── playwright.config.ts
    ├── tests/{easy,intermediate,advanced}.spec.ts
    └── helpers/{axe,jsonld}.ts
```

Nothing here is a microfrontend architecture. Vue and Angular are used deliberately
side by side because this Lab simulates three different client engagements, not one
production application — do not try to make the two frameworks share runtime state.

## Docker services

| Service | Purpose | Host port |
| --- | --- | --- |
| cms-api | Fixture-backed CMS API (`/api/home`, `/api/products`, `/api/products/:id`, `/api/categories`) and static media | 127.0.0.1:18089 |
| landing-vue | Easy scenario dev server | 127.0.0.1:15091 |
| catalog-angular | Intermediate scenario dev server | 127.0.0.1:15092 |
| product-vue | Advanced scenario dev server | 127.0.0.1:15093 |
| dev | Dev Container: full workspace, Playwright/axe, Angular CLI | none (attach via VS Code) |

Every service is bound to `127.0.0.1` only. Each app's dev server proxies `/api` and
`/media` to `cms-api` over the Compose network, so the same code path works whether
you open it from your host browser (published port) or from Playwright running inside
the Dev Container (service DNS name) — there is no separate "test" configuration.

## Docker-first setup

Prerequisites on the host: Git, Docker Desktop (or Docker Engine + Compose), VS Code
with the **Dev Containers** extension. Node, npm, the Angular CLI and Playwright are
not required on the host — they only exist inside the Dev Container image.

    git clone https://github.com/eChrls/StackQuest-Labs.git
    cd StackQuest-Labs/lab-09-frontend-experience-engineering
    docker compose config

Open this Lab directory in VS Code and run **Dev Containers: Reopen in Container**.
The first run builds the shared workspace image (Node 20, Angular CLI, `npm ci` across
every app, `playwright install --with-deps chromium`) and starts `cms-api` plus all
three dev servers alongside the `dev` service you land in.

From the Dev Container terminal, confirm the environment:

    node --version
    npx ng version
    curl -s http://cms-api:4000/api/home | head -c 200

From a host terminal in the Lab directory, confirm the published ports:

    docker compose ps
    curl -s http://127.0.0.1:18089/api/products | head -c 200
    curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:15091/
    curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:15092/
    curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:15093/

If the services were stopped, bring them back from the host:

    docker compose up -d cms-api landing-vue catalog-angular product-vue

Each app can also be started independently for local iteration, from inside the Dev
Container or via `docker compose run`:

    npm run dev:landing        # http://localhost:5173
    npm run dev:catalog        # http://localhost:4200
    npm run dev:product        # http://localhost:5174

## CMS API and seed data

`services/cms-api` wraps `json-server` as Express middleware over a single
deterministic `db.json`. Nothing here is a real backend — no auth, no writes, no
external network calls, and the fixture data is original (a fictional brand, Terra &
Co., selling lighting, storage, textiles and outdoor goods).

| Endpoint | Returns |
| --- | --- |
| `GET /api/home` | Hero, nav, benefits, testimonial, footer content for the landing page |
| `GET /api/products` | All 12 products across 4 categories |
| `GET /api/products/:id` | A single product (variants, specs, gallery images, related IDs) |
| `GET /api/categories` | The 4 category records |
| `GET /media/...` | Static placeholder images referenced by the fixtures |

The catalog spans **Lighting** (3), **Storage** (3), **Textiles** (3) and **Outdoor**
(3) products, with a mix of `in_stock`, `low_stock` and `out_of_stock` availability so
loading/empty/error and disabled-state UI has real data to react to. The Advanced
scenario is built around one fixture product, `aurora-desk-lamp` — a lamp with three
finish variants, four gallery images and five spec rows.

## The baseline

All three apps run and render real CMS data out of the box. None of them are broken.
What they lack is professional polish: weak or absent semantic landmarks, low-contrast
or undersized interactive elements, interaction that isn't keyboard-reachable, missing
or generic SEO metadata, and no Schema.org markup. That gap — mediocre-but-functional
to professional — is the Lab.

Each scenario has its own short visual brief below. There is no pixel-perfect mock to
match. Interpreting the brief into real layout, spacing and type decisions is part of
the exercise — the QA suite checks behaviour and structure, not visual similarity to
a reference screenshot.

## Choose a scenario

Easy, Intermediate and Advanced are three fully independent mini-apps. You can work
any one of them without the others being solved, and a broken Advanced attempt never
blocks Easy or Intermediate. Difficulty rises with the surface area of the requirement
(visual/semantic → stateful interaction + one library decision → full interaction +
structured data), not with how "hard" any single bug is — there are no hidden bugs to
hunt here, only requirements to implement.

---

## Easy — Landing (Vue)

**Brief, as given to you by the client:**

> Terra & Co. wants a modern, clear, accessible landing page. It needs a header with
> navigation, a hero with one clear call to action, a short benefits section, a
> customer quote, and a footer — built from the content already coming out of our CMS.
> It has to work as well on a phone as it does on a laptop, and it needs to be findable
> — real page titles and previews when the link is shared.

**Visual character:** warm, approachable, editorial-adjacent but simple. A single
accent color (the starting CSS uses a muted terracotta/clay tone), generous
line-height, no heavy shadows or gradients. Think "small, considered goods brand,"
not "SaaS dashboard."

**Requirements:**

- semantic landmarks (`header`, `nav` with an accessible name, `main`, `footer`) and
  exactly one `<h1>`;
- a responsive layout that reads cleanly from a 375px phone viewport up through
  desktop, with no horizontal overflow;
- visible focus indicators on every interactive element, and CTA/nav/footer contrast
  and touch-target sizing that clears WCAG 2.2 AA;
- meaningful `alt` text on the hero image;
- a descriptive `<title>`, a real `meta[name=description]`, and Open Graph
  `og:title`/`og:description`/`og:image` — set from the CMS content, not hard-coded.

### A guided library: icons

This is the one place in the Lab where you're told exactly what to reach for, because
the point here is practicing the *mechanics* of adding a dependency, not the decision
itself:

1. `npm install lucide-vue-next --workspace apps/landing-vue`
2. Import only the icons you use: `import { ArrowRight } from 'lucide-vue-next'`
3. Use it as a component: `<ArrowRight :size="18" />`

`lucide-vue-next` is MIT-licensed, tree-shakeable (only imported icons ship), and
ships its own TypeScript types — reasonable defaults to look for in any icon library.

### Acceptance criteria

- `npm run qa:easy` passes all 3 tests (`qa/playwright/tests/easy.spec.ts`);
- the automated WCAG 2.2 AA scan reports no serious/critical violations;
- title, meta description and Open Graph tags reflect real CMS content.

<details>
<summary>Hint 1 — start here</summary>

Open the rendered page's accessibility tree (Chrome DevTools → Elements → Accessibility,
or the axe DevTools extension) before touching CSS. Compare what landmarks are
*actually* exposed against what the brief asks for. A `<div class="topbar">` styled to
look like a header is not a `<header>`.
</details>

<details>
<summary>Hint 2 — if you're stuck on the failing axe scan</summary>

Read the `serious`/`critical` violations the test prints — axe tells you the exact
element, the computed colors, and the contrast ratio it measured. Fix contrast and
focus-visibility as CSS changes; you should not need to change the Vue template
structure for either of them.
</details>

<details>
<summary>Hint 3 — if SEO metadata isn't updating</summary>

The CMS content arrives asynchronously. `<title>` and `<meta>` tags written into
`index.html` are static and won't reflect that data. Something needs to run *after*
the fetch resolves and update `document.title` / the DOM `<meta>` elements directly —
look at what lifecycle/reactivity hook fires when your fetched `ref` changes.
</details>

---

## Intermediate — Catalog (Angular)

**Brief, as given to you by the client:**

> We need a real catalog page: browse all products, switch between a grid and a list
> view, filter by category, search by name, sort by price. If someone switches to list
> view, it should still be list view if they come back later in the session. Every
> product card should be able to expand for a longer description without leaving the
> page. And it needs structured data so search engines understand it's a product
> listing.

**Visual character:** deliberately different from the landing page — dense,
utilitarian, marketplace-like. Tight spacing, a grayscale palette with a single dark
accent, square corners, small type. This is a tool for browsing, not a brand moment.

**Requirements:**

- a working grid/list toggle whose choice survives a page reload within the session;
- category filtering, name search and price sorting that actually change the rendered
  results;
- an accessible empty state when a filter/search combination matches nothing;
- each card's "show details" control must be a real, keyboard-operable disclosure —
  reachable by <kbd>Tab</kbd>, activatable by <kbd>Enter</kbd>/<kbd>Space</kbd>, and
  exposing its state via `aria-expanded`;
- loading and error states for the initial fetch;
- canonical URL, Open Graph tags, and a Schema.org `ItemList` JSON-LD block listing
  the rendered products.

### Evaluating a frontend dependency for this scenario

The brief doesn't hand you a library — the disclosure pattern (keyboard handling,
`aria-expanded`/`aria-controls` wiring) and the view-toggle state are complex enough
that reaching for a library is a legitimate option, and building them by hand is
another. Before adding anything, work through the checklist in
[Evaluating a frontend dependency](#evaluating-a-frontend-dependency) below. Candidates
worth researching for this scenario include Angular's own `@angular/cdk` (`a11y`
module — same org as the framework, TypeScript-first, MIT), a headless
interaction/accessibility library, or a small utility-CSS package if the gap is purely
visual density. None of these is the "correct" answer by default — a clean hand-rolled
implementation is a perfectly valid outcome too, as long as you can defend the
trade-off.

### Acceptance criteria

- `npm run qa:intermediate` passes all 5 tests
  (`qa/playwright/tests/intermediate.spec.ts`);
- the view preference survives `page.reload()`;
- the disclosure trigger is a real interactive element with correct ARIA state;
- the `ItemList` JSON-LD contains one entry per rendered product.

<details>
<summary>Hint 1 — start here</summary>

Read `intermediate.spec.ts` before writing anything — it's the acceptance criteria in
executable form. Notice which of the 5 tests already pass on the baseline (filtering
and sorting work today) and which don't; that split tells you exactly where the gap is.
</details>

<details>
<summary>Hint 2 — state and interaction</summary>

"Survives a reload" means the preference has to live somewhere that outlives the
in-memory component state — think about what browser storage is scoped to a single
tab session versus what persists forever. For the disclosure control, compare what a
`<div>` with a click handler gives you for free against what a `<button>` gives you for
free (focusability, keyboard activation, semantics) before adding any ARIA by hand.
</details>

<details>
<summary>Hint 3 — SEO/schema</summary>

`ItemList.itemListElement` is an array of `ListItem` entries, each wrapping the actual
`Product`. The array should be built from whatever is currently *rendered* (i.e. after
filtering), not the full unfiltered catalog — otherwise the structured data lies about
what's on the page.
</details>

---

## Advanced — Product detail (Vue)

**Brief, as given to you by the client:**

> This is our flagship product page — it should feel premium, not like a template.
> Full breadcrumb trail, a real image gallery, finish/color options that actually
> change the price, specifications that don't overwhelm the page by default, and
> related products. And it needs the full SEO and Schema.org treatment: this is the
> page that shows up in Google Shopping-style results.

**Visual character:** premium, editorial, restrained. Serif display type for the
product name, warm off-white background, generous whitespace, minimal color beyond the
product imagery itself. This is the highest-interpretation brief in the Lab — there is
no wireframe, only the brand language established by the other two scenarios to react
against (or deliberately depart from, if you can justify it).

**Requirements:**

- breadcrumbs (Home → Category → Product);
- an image gallery where every fixture image is actually reachable, not just the
  first one;
- finish/color variant selection that is keyboard-operable, exposes the correct ARIA
  role/state for a single-select group, and updates the displayed price;
- specifications presented as a disclosure the page doesn't force open by default;
- related products, loading/error states;
- full SEO: title, description, canonical, Open Graph, and JSON-LD for `Product`
  (with a nested `Offer`) and `BreadcrumbList`.

### The library decision is more open here

The gallery is complex enough — multiple images, an active-image indicator, keyboard
navigation — that it's worth asking "why would I build this myself instead of using a
library?" out loud. Research a lightweight, headless carousel/gallery library (for
example one built specifically for this kind of use case, MIT-licensed, no framework
lock-in) against simply managing an `activeImageIndex` ref yourself. For the
specifications disclosure, weigh a library-provided accordion against the native
`<details>`/`<summary>` element, which gets you disclosure semantics for free with zero
dependencies — sometimes the right call is *not* to add a library. Use the
[Evaluating a frontend dependency](#evaluating-a-frontend-dependency) checklist and be
ready to explain whichever way you went in Review Mode.

### Acceptance criteria

- `npm run qa:advanced` passes all 4 tests (`qa/playwright/tests/advanced.spec.ts`);
- every gallery image is reachable and becomes the displayed main image;
- the variant control group exposes `role="radio"`/`aria-checked` correctly and the
  price updates when a variant is chosen via keyboard;
- the `Product` JSON-LD includes a nested `Offer` with a numeric `price`, a
  `priceCurrency`, and a `schema.org`-namespaced `availability` value, and a sibling
  `BreadcrumbList` matches the visible breadcrumb depth.

<details>
<summary>Hint 1 — start here</summary>

Run `npm run qa:advanced` against the baseline and read all four failures together
before starting — they map cleanly onto four separate areas of the brief (gallery,
disclosure, variants, structured data), so you can tackle them in almost any order.
</details>

<details>
<summary>Hint 2 — gallery and variants</summary>

For both the gallery thumbnails and the variant swatches, ask what HTML element you're
using to represent "a thing the user can activate and that has a selected/unselected
state." An `<img>` or a `<span>` gives you neither for free.
</details>

<details>
<summary>Hint 3 — Schema.org</summary>

`schema.org/InStock`, `schema.org/LimitedAvailability` and `schema.org/OutOfStock` are
the three availability values that map onto this fixture data's `in_stock`/
`low_stock`/`out_of_stock` — the JSON-LD test parses the actual value, so a string that
merely *contains* the word "stock" won't satisfy it.
</details>

---

## Evaluating a frontend dependency

Before installing anything beyond the guided Easy-scenario example, work through this
checklist — it's as much the point of the Intermediate/Advanced scenarios as the code
is:

- **Necessity** — does this solve a real gap, or would a dozen lines of plain
  TypeScript do the same job?
- **Maintenance** — recent releases, open issues actually being triaged, not just
  stars;
- **Documentation** — can you use it correctly from the docs alone?
- **TypeScript** — first-class types, not a stale `@types` package;
- **Accessibility** — does it ship correct ARIA/keyboard behaviour, or just visuals?
- **License** — MIT/Apache-2.0/BSD-compatible with this repository; nothing
  copyleft or commercial for the base exercise;
- **Bundle size** — check the real shipped size (e.g. on Bundlephobia), not the
  marketing page;
- **Personalization** — can you actually restyle it to match a brief, or does it fight
  you?
- **Lock-in** — how painful would it be to rip out in six months?
- **The native alternative** — does the platform already give you this
  (`<details>`, `<dialog>`, CSS `:has()`, scroll-driven animations) for free?

Passing this checklist and *choosing not to add a dependency* is a valid, often
correct outcome — it is explicitly not "cheating" on the Intermediate/Advanced
scenarios.

## Accessibility

**WCAG 2.2 AA is the target; automated checks cover only testable criteria and manual
review remains necessary.** Nothing in this Lab claims certification.

What the automated suite covers, via `@axe-core/playwright` plus custom Playwright
checks:

- landmarks, heading structure, accessible names for interactive elements;
- color contrast (as far as axe can compute it from rendered styles);
- image `alt` text;
- keyboard reachability and operability of custom controls;
- visible focus indication;
- `aria-expanded`/`aria-checked`/`role` correctness on disclosure and radio-group
  patterns;
- responsive reflow at a 375px viewport.

What still needs a human, per scenario:

- does focus order actually match visual/reading order, not just "is everything
  reachable";
- do error/empty-state messages make sense read out of context by a screen reader;
- is motion (if you add any) safe under `prefers-reduced-motion`;
- does the page hold up under 200% browser zoom, not just a narrow viewport.

## SEO and Schema.org

SEO requirements are progressive across the three scenarios, matching the difficulty
curve:

| Scenario | Required |
| --- | --- |
| Easy | descriptive `<title>`, `meta[name=description]`, Open Graph (`og:title`/`og:description`/`og:image`) |
| Intermediate | canonical `<link>`, fuller Open Graph, `ItemList` JSON-LD |
| Advanced | full set — title, description, canonical, Open Graph, `Product` + nested `Offer` + `BreadcrumbList` JSON-LD |

Working through it as a chain, per page:

    WHAT DATA IS ON THE PAGE
        ↓
    WHAT REAL-WORLD ENTITY DOES IT REPRESENT
        ↓
    WHICH SCHEMA.ORG TYPE FITS THAT ENTITY
        ↓
    JSON-LD, BUILT FROM THE SAME DATA THE PAGE RENDERS

Every SEO/schema test in this Lab parses the actual `<meta>` attributes and JSON-LD
payload and asserts on their content — a `<script type="application/ld+json">` tag
with no valid JSON, or Open Graph tags that don't reflect what's on the page, will not
pass.

## Tests and QA

The QA harness — Playwright config, fixtures, helpers, axe wiring, all 12 tests across
the three spec files — is provided. **You run it and read it as evidence; you are not
expected to write test infrastructure in this Lab**, only to make the provided tests
pass and understand what each one is checking.

    npm run qa:easy           # 3 tests — Easy / landing-vue
    npm run qa:intermediate   # 5 tests — Intermediate / catalog-angular
    npm run qa:advanced       # 4 tests — Advanced / product-vue
    npm run qa:all            # all 12

Run these from the `dev` Dev Container service (or `docker compose exec dev npm run
qa:easy` from a host terminal). Each command targets only its own scenario, so working
Easy never requires Advanced to be solved, and vice versa.

Each test groups several coherent assertions rather than checking one thing at a time
— for example, the Easy accessibility test both runs the axe scan *and* checks focus
visibility, because both are part of "is this keyboard/screen-reader usable."

## Visual review rubric

Because the briefs are interpreted, not traced from a mock, use this rubric — for
yourself or a reviewer — instead of pixel comparison:

- **Hierarchy** — is it obvious what to look at first, second, third?
- **Brief fidelity** — does it match the *character* described (warm/approachable,
  dense/utilitarian, premium/editorial), even if the exact execution differs from
  anyone else's?
- **Internal consistency** — do spacing, type scale and color stay consistent within
  that one page?
- **Legibility and density** — is text comfortable to read at its size and line
  length?
- **Responsive behaviour** — does the layout degrade gracefully, not just "not
  overflow," from 375px up?
- **Interaction feedback** — do hover/focus/active/disabled states exist and make
  sense?
- **CTA clarity** — is the primary action on each page unambiguous?

Never evaluate against "does this look like [a specific commercial site]" — the brief
is the only spec.

## Debugging

`.vscode/launch.json` provides three `pwa-chrome` configurations, all usable from
inside the Dev Container without any host-installed Node:

- **Lab 09 — Debug Landing (Vue, Easy)** → attaches to `http://localhost:15091`
- **Lab 09 — Debug Catalog (Angular, Intermediate)** → attaches to
  `http://localhost:15092`
- **Lab 09 — Debug Product (Vue, Advanced)** → attaches to `http://localhost:15093`

To confirm a real breakpoint hit:

1. With the Dev Container running (and the corresponding app started via `npm run
   dev:*`), open a source file — for example
   `apps/landing-vue/src/composables/useHome.ts` — and set a breakpoint inside `load()`.
2. Run the matching launch configuration from the **Run and Debug** panel.
3. Reload the page (for Vue) or trigger the action that calls the code (for Angular).
4. Execution should pause on your breakpoint, with the **Variables**/**Call Stack**
   panel showing the original TypeScript — not compiled/bundled output — because both
   Vite and the Angular dev server emit real source maps.

This exact mechanism — a debugger attaching to the served dev-server script and using
its source map to resolve back to `.ts`/`.vue` source — was independently verified via
the Chrome DevTools Protocol for both the Vue and the Angular app before this Lab was
published: a breakpoint set before navigation paused execution inside the original
source in both cases.

## Modes

**Learning Mode:** no timebox. Read the hints in order, ask for the library-decision
checklist to be walked through explicitly, and use the Mentor spoilers freely once
you've made a genuine attempt.

**Interview Mode:** pick one scenario, use a loose timebox (Easy ~45–60 min,
Intermediate ~75–120 min, Advanced ~90–150 min), start from the public brief only, and
narrate your library-decision reasoning out loud rather than silently reaching for
`npm install`.

**Review Mode:** walk through the diff, explain why each requirement was solved the
way it was, justify (or reconsider) any dependency you added, discuss what WCAG 2.2 AA
gaps automated tooling couldn't catch, and identify one thing you'd do differently at
production scale (e.g. real image optimization, i18n, a design system shared across
more than three pages).

## Infrastructure versus challenge

| Symptom | First check | Interpretation |
| --- | --- | --- |
| `docker compose ps` shows no containers | Compose project not started | Run the Docker-first setup steps above |
| A dev server returns "Blocked request... not allowed" | `allowedHosts` config | Infrastructure — should already be set; report if seen on a clean checkout |
| `/api/*` returns nothing from an app's dev server | `cms-api` not running or proxy misconfigured | `docker compose ps cms-api`, then `curl http://127.0.0.1:18089/healthz` |
| `npm run qa:*` fails at the very first `page.goto` | Target dev server not running | `docker compose up -d <service>` and retry |
| A test fails with a clear assertion about content/attributes | Likely challenge evidence | Read the failure message — it names the missing landmark, contrast ratio, or JSON-LD field |
| Angular `ng serve` fails to start | Rare — image build issue | `docker compose build catalog-angular`, check for a `npm ci` error in the log |

## Mentor / AI Support — SPOILERS

<details>
<summary>Open only for mentoring, review or an explicitly requested solution.</summary>

### Verified base root causes

**Easy — landing-vue:**
- Landmarks: the baseline uses `<div class="topbar">`/`<div class="nav">`/no `<main>`/
  `<div class="footer">` instead of `<header>`/`<nav aria-label>`/`<main>`/`<footer>`.
- Responsive: `.hero` and `.benefits` use fixed pixel `width: 960px` and a fixed
  `repeat(4, 220px)` grid with no media query, causing horizontal overflow below
  ~1000px.
- Contrast: `.cta` is `color: #cfcfcf` on `background: #e8e8e8` (~1.27:1); `.eyebrow`,
  `.testimonial-author` and `.footer-note` are `#888`/`#999` on white (~3.5:1/~2.8:1),
  all below the 4.5:1 AA text threshold.
- Focus: global `* { outline: none; }` with no replacement.
- `alt`: the hero `<img>` has no `alt` attribute at all.
- Touch targets: nav/footer links have no padding, so their hit area is smaller than
  24×24 CSS px.
- SEO: `index.html` has a static generic `<title>Home</title>` and no
  `meta[name=description]`/Open Graph tags; nothing updates them after the CMS fetch
  resolves.

**Intermediate — catalog-angular:**
- View persistence: `setView()` only calls `this.view.set(mode)`; nothing is written
  to `sessionStorage`, and the signal's initial value is the literal `'grid'` rather
  than a read from storage.
- Disclosure: `<div class="expand-trigger" (click)="toggleExpand(...)">` — a `<div>`
  is not in the tab order and has no `aria-expanded`/`aria-controls`.
- Empty state: the `*ngFor` card loop has no sibling `*ngIf="filteredProducts().length
  === 0"` branch at all — a no-match search renders a genuinely empty `<main>`.
- SEO/schema: no canonical `<link>`, no Open Graph tags, and no
  `<script type="application/ld+json">` anywhere in the baseline.

**Advanced — product-vue:**
- Gallery: `mainImage` is hard-coded to `product.images[0]`; the thumbnail `<img>`
  elements have no click handler and no way to change the active index.
- Disclosure: specs render as a plain, always-visible `<dl>` with no toggle control at
  all.
- Variants: swatches are `<span class="swatch" @click="selectVariant(...)">` — no
  `role`, no `tabindex`, no `aria-checked`; the click handler does correctly update
  `effectivePrice`, so the functional logic is fine, only the semantics/keyboard access
  are missing.
- SEO/schema: `index.html` has a static `<title>Product</title>`, no meta description/
  canonical/Open Graph, and the baseline never injects any JSON-LD.

### Verified temporary solutions

Each scenario was temporarily solved, the full suite run to green, a visual/manual QA
pass performed, and the solution then reverted to the committed baseline before this
Lab was published. None of the temporary code remains in the repository.

- **Easy:** added real `<header>`/`<nav aria-label="Primary">`/`<main>`/`<footer>`
  landmarks; replaced fixed widths with `max-width` + `flex-wrap`/`auto-fit` grid;
  darkened `.eyebrow`/`.testimonial-author`/`.footer-note` to `#595959` (~7:1) and the
  CTA to a solid `#b5541f` background with white text; added a
  `a:focus-visible { outline: 3px solid #b5541f; }` rule; added real `alt` text on the
  hero image; added `padding` to nav/footer links to clear the 24×24px target-size
  check; and used a Vue `watch()` on the fetched CMS data to set `document.title` and
  create/update the description and Open Graph `<meta>` tags at runtime.
- **Intermediate:** persisted `view` to `sessionStorage` under a
  `lab9-catalog-view` key and read it back as the signal's initial value; converted
  the disclosure trigger to a real `<button>` with `[attr.aria-expanded]` and
  `[attr.aria-controls]`; added the missing empty-state `<p role="status" *ngIf="...">`
  branch; and used Angular's `Title`/`Meta` services plus a manually created
  canonical `<link>` and `<script type="application/ld+json">` (built from the
  currently filtered/rendered product list) for the SEO/schema requirements. The
  library-decision checklist was walked using `@angular/cdk`'s `a11y` module as one
  reasonable candidate; the temporary reference ultimately hand-rolled the ARIA wiring,
  since the actual gap (a native `<button>` plus two attribute bindings) didn't justify
  a new dependency — a legitimate answer to the checklist, not the only one.
- **Advanced:** added an `activeImageIndex` ref driving `mainImage`, with each
  thumbnail converted to a real `<button aria-pressed>`; wrapped the swatches in a
  `role="radiogroup"` with each swatch converted to `<button role="radio"
  aria-checked>`; wrapped specs in a `<button aria-expanded aria-controls>` toggling a
  `v-show` panel; and injected `Product` (with a nested `Offer` mapping
  `in_stock`/`low_stock`/`out_of_stock` to the three `schema.org` availability URIs)
  and `BreadcrumbList` JSON-LD via a `watch()` on the fetched product, alongside the
  same title/description/canonical/Open Graph pattern used in Easy. The library
  checklist was applied to the gallery (a headless carousel library such as
  `embla-carousel-vue` is a reasonable candidate given the interaction complexity) and
  to the specs disclosure (native `<details>`/`<summary>` is a reasonable candidate
  precisely because it needs no dependency at all) — either a library-backed or a
  hand-rolled implementation satisfies the acceptance tests, which check behaviour and
  ARIA state, not implementation choice.

### Common wrong fixes

- adding `alt=""` to the hero image instead of a real description (passes a naive
  linter, fails the intent of the requirement and still likely fails axe's
  image-alt check because the image is not decorative);
- persisting view/search state to a Angular component field or a `BehaviorSubject`
  instead of `sessionStorage` — survives navigation but not a hard reload, so the
  reload-based test still fails;
- adding `tabindex="0"` and a `keydown` handler to a `<div>` instead of using a
  `<button>` — technically satisfiable, but reinvents behaviour the platform already
  gives a real button for free, and easy to get subtly wrong (missing `role="button"`,
  wrong keys handled);
- writing JSON-LD as a hard-coded string that doesn't reflect the actual
  fetched/filtered data — passes a "tag exists" check but not this Lab's tests, which
  parse and assert on the JSON content;
- installing a large UI kit to solve one disclosure button — technically works, fails
  the bundle-size/necessity checklist and is exactly the outcome Intermediate/Advanced
  are designed to make you weigh against the alternative.

### Full-resolution outline

1. Fix landmarks, responsive width, contrast, focus-visibility, `alt` text and
   touch-target sizing in `landing-vue`; wire dynamic SEO metadata from the fetched
   CMS `watch()`.
2. Persist the catalog view preference; convert the disclosure trigger to a real
   button with correct ARIA; add the empty-state branch; add canonical/Open
   Graph/`ItemList` JSON-LD reflecting the rendered (filtered) product set.
3. Make every gallery image reachable via a real control; convert variant swatches to
   an accessible radio group that drives the price; add a real specs disclosure; add
   the full SEO set plus `Product`/`Offer`/`BreadcrumbList` JSON-LD.
4. For each scenario's library-eligible requirement, run the
   [Evaluating a frontend dependency](#evaluating-a-frontend-dependency) checklist and
   record the decision, whichever way it goes.
5. Re-run `npm run qa:all`, do a manual keyboard pass and a 375px viewport pass on all
   three apps, and review remaining manual-only WCAG 2.2 AA items.

### Troubleshooting

If a QA run fails before it even reaches an assertion (a `page.goto` timeout, a
connection-refused error), that is infrastructure, not challenge evidence — confirm
`docker compose ps` shows all four app-facing services running and that
`curl http://127.0.0.1:18089/healthz` returns `{"status":"ok"}` before treating a
failure as part of the exercise.

### Validation matrix

| Scenario | Baseline result | Temporary reference result | Restored baseline result |
| --- | --- | --- | --- |
| Easy | 3/3 fail (landmarks/responsive, WCAG scan+focus, SEO) | 3/3 pass | 3/3 fail, identical to baseline run |
| Intermediate | 4/5 fail (persistence, keyboard/aria, empty state, SEO/schema); 1/5 pass (filter/sort) | 5/5 pass | 4/5 fail, 1/5 pass — identical to baseline run |
| Advanced | 4/4 fail (gallery, disclosure, variants, JSON-LD) | 4/4 pass | 4/4 fail, identical to baseline run |

### Agent continuity context

A new agent can, from this README alone: understand the three-scenario architecture
and why Vue and Angular are used deliberately side by side; identify every deliberate
baseline gap and its file/selector; give Hint 1/2/3 for any scenario without revealing
the fix; distinguish an infrastructure failure (Docker/proxy/dev-server not running)
from real challenge evidence (a named, specific test assertion); explain the
library-decision checklist and apply it to either the Intermediate or Advanced
scenario; validate a candidate solution against the exact acceptance criteria above;
and fully resolve any scenario using the Full-resolution outline without
re-discovering the codebase from scratch.

</details>

## Final baseline contract

The committed state of `apps/landing-vue`, `apps/catalog-angular` and
`apps/product-vue` contains none of the temporary reference implementation described
above. All three apps run and render real CMS data; all 12 provided QA tests are
red for the reasons documented in the Mentor spoiler section, with the one
already-functional Intermediate test (filter/sort) passing on the baseline. Temporary
solutions, `node_modules`, build output, Playwright reports and screenshots do not
belong in this Lab's committed tree.

This README is the Lab-specific continuity source. Global documentation and the
master roadmap are maintained by their dedicated documentation work.
