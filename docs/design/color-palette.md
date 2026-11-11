# Toad — Color Palette

**Version:** 1.0  
**Author:** Zosia (Designer)

---

## Palette Overview

Theme direction: dark forest. Rich, earthy, organic. Designed for outdoor use — high contrast, never harsh.

---

## Primary — Forest Green

```
████████████████  #2D6A4F  primary
```
Deep, saturated forest green. Used for: primary buttons, FAB, active navigation item, links.

```
████████████████  #1B4332  onPrimaryContainer / darkest green
```
Used for: text on light green surfaces, pressed/active state.

```
████████████████  #B7E4C7  primaryContainer
```
Mint-light green. Used for: chip backgrounds, success banners, light accent fills.

---

## Secondary — Earthy Brown

```
████████████████  #6B4226  secondary
```
Warm dark brown. Used for: secondary buttons, icons in context.

```
████████████████  #8B5E3C  secondaryVariant
```
Lighter brown. Used for: hover/pressed state, dividers in brown-toned surfaces.

```
████████████████  #D4B896  secondaryContainer
```
Parchment-brown. Used for: card surfaces that need warmth (trip cards, photo detail backgrounds).

---

## Accent — Mushroom Red

```
████████████████  #C0392B  tertiary / mushroom cap
```
Deep fly-agaric red. Used for: logo, found-mushroom badge, danger states that feel thematic rather than alarming.

```
████████████████  #E74C3C  tertiaryVariant
```
Brighter red. Used for: active pin on map, urgent state, "new find" notification dot.

```
████████████████  #FFDAD5  tertiaryContainer
```
Faint blush. Used for: error/warning banners (lighter feel than harsh red).

---

## Background & Surface (Dark Theme)

```
████████████████  #0D1B0F  background
```
Near-black forest green. App background. Almost black but with green undertone — avoids flat tech-black.

```
████████████████  #111A13  surface
```
Slightly lighter. Cards, bottom nav, top app bar.

```
████████████████  #1E2F20  surfaceVariant
```
Elevated surface. Input fields, secondary cards, bottom sheet handle area.

```
████████████████  #2A3D2C  surfaceContainerHigh
```
Most elevated surface. Dialogs, dropdowns, tooltips.

---

## Text (Dark Theme)

```
████████████████  #E8F0E9  onBackground
```
Almost-white with green tint. Primary body text.

```
████████████████  #F5F0E8  parchment
```
Warm cream. High-emphasis display text, section headers, empty state headlines.

```
████████████████  #A8BEA9  onSurfaceVariant
```
Muted sage. Secondary text, captions, timestamps, helper text.

---

## Background & Surface (Light Theme)

```
████████████████  #F4F7F4  background
```
Near-white with faint green tint.

```
████████████████  #FFFFFF  surface
```
Pure white. Cards, sheets.

```
████████████████  #E8F0E9  surfaceVariant
```
Very light green. Input fields, secondary cards.

---

## Semantic Colors

```
████████████████  #CF6679  error
```
Muted rose-red. Error states. Less alarming than pure red; fits the forest palette.

```
████████████████  #95C8A1  success (custom)
```
Muted forest green. "Photo uploaded", "sync complete", "mushroom added" confirmations.

```
████████████████  #F9A825  warning (custom)
```
Amber. "GPS signal weak", "offline mode", "storage low".

---

## Usage Context Summary

| Color | Where you see it |
|---|---|
| `#2D6A4F` primary | "Zapisz", FAB, active tab, map route line |
| `#C0392B` tertiary | Logo, map find pins, mushroom species badge (edible → green, poisonous → red) |
| `#0D1B0F` background | App background, photo viewer backdrop |
| `#F5F0E8` parchment | Display text on dark, empty state headings |
| `#A8BEA9` muted | Timestamps, coordinates, secondary metadata |
| `#D4B896` warm surface | Trip detail cards, offline banners |

---

## Do Not Use

- Pure `#000000` black — too harsh; use `#0D1B0F` instead
- Pure `#FFFFFF` white on green backgrounds — use `#F5F0E8` parchment for a warmer feel
- Saturated neons — they break the organic forest mood
- More than 3 colors in a single card or list item
