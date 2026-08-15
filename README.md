# Cars24Clone — SDUI

Server-driven UI take-home for Cars24 Mobile Engineering.

**Stack:** Android / Kotlin / Jetpack Compose (one platform, deep).

**Screen (to be justified in this README after the schema lands):** Cars24 home / landing, plus a JSON-driven tenure → EMI strip and bottom sheet for the required recording.

## Setup

1. Open this folder in Android Studio (Narwhal / AGP 9.2 is already configured).
2. Sync Gradle. SDUI JSON parsing uses `kotlinx.serialization`; images use Coil 3.
3. Run the `app` configuration on an emulator or device.

Internet permission is declared so Coil can load remote images from the JSON payload. Page data itself is local JSON (no live Cars24 APIs).

## Status

Hour 0 only: repo, dependencies, Cursor rules, `AI_WORKFLOW.md`. Schema, renderer, and the static twin are next.

See `AI_WORKFLOW.md` for how AI is being used. `PERF.md` and `COVERAGE.md` will land with those phases.
