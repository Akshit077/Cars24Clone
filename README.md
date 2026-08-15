# Cars24Clone — SDUI

Server-driven UI take-home for Cars24 Mobile Engineering.

**Stack:** Android / Kotlin / Jetpack Compose (one platform, deep).

## Screen choice

**Cars24 home / landing**, plus a finance strip that is not always on the marketing home.

Home clears the complexity bar: search/header, banner rail, category chips, car-card rail, value-prop grid, footer CTA. The recording also requires a **tenure selector that updates EMI** and a **CTA that opens a sheet**. Those live on the same page as a JSON-driven finance card so we do not hardcode a second product surface.

A `car_detail_sketch.json` uses the same primitives on a different page. That is the rehearsal for the surprise-screen round — not a second renderer.

## Schema

The contract is `docs/schema.md`. Payloads:

| File | Role |
|---|---|
| `app/src/main/assets/sdui/home.json` | Reference screen |
| `app/src/main/assets/sdui/home_unknown_type.json` | Unknown-type fallback demo |
| `app/src/main/assets/sdui/car_detail_sketch.json` | Same schema, other page |

Types are primitives (`column`, `carousel`, `card`, `chip`, …). There is no `CarCard`. Actions (`setState`, `openSheet`, `navigate`) and `bind` / `visibleIf` live in JSON.

**Versioning:** `schemaVersion` + `minClientVersion`. Unknown keys are ignored; unknown `type` becomes a fallback node. Breaking changes bump both versions and are down-converted on the server. Full client coexistence UI is not built yet — the story is in the schema doc.

## Setup

1. Open this folder in Android Studio (Narwhal / AGP 9.2 is already configured).
2. Sync Gradle. SDUI JSON parsing uses `kotlinx.serialization`; images use Coil 3.
3. Run the `app` configuration on an emulator or device.

Internet permission is declared so Coil can load remote images from the JSON payload. Page data itself is local JSON (no live Cars24 APIs).

## Status

Schema and sample JSON are in. Renderer, static twin, and perf loop are next.

See `AI_WORKFLOW.md` for how AI is being used. `PERF.md` and `COVERAGE.md` will land with those phases.
