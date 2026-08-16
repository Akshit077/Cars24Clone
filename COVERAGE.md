# Coverage

What this renderer can express as **JSON only**, and what needs a new registry entry. Written for the surprise-screen interview: a new Cars24 page should not require Kotlin unless a primitive is actually missing.

App chrome (overflow menu, snackbar host) is not schema. Everything below is the SDUI tree.

## Registry inventory

| Kind | Types | Used on home | Used on car detail |
|---|---|---|---|
| Layout | `column`, `row`, `spacer`, `section` | yes | yes (`spacer` unused there) |
| Collections | `list`, `grid`, `carousel` | `grid`, `carousel` | `grid`, `carousel` |
| Leaves | `text`, `image`, `icon`, `button`, `chip`, `search` | all except `icon` | `text`, `image`, `button`, `chip` |
| Containers | `card`, `sheet`, `tabs` | `card`; sheets via `openSheet` | same |

Registered but unused in the three payloads: `list`, `icon`, `tabs` (home tabs are `chip` + `variant: tab` on a `row`). They stay in the registry so a surprise page can use them without a client release.

Unknown `type` → `FallbackNode`. Demo: `liveAuctionTicker` in `home_unknown_type.json`.

## JSON-only patterns (no new Kotlin)

These are proven in-repo, not hypothetical.

| Pattern | Where |
|---|---|
| Header search | `search` + `variant: hero` |
| Tabs that swap sections | `setState` `selectedTab` + `visibleIf` `in` / `eq` |
| 3-column service grid | `grid` `columns: 3` + `card` |
| Horizontal rails | `carousel` + `card` |
| Filter chips that swap a rail | `usedCarFilter` + two carousels with `visibleIf` |
| Bound EMI | `bind` `lookups.emiByTenure[state.tenureMonths]` |
| Sheet | `openSheet` / `closeSheet` (`loanSheet`, `addVehicleSheet`) |
| Navigation intent | `navigate` `sdui://…` (snackbar stub) |
| Token styling | `color.hero`, `color.orbit`, `space.*`, `radius.*` |
| Status-bar inset | `style.insets: statusBars` |
| Second page | `car_detail_sketch.json` — gallery, spec grid, same EMI + sheet |

A new Cars24 landing block (e.g. “Insurance plans”) is another `section` + `carousel`/`grid` in JSON.

## Honest JSON-only %

There is no precise industry number. This is the estimate I would defend in the interview.

| Surface | JSON-only | Needs client |
|---|---|---|
| This home (as designed) | **~95%** | Overflow menu, snackbar, Coil, status-bar chrome |
| Another Cars24 *browse* page (listing, sell steps, loan explainer) | **~70–85%** | If it is still rails, grids, cards, chips, sheets |
| Car detail as sketched | **~90%** | Same engine; no map/video |
| Store locator, 360° spin, live auction, native payment, camera KYC | **~0–20%** | New leaves: map, video, camera, payment, maybe a formula bind |

I am **not** claiming 95% of all future Cars24. I am claiming this home and pages like it.

`car_detail_sketch.json` was the rehearsal: I did not add `CarGallery` or `SpecGrid`. If the surprise screen is “hub page with a map,” the honest answer is: JSON can do the list and the CTA; the map is a new `type`.

## Gaps (new registry entry)

| Missing | Why JSON cannot fake it |
|---|---|
| Map | Needs a native map SDK |
| Video / 360 | Needs a player, not `image` |
| Camera | Capture pipeline |
| Native payment | PCI / SDK |
| Charts | Drawing surface |
| General formulas | We have lookups only. `min(a,b)` is a new bind form or a precomputed table |
| Real navigation | `navigate` is a snackbar |
| Hot-reload assets | Edit JSON + rerun; no file watcher |

## What I would say in the surprise-screen round

1. Draw the page as `column` → `section` / `carousel` / `grid` / `card`.
2. If a node is “a car” or “a banner,” it is still `card` + children.
3. If they ask for a map pin, that is the first new primitive — and I would show it as a fallback until the leaf ships.
