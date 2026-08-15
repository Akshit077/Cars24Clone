# AI workflow

How this SDUI system was built with AI. Append entries as work happens — do not reconstruct prompts after the fact.

## Tool stack

| Tool | Role |
|---|---|
| Cursor (Grok 4.6) | Design, implementation, docs |
| Android Studio | Run, Layout Inspector, release builds, profiling |
| Cars24 consumer app | Visual reference for the home/landing screen |

## Context / rules files I wrote to brief the model

These exist before any renderer code on purpose. The assignment scores the brief, not just the output.

| File | Scope |
|---|---|
| `.cursor/rules/sdui-invariants.mdc` | Always on: primitives only, JSON-driven actions, fallback, one stack |
| `.cursor/rules/kotlin-sdui.mdc` | `**/*.kt` — package map, registry, dispatcher, Coil |
| `.cursor/rules/sdui-json-schema.mdc` | JSON / `schema.md` — document shape, actions, no page-specific types |
| `.cursor/rules/assignment-docs.mdc` | Submission markdown honesty |

## Verification strategy for AI-generated code

1. Read the diff. Reject any new registry type whose name describes a Cars24 marketing section.
2. Compile (`./gradlew :app:assembleDebug`) after dependency or source changes.
3. Unit-test JSON decode + unknown-type fallback (no crash, fallback node present).
4. Manual: render `home.json`, flip a chip, open the sheet, load `home_unknown_type.json`.
5. Perf numbers only from a **release** build on a real device. Do not accept model-invented timings.

---

## Prompt → outcome stories

### 1. Hour 0 setup (2026-08-15)

**Prompt (abridged):** Help with `0:00–0:30` — init git, add kotlinx.serialization + Coil, write Cursor rules, start `AI_WORKFLOW.md`. Project already exists as `Cars24Clone`.

**What the model produced:**

- Version catalog + Gradle plugin wiring for `kotlinx-serialization-json` and Coil 3
- `INTERNET` permission
- Four `.cursor/rules/*.mdc` files
- This log, a thin `README.md`, `git init`

**What I accepted:** The split of rules (always-on invariants vs Kotlin vs JSON vs docs). Starting this file in hour 0. Coil 3 + OkHttp network fetcher instead of Coil 2.

**What I will watch / may rewrite later:**

- Serialization runtime is `1.10.0` (fits Kotlin `2.2.10`) rather than `1.11.0` (defaults to Kotlin 2.3). If the compiler plugin complains, pin or bump Kotlin — do not silently “fix” it by dropping `@Serializable`.
- Rules are constraints, not a schema. The next session must write `docs/schema.md` and sample JSON *before* generating composables. If the model jumps to `MainActivity` UI, reject that.

### 2. Schema + sample JSON (2026-08-15)

**Prompt (abridged):** Do the next step after hour 0 — first git commit, then `docs/schema.md` and v0 `home.json` before any Compose views.

**What the model produced:**

- `docs/schema.md` (document, primitives, bind, visibleIf, actions, versioning)
- `home.json`, `home_unknown_type.json`, `car_detail_sketch.json`
- `SduiDocument` + parse unit tests
- README screen-choice paragraph

**What I accepted:** Open `props` (`JsonObject`) so new leaf attributes do not require a client release. Structured `visibleIf` (`eq` / `in` / `neq`) instead of a general expression language. Lookup tables for EMI, not a formula engine. A car-detail *sketch* payload to rehearse the surprise screen.

**What I rejected / rewrote:**

- **Sealed `SduiNode` subtypes per widget.** The Kotlin rule suggested sealed nodes over `Map<String, Any>`. Sealed types fail decode on `liveAuctionTicker`, which breaks the required unknown-type fallback. Kept a single `SduiNode` data class with `type: String`.
- **`HomeBanner` / `CarRail` / `CarCard` types.** Those would make the surprise screen a rewrite. Car tiles are `card` + children.
- **Renderer / Compose home in this step.** Out of scope; schema first.

### 3. SDUI engine (2026-08-15)

**Prompt (abridged):** Do the next step — registry, action bus, bind / visibleIf, visible unknown-type fallback, one screen that loads `home.json`. No hardcoded home composable.

**What the model produced:**

- `SduiRegistry` + primitive renderers, `SduiController` / `applyActions`, bind + `visibleIf`
- `SduiScreen` + `SduiHostRoute` (payload menu is host chrome)
- Coil `Application` ImageLoader so network images actually load
- Unit tests for bind, setState, unknown action, registry membership

**What I accepted:** Open props + string `type` registry. Lookup tables for EMI. Grid as a non-lazy row-chunk so it can sit inside a scrolling column. Snackbar stub for `navigate`.

**What I rejected / rewrote:**

- **Hardcoded `HomeScreen` composable.** `MainActivity` only hosts `SduiHostRoute`.
- **ViewModel fields named `tenureMonths` / `selectedCategory`.** State is a `JsonObject` the JSON writes.
- **`Card(onClick = {})` when there are no actions.** That still consumes clicks; non-action cards use the non-clickable `Card` overload.
- **Sealed action types.** Unknown actions no-op so a future `share` does not crash decode.

---

## One AI failure

_(Fill the first time the model is wrong about schema, Compose APIs, or perf — and how it was caught. Do not invent one.)_

---

## Session log

| When | Prompt intent | Kept | Rejected |
|---|---|---|---|
| 2026-08-15 | Hour 0 setup on existing Android project | Deps, rules, this file, git init | Renderer / home UI (out of scope for this block) |
| 2026-08-15 | Schema + sample JSON | Primitive contract, 3 payloads, parse models/tests | Sealed per-widget nodes; page-specific types; Compose UI |
| 2026-08-15 | SDUI engine | Registry, actions, bind, fallback, JSON host | Hardcoded HomeScreen; page-specific ViewModel state |
