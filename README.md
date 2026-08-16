# Cars24Clone — SDUI

Take-home for **Cars24 Mobile Engineering — The SDUI Assignment**.

**Stack:** Android / Kotlin / Jetpack Compose only. One platform, deep — not a thin clone on two OS’s.

## Screen choice

**Cars24 home / landing**, built from the live app screenshots, plus a finance strip the marketing home does not always show.

Home clears the complexity bar without page-specific types:

- Search + underline tabs (All / Buy used car / Sell car / Loans)
- Vertical **grid** (manage vehicle, car-check)
- Horizontal **carousel** rails (showrooms, buy, sell, loans, used cars)
- Chips that change what is on screen (`setState` + `visibleIf`)
- Card taps (`navigate` → snackbar `sdui://…`)
- Tenure chips that **bind** EMI from a JSON lookup
- **Check eligibility** / **Add vehicle** → `openSheet`

A used-car tile is `card` + `image` + `text`. There is no `CarCard` or `HomeBanner`.

`car_detail_sketch.json` is the same engine on another page (gallery, specs grid, EMI + sheet). That is the surprise-screen rehearsal, not a second renderer.

## Schema rationale

The contract is `docs/schema.md`. It is written for a **second, unseen Cars24 page**, not only this home.

| Choice | Why |
|---|---|
| Primitives only | A new marketing section is JSON. A new *kind* of widget (map, video) is a registry entry. |
| Open `props` (`JsonObject`) | `minLines`, `scale`, `variant` can land without a model change. |
| `type: String`, not sealed node classes | Unknown types must decode so the fallback can show. Sealed subtypes fail on `liveAuctionTicker`. |
| `bind` + lookup tables | EMI is `lookups.emiByTenure[state.tenureMonths]`. Not a formula engine. |
| Structured `visibleIf` (`eq` / `in` / `neq`) | Tabs and rails without Kotlin `when (tab)`. |
| Actions in JSON | `setState`, `openSheet`, `navigate`, `closeSheet`. Taps do not hardcode product logic in composables. |

Payloads:

| File | Role |
|---|---|
| `app/src/main/assets/sdui/home.json` | Reference screen |
| `app/src/main/assets/sdui/home_unknown_type.json` | Unknown-type fallback (must not crash) |
| `app/src/main/assets/sdui/car_detail_sketch.json` | Same schema, other page |

## Versioning (old app + new payload)

`schemaVersion` is the document shape. `minClientVersion` is the oldest renderer allowed to show it.

| Change | Old client | New client |
|---|---|---|
| New optional prop or unknown `type` | Ignore key / pink fallback node | Uses the prop / new registry entry |
| Rename `type` or change action meaning | Keep last good cached document (banner not built — story only) | Bump **both** versions; server down-converts for old apps |

Unknown keys: `ignoreUnknownKeys`. Do not fork the renderer per app version. A real server would send `X-SDUI-Client: 1`; today the file is local.

## Architecture (MVI)

```
View  --SduiIntent-->  ViewModel  --reduce()-->  SduiUiState
                         |                         |
                    load JSON                  SduiScreen
                         |
                    SduiEffect (navigate snackbar)
```

`reduce()` is a pure function (`SduiReducerTest`). JSON `setState` writes `UiState.nodeState`. The first engine used `SduiController` + `mutableStateOf`; that was deleted — see `AI_WORKFLOW.md` story 3 and **One AI failure**.

Leaves in `ui/components/` are shared with the **static twin** so `PERF.md` measures the engine, not different chips.

## Trade-offs (cut on purpose)

| Cut | Why |
|---|---|
| No iOS / Flutter | Assignment: one stack, deep, after docs + recording + fallback exist. |
| `navigate` is a snackbar | Proves the intent. A real graph is not the SDUI claim. |
| No live HTTP | Assets are the server. Same decode path. |
| JSON edit needs a rerun | `assets/` are packaged. Edit JSON, **do not touch Kotlin**, rerun — that is the live-edit demo. |
| No `minClientVersion` banner UI | Versioning story is in the schema; the banner was out of scope. |
| Bottom nav not in JSON | App chrome. The overflow ⋮ is also chrome. |
| PERF overhead % blank | First logcat set was first-open biased (n=1 static). Methodology over a fake median. See `PERF.md`. |

## Setup

1. Open this folder in Android Studio (AGP 9.2).
2. Sync Gradle. Parsing: `kotlinx.serialization`. Images: Coil 3.
3. Run `app` on a device or emulator.

`INTERNET` is for Coil. Page JSON is local.

## Run

Overflow ⋮ (demo chrome, not schema):

| Item | What it is |
|---|---|
| **SDUI · Home** | `home.json` |
| **SDUI · Unknown type** | Fallback demo |
| **SDUI · Car detail** | Same engine, other JSON |
| **Static · Home** | Hardcoded twin — `PERF.md` baseline only |

## Recording (3–5 min)

https://drive.google.com/file/d/1DK1ouoxO489BCwfGaACLs0YSqjoNIle_/view?usp=drivesdk

Talk while you tap. Do not open Static Home except to say it exists for perf.

1. **JSON render (~45s).** SDUI · Home. Scroll. “This tree is `home.json`. A car tile is `card` + children, not `CarCard`.”
2. **Tabs / chips (~30s).** All → Buy → back to All. “`setState` + `visibleIf`. No Kotlin `when (tab)`.”
3. **Tenure + sheet (~60s).** Scroll to **Car loan EMI**. Tap 12 / 24 / 36 — price changes. “Lookup in JSON, not a `when` in Compose.” **Check eligibility** — sheet opens with the same bound EMI. Close.
4. **Unknown fallback (~40s).** ⋮ → Unknown type. Pink `liveAuctionTicker`. Swift card still there. “Unknown `type` does not crash.”
5. **Live JSON, no Kotlin (~60s).** Android Studio: `home.json`, change `"Search Swift"` to `"Search Baleno"` (or any string). Run the app again. **Do not edit `.kt` files.** Show the new placeholder. “Client code unchanged.”

If time: ⋮ → Car detail — “same primitives, other page.”

## Docs

| File | What it is |
|---|---|
| `docs/schema.md` | Wire contract |
| `PERF.md` | Static vs SDUI, real logcat, no invented % |
| `COVERAGE.md` | What JSON can do vs new leaves |
| `AI_WORKFLOW.md` | Three prompt stories + one AI failure |
