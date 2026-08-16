# Performance

Compare the **static** home twin with the **SDUI** home. Same visual leaves (`ui/components`), same device. There is no pass/fail number.

**Trade-off:** I did not invent a 5-trial median or an overhead %. The first capture is in this file. A fair alternate-cold set is still open if we get more logcat.

Numbers below are from a real `adb logcat -s Cars24Perf` capture on 2026-08-16. Nothing here is invented.

## Device and build

| Item | Value |
|---|---|
| Device | Not written in the log (fill model + Android version) |
| Build | Not confirmed from the log (`I Cars24Perf` — treat as whatever APK was installed) |
| App start | Cold = first `*_fully_drawn` after `application_onCreate` on a **new PID** |
| Trials kept | Static cold **n=1**. SDUI cold **n=2**. Target was 5 each — not reached |

## How to read the log

`elapsed_ms` is time since **process start**, not since the last menu tap.

- **Keep:** `application_onCreate` then the next `static_fully_drawn` / `sdui_fully_drawn` on that same PID.
- **Drop:** later `*_fully_drawn` on the same PID (you sat on a screen, then switched). Those 15–29 s lines are idle time, not render time.

## Cold trials (kept)

| PID | Variant | TTR `elapsed_ms` | `parse_ms` | View-build (TTR − parse) |
|---|---|---|---|---|
| 21162 | Static | 1528 | 0 | 1528 |
| 21414 | SDUI | 582 | 18 | 564 |
| 22496 | SDUI | 825 | 20 | 805 |
| 27488 | SDUI (incomplete) | — | 6 | no `fully_drawn` in the paste |

PID 21162 is the first process in the session. That 1528 ms static run includes first-open cost (class load, Coil, ART). The two SDUI cold starts are later processes and look faster for that reason, **not** because the engine is proven cheaper.

## Discarded (same PID, not cold)

| PID | Line | Why dropped |
|---|---|---|
| 21414 | static 15292, sdui 18975 / 20852 / 22427 | Menu switches; clock still running from 09:07:04 |
| 22496 | sdui 11249 / 29372 | Same |

## Results

| Metric | Static | SDUI | Overhead % `(sdui-static)/static` |
|---|---|---|---|
| TTR | 1528 ms (n=1, first-open) | 582 ms, 825 ms (n=2) | **Not reported** — first-open bias, n too small |
| TTI | not measured | not measured | — |
| Full page | not measured | not measured | — |
| Parse | 0 | 18 / 20 / 6 ms (cold) | — |
| View-build | 1528 ms | 564 / 805 ms | **Not reported** (same bias) |
| Scroll jank | not measured | not measured | — |

Parse is cheap on this payload (tens of milliseconds). The open question is view-build, and we do not have a fair pair yet.

## What this does *not* prove

SDUI TTR < static TTR in this paste is **not** “SDUI is faster.” Alternate cold starts (Static → force-stop → SDUI → force-stop → …) five times each, **after** the app has been opened once so first-open is out of the set. Then take medians and fill overhead %.

## Optimize loop

No code change from these numbers. Caching JSON would shave ~20 ms; that is not the unknown. Re-measure fairly before touching the renderer.

## How to capture the next set

```
adb logcat -c
adb logcat Cars24Perf:W *:S
```

Each trial:

```
adb shell am force-stop com.example.cars24clone
adb shell am start -n com.example.cars24clone/.MainActivity
```

Then immediately ⋮ → the variant you are timing. Only the first `*_fully_drawn` after `application_onCreate` counts.
