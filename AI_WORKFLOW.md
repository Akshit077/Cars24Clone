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
| `.cursor/rules/kotlin-sdui.mdc` | `**/*.kt` — MVI, package map, registry, Coil |
| `.cursor/rules/sdui-json-schema.mdc` | JSON / `schema.md` — document shape, actions, no page-specific types |
| `.cursor/rules/assignment-docs.mdc` | Submission markdown honesty |

## Verification strategy for AI-generated code

1. Read the diff. Reject any new registry type whose name describes a Cars24 marketing section.
2. Compile (`./gradlew :app:assembleDebug`) after dependency or source changes.
3. Unit-test JSON decode, unknown-type preservation, and `SduiReducerTest` (intent → state/effect, no Compose).
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

### 3. SDUI engine, then MVI rewrite (2026-08-15)

**Prompt 3a (abridged):** Do the next step — registry, action bus, bind / visibleIf, visible unknown-type fallback, one screen that loads `home.json`. No hardcoded home composable.

**What the model produced first:**

- Registry + primitive renderers, bind / `visibleIf`, `applyActions`
- **`SduiController`** with three `mutableStateOf` fields (`state`, `openSheetId`, `navigationUrl`) and `dispatch()` mutating them in place
- `SduiHostRoute` owning payload + document + controller in `remember { }`
- Coil `Application` ImageLoader

**What I accepted from 3a:** Primitive registry, lookup EMI, grid as row-chunks, snackbar for `navigate`, no `HomeScreen` composable.

**Prompt 3b (user correction):** Follow **MVI**. Keep a note of what was produced earlier and what was rewritten so the assignment’s three prompt→outcome stories stay honest.

**What I rejected / rewrote after 3b:**

- **`SduiController` (deleted).** It mixed reduce + side effects + Compose snapshot state. Rotation / process death would drop JSON state; navigation lived on the same object as UI state; nothing was a testable `(state, intent) → state`.
- **Host-local `remember` as the source of truth.** Payload switching and document load now go through `SduiIntent.SelectPayload` → ViewModel load → `DocumentLoaded`.
- **Navigation as mutable state.** `navigate` is a one-shot `SduiEffect.ShowNavigation` on a `Channel`, not a field the view must remember to clear.

**What shipped instead:** `SduiIntent` / `SduiUiState` / `SduiEffect` / `reduce()` / `SduiViewModel`. Leaves only call `scope.dispatch(actions)` → `ExecuteNodeActions`. `SduiReducerTest` covers setState+sheet, navigate-as-effect, dismiss, unknown action, load failure.

---

## One AI failure

**Where the model was wrong:** The first engine used `SduiController` — a Compose-aware mutable holder with `dispatch()` writing `state` / `openSheetId` / `navigationUrl` directly. That is MV-whatever, not MVI. It also stored navigation on the same object as document state, so the view had to `consumeNavigation()` after a snackbar.

**How it was caught:** Review against the architecture we actually want (MVI) — called out in chat, not by a crash.

**What changed:** Deleted `SduiController`. Intents in, pure `reduce()`, `StateFlow` out, effects on a `Channel`. The useful leftover from the failed design is `applyActions()` — it was already a pure JSON reducer and now sits inside `reduce()`.

**How we verify:** `SduiReducerTest` (no Compose). Host only collects `state` and `effects`.

---

## Session log

| When | Prompt intent | Kept | Rejected |
|---|---|---|---|
| 2026-08-15 | Hour 0 setup on existing Android project | Deps, rules, this file, git init | Renderer / home UI (out of scope for this block) |
| 2026-08-15 | Schema + sample JSON | Primitive contract, 3 payloads, parse models/tests | Sealed per-widget nodes; page-specific types; Compose UI |
| 2026-08-15 | SDUI engine | Registry, actions, bind, fallback, JSON host | Hardcoded HomeScreen; page-specific ViewModel state |
| 2026-08-15 | Follow MVI; log the rewrite | Intent / reduce / UiState / Effect / ViewModel | `SduiController` + `remember` as source of truth |
| 2026-08-15 | Static twin + PERF skeleton | Shared `ui/components`, Static MVI, AppHost switch, PERF methodology | Duplicating Material3 leaves; inventing perf numbers |
| 2026-08-15 | Restyle home from Cars24 screenshots | Tokenized hero/orbit/buy/sell colors; `home.json` tabs + sections; static twin match; overlay menu | New page-specific types (`HomeBanner`, `ShowroomCard`); hex in every node |
| 2026-08-16 | Fix home layout from device screenshot | Status/nav padding on `SduiScreen`; equal grid cards; Orbit section title; reserved image boxes + `scale: fit` | Page-specific `ServiceTile` type; leaving broken Unsplash URLs |
| 2026-08-16 | 6:30–7:30 static twin for honest overhead % | Shared leaves already in place; `PerfTrace` + `reportFullyDrawn`; catalog↔`home.json` parity test | Inventing PERF numbers; a second renderer; page-specific ViewModel fields |
| 2026-08-16 | First Cars24Perf logcat paste | Wrote real cold trials into `PERF.md`; dropped 15–29 s same-PID lines | Treating SDUI 582 ms as “faster than static”; inventing a 5-trial median |
| 2026-08-16 | 8:30–10:00 docs + recording script | README (choice, schema, versioning, trade-offs, 3–5 min script); `COVERAGE.md` | Inventing overhead %; claiming 95% of all future Cars24 pages |
