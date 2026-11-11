# Toad — Brand Identity

**Version:** 1.0  
**Author:** Zosia (Designer)  
**Date:** 2026-11-11

---

## Theme Direction

Dark forest. Earthy, organic, grounded. The visual language of moss, bark, damp undergrowth, and shafts of light breaking through a canopy. Slightly playful — this is a joyful hobby — but never childish. The app is for adult hunters who take their craft seriously.

Style reference: Duolingo's clarity and confidence, applied to a forest palette. Clean layouts, bold illustration accent, no visual noise.

---

## Color Palette

### Primary — Forest Green
| Token | Hex | Usage |
|---|---|---|
| `primary` | `#2D6A4F` | Buttons, active states, key UI chrome |
| `onPrimary` | `#FFFFFF` | Text/icons on primary surfaces |
| `primaryContainer` | `#B7E4C7` | Chip backgrounds, light fills |
| `onPrimaryContainer` | `#1B4332` | Text on primaryContainer |

### Secondary — Earthy Brown
| Token | Hex | Usage |
|---|---|---|
| `secondary` | `#6B4226` | Secondary actions, highlights |
| `onSecondary` | `#FFFFFF` | Text on secondary |
| `secondaryContainer` | `#D4B896` | Warm surfaces (cards, bottom sheets) |
| `onSecondaryContainer` | `#3E2007` | Text on secondaryContainer |

### Accent — Mushroom Red
| Token | Hex | Usage |
|---|---|---|
| `tertiary` | `#C0392B` | Logo accent, success state mushroom found, danger variant |
| `onTertiary` | `#FFFFFF` | Text on tertiary |
| `tertiaryContainer` | `#FFDAD5` | Light red fills |
| `onTertiaryContainer` | `#410001` | Text on tertiaryContainer |

### Background & Surface
| Token | Hex | Usage |
|---|---|---|
| `background` | `#0D1B0F` | App background (dark theme) |
| `onBackground` | `#E8F0E9` | Primary text on background |
| `surface` | `#111A13` | Card surfaces, bottom nav |
| `onSurface` | `#E0EBE1` | Text on surfaces |
| `surfaceVariant` | `#1E2F20` | Secondary card surfaces, input fields |
| `outline` | `#4A6741` | Dividers, borders |

### Text
| Token | Hex | Usage |
|---|---|---|
| `onSurfaceVariant` | `#A8BEA9` | Secondary / helper text |
| Cream / Parchment | `#F5F0E8` | High-contrast labels, offline/error overlays |

### Error
| Token | Hex | Usage |
|---|---|---|
| `error` | `#CF6679` | Error states, destructive actions |
| `onError` | `#FFFFFF` | — |

### Light Theme
Invert the value scale: `background → #F4F7F4`, `surface → #FFFFFF`, `primary → #2D6A4F` (unchanged — works on both). Provide light-theme variants for all surface tokens in `Theme.kt`.

**Recommendation:** Dark theme is the primary experience. The app is used outdoors in forests where bright screens are disruptive. Light theme is secondary/system-pref fallback.

---

## Typography

Font stack: **Google Fonts via Gradle plugin** (`com.google.fonts:provider`).

| Role | Font | Weight | Size |
|---|---|---|---|
| Display (splash, empty states) | **Fraunces** | Bold 700 | 32sp |
| Headline (screen titles) | **Fraunces** | SemiBold 600 | 24sp |
| Title (card headers, section headers) | **Fraunces** | Medium 500 | 18sp |
| Body (descriptions, guide text) | **DM Sans** | Regular 400 | 16sp |
| Body small | **DM Sans** | Regular 400 | 14sp |
| Label (chips, tags, metadata) | **DM Sans** | Medium 500 | 12sp |
| Caption (timestamps, secondary meta) | **DM Sans** | Regular 400 | 11sp |

**Rationale:**  
Fraunces has an organic, slightly old-fashioned serif quality — it reads like a field guide, not a tech app. DM Sans is clean and legible at small sizes, good for dense metadata (coordinates, timestamps, species names).

Line height: `body` and larger → 1.5× size. `label` and `caption` → 1.3× size.

---

## Logo

See `logo-concept.md` for detailed vector spec.

**Summary:** Amanita muscaria (fly agaric) mushroom. Red cap with white polka dots. Modern, flat, clean. Recognizable at 48×48dp. No text in the icon itself.

**Usage rules:**
- Minimum size: 48×48dp (launcher icon)
- Do not add drop shadows to the logo outside of system-drawn launcher elevation
- Do not recolor the logo — the red cap + white spots is the identity
- Do not place the logo on red or dark-red backgrounds (clashes with cap)
- Safe background colors: `#0D1B0F` (dark), `#2D6A4F` (green), `#FFFFFF` (white)
- Logo + wordmark lockup (if needed): "Toad" in Fraunces Bold, forest green, left of or below the icon

---

## Iconography

**Style:** Filled icons for navigation and primary actions; outlined icons for secondary/contextual actions. This matches Material Symbols defaults.

**Icon set:** Material Symbols (Rounded variant) — consistent with Jetpack Compose `Icons.Rounded.*`.

**Custom icons needed:**
- Mushroom (find icon, map pin) — custom vector drawable; style must match Material Symbols Rounded weight/opticalSize
- GPS track / route — can use `Icons.Rounded.Route` or similar

**Icon sizes:**
- Navigation bar: 24dp
- FAB: 24dp
- List/card: 20dp
- Inline / chip: 16dp

---

## Motion

- Transitions: `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)` as default
- Entry: items slide up + fade in, staggered 40ms per item
- Exit: fade out, 150ms
- Route transitions: container transform for detail screens; fade-through for tab switches
- Loading: circular progress in `primary` color; no skeleton screens for first load (data loads fast on local network)

---

## Tone of Voice (UI copy)

- Warm, direct, slightly wry. Not corporate.
- Use "ty" form throughout ("Zapisz swoją trasę", not "Proszę zapisać trasę")
- Empty states tell a story: "Nie znaleziono grzybów? To jeszcze nie znaczy, że ich tu nie ma."
- Error states are honest: "Coś poszło nie tak. Spróbuj jeszcze raz." — no vague "unknown error"
- Avoid: "Please", "successfully", "click here"
