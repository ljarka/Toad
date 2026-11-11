# Toad — Logo Concept

**Version:** 1.0  
**Author:** Zosia (Designer)

---

## Concept

A modern, flat-design Amanita muscaria (fly agaric) mushroom. The subject is immediately recognizable — the red cap with white spots is one of the most iconic natural shapes in the world. The design strips it to its essential geometry: a dome cap on a short stem, without botanical detail or shadow. It reads clearly at launcher-icon scale (48dp) and large sizes alike.

The deliberate reference to Toad from Mario is a wink, not a copy — this is not a cartoon character, it's a clean vector interpretation of the actual mushroom that the character was based on.

---

## Vector Drawable Specification

**Canvas:** 108×108dp (Android adaptive icon foreground layer size)  
**Safe zone (visible circle):** 72dp centered — all key shapes within this circle  
**Background layer:** `#2D6A4F` (forest green), full 108×108dp fill

### Stem

- Shape: rounded rectangle
- Width: 28dp
- Height: 24dp
- Corner radius: 6dp (top corners: 2dp; bottom corners: 6dp — slightly wider base)
- Color: `#F5F0E8` (cream/parchment)
- Vertical skirt detail (optional): a thin crescent shape at the base of the cap overlapping the stem top, same cream color, 32dp wide × 6dp tall, center-aligned — represents the volva/ring of a real Amanita
- Position: horizontally centered, bottom edge at y=78dp (within 108dp canvas)

### Cap

- Shape: ellipse clipped to upper half (dome)
- Full ellipse: width=68dp, height=52dp
- Show only top half: clip rect from y=0 to y=26dp (the dome)
- Color: `#C0392B` (mushroom red)
- Position: horizontally centered, dome bottom edge meets stem top at approximately y=52dp

### Cap rim (bottom edge)

- A thin filled ellipse at the bottom edge of the cap creates a subtle shadow line
- Full ellipse: width=68dp, height=10dp
- Color: `#962D22` (darkened red, 20% darker than cap)
- Layered behind the main cap dome, visible only as a shadow fringe at the base of the dome

### White spots (polka dots)

5 dots total. Sizes and positions (relative to cap center):

| Dot | Diameter | Offset X | Offset Y |
|-----|----------|----------|----------|
| Center top | 14dp | 0 | -14dp |
| Upper left | 10dp | -18dp | -6dp |
| Upper right | 10dp | +18dp | -6dp |
| Lower left | 8dp | -10dp | +6dp |
| Lower right | 8dp | +10dp | +6dp |

Color: `#FFFFFF`  
Shape: circle (filled ellipse, equal width/height)

Spot positions are approximate — the visual goal is a natural, slightly irregular clustering, not a perfect grid. Adjust during implementation so no spot overlaps the cap edge.

---

## Color Summary

| Part | Hex |
|---|---|
| Background layer | `#2D6A4F` |
| Cap | `#C0392B` |
| Cap rim shadow | `#962D22` |
| Stem + skirt | `#F5F0E8` |
| Spots | `#FFFFFF` |

---

## Android Implementation Notes

- Implement as `res/drawable/ic_launcher_foreground.xml` (vector drawable)
- Background: `res/drawable/ic_launcher_background.xml` — solid `#2D6A4F`
- Adaptive icon manifest entry in `res/mipmap-anydpi-v26/ic_launcher.xml`
- For round icon: same foreground, circular mask is applied by Android — no changes needed
- For notification icon: use a simplified monochrome version — white cap silhouette only, no spots, no color

---

## What NOT to do

- Do not add gradient fills — the flat design reads better at small sizes
- Do not add eyes or anthropomorphic features — this is a mushroom, not a character
- Do not outline every shape with a stroke — the shapes read cleanly without outlines
- Do not scale the stem taller than the cap — the cap should dominate
