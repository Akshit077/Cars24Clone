# SDUI schema v1

The client is a renderer. The server (here: a JSON file) owns layout, copy, and interaction. This contract is written for a **second, unseen Cars24 screen**, not only home.

## Document

```json
{
  "schemaVersion": 1,
  "minClientVersion": 1,
  "screen": { "id": "home", "title": "Cars24" },
  "state": { "selectedTab": "all", "usedCarFilter": "recent", "tenureMonths": 24 },
  "lookups": { "emiByTenure": { "12": "₹8,499/mo", "24": "₹4,599/mo" } },
  "sheets": { "loanSheet": { "type": "column", "children": [] } },
  "root": { "type": "column", "children": [] }
}
```

| Field | Role |
|---|---|
| `schemaVersion` | Payload shape. Bump when nodes/actions change meaning. |
| `minClientVersion` | Oldest renderer that may show this payload. Older apps keep the last cached document. |
| `state` | Mutable JSON object. `setState` writes here; `bind` / `visibleIf` read here. |
| `lookups` | Read-only tables (EMI by tenure, labels). Not a formula engine. |
| `sheets` | Named node trees shown by `openSheet`. |
| `root` | The page tree. |

Unknown keys at any level are ignored (`ignoreUnknownKeys`). That is the forward-compat story.

## Node

```json
{
  "id": "rail-suv",
  "type": "carousel",
  "props": { "itemWidth": 260 },
  "style": { "gap": "space.md", "paddingH": "space.md" },
  "bind": {},
  "visibleIf": { "path": "state.selectedTab", "eq": "buy" },
  "actions": [],
  "children": []
}
```

Every node has `type`. Everything else is optional. `props` stay an open object so a new leaf property does not require a client release.

## Registry (primitives only)

**Layout:** `column`, `row`, `spacer`, `section`  
**Collections:** `list`, `grid`, `carousel`  
**Leaves:** `text`, `image`, `icon`, `button`, `chip`, `search`  
**Containers:** `card`, `sheet`, `tabs`

A used-car tile is `card` → `image` + `text` + `chip`. There is no `CarCard`.

Unknown `type` → visible fallback (label + type name). The page does not crash. See `home_unknown_type.json`.

## Props (v1)

| type | props |
|---|---|
| `text` | `text`, `variant`: `title` \| `body` \| `caption` \| `price` \| `onHero` \| `onHeroTitle`, optional `minLines` / `maxLines` |
| `image` | `url`, `aspectRatio` (e.g. `16:9`), `scale`: `crop` \| `fit` |
| `icon` | `name` (client icon set: `location`, `search`, `chevron`) |
| `button` | `text`, `variant`: `primary` \| `secondary` \| `ghost` \| `inverse` |
| `chip` | `text`, `value`, `variant`: `filter` \| `tab` |
| `search` | `placeholder`, `variant`: `default` \| `hero` |
| `spacer` | `size` token |
| `section` | `title`, `titleVariant`, `actionText`, `actionVariant`: `link` \| `badge` |
| `grid` | `columns` |
| `carousel` | `itemWidth` (dp) |
| `card` | — (chrome only; children do the work) |

## Style tokens

Do not sprinkle raw hex unless a token is missing.

- Color: `color.bg`, `color.surface`, `color.primary`, `color.onPrimary`, `color.text`, `color.muted`, `color.border`, `color.hero`, `color.onHero`, `color.orbit`, `color.buyCard`, `color.sellCard`, `color.cream`, `color.badge`
- Space: `space.xs`, `space.sm`, `space.md`, `space.lg`, `space.xl`
- Radius: `radius.sm`, `radius.md`, `radius.lg`

Style fields: `padding`, `paddingH`, `paddingV`, `gap`, `background`, `corner`, `width`, `height`, `insets` (`statusBars`).

## Bind

`bind` maps a prop name to a path string.

| Form | Example | Resolves to |
|---|---|---|
| State path | `state.tenureMonths` | `24` |
| Lookup | `lookups.emiByTenure[state.tenureMonths]` | `"₹4,599/mo"` |

Special case: `bind.selected` on `chip` is a state path. The chip is selected when that value **equals** `props.value`.

```json
{
  "type": "chip",
  "props": { "text": "24 mo", "value": 24 },
  "bind": { "selected": "state.tenureMonths" },
  "actions": [{ "type": "setState", "path": "tenureMonths", "value": 24 }]
}
```

No general expression language. If a surprise screen needs `min(a,b)`, that is a new bind form (client code) or a lookup table (JSON-only).

## visibleIf

```json
{ "path": "state.selectedTab", "eq": "buy" }
{ "path": "state.selectedTab", "in": ["all", "buy"] }
{ "path": "state.selectedTab", "neq": "all" }
```

Missing path → hide. Unknown operator → hide (safe default).

## Actions

| type | Fields | Effect |
|---|---|---|
| `setState` | `path`, `value` | Write `state` (dot path, single segment in v1) |
| `navigate` | `url` | `sdui://car/swift-2019` (client logs / stub route) |
| `openSheet` | `id` | Show `sheets[id]` |
| `closeSheet` | — | Dismiss |

`actions` is an array so one tap can `setState` then `openSheet` later if needed. v1 runs them in order.

## Versioning

1. Client sends `X-SDUI-Client: 1` when a real server exists. Today the file is local.
2. If `minClientVersion` > client version, keep last good document and show a non-blocking banner (not implemented in hour 1; story is enough).
3. Additive change (new optional prop, new unknown type): old clients ignore keys / fallback the node.
4. Breaking change (rename `type`, change action meaning): bump `schemaVersion` **and** `minClientVersion`. Serve a down-converted payload to old clients on the server. Do not fork the renderer.

## What this can express without new client code

Lists, grids, rails, cards, chips that swap rails, bound EMI text, sheets, navigation intents, token styling.

## What needs a new registry entry

Map, video, camera, charts, native payment widgets, a real formula engine.

## Payloads in this repo

| File | Why |
|---|---|
| `app/src/main/assets/sdui/home.json` | Reference screen |
| `app/src/main/assets/sdui/home_unknown_type.json` | Fallback demo |
| `app/src/main/assets/sdui/car_detail_sketch.json` | Same primitives, different page (coverage rehearsal) |
