# Toad — Design Review

**Reviewer:** Marta (PM)  
**Date:** 2026-11-11  
**Deliverables reviewed:** BRAND.md, logo-concept.md

---

## What's Done

- **Color palette** — full Material You token system defined for both dark and light themes; all semantic roles covered (primary, secondary, tertiary/accent, background, surface, error, text); hex values provided
- **Typography** — complete type scale (Display → Caption) with font choices (Fraunces + DM Sans), weights, sizes, and line-height rationale; Google Fonts delivery method specified
- **Iconography** — icon style (Material Symbols Rounded), filled vs outlined usage rules, size table per context, custom icon requirements listed
- **Motion spec** — spring animation parameters, entry/exit patterns, route transition types defined
- **Tone of voice** — register, pronoun form, example copy for empty states and error states; list of words to avoid
- **Logo concept** — Amanita muscaria, flat design; full vector drawable specification: canvas size, safe zone, all shapes with dimensions, colors, and positions; Android adaptive icon implementation notes; notification icon variant specified

---

## What's Missing

1. **No logo file** — only a written spec exists; no SVG, PNG, or `.xml` vector drawable is provided. The engineer must interpret and build from scratch, which risks divergence from intent.
2. **Light theme token table incomplete** — BRAND.md says "invert the value scale" but does not provide explicit hex values for each surface/background token in light mode. `Theme.kt` implementation will require guessing.
3. **No spacing / grid spec** — no mention of base grid unit (assumed 8dp), screen-edge margins, or component padding rules. This gap will cause inconsistency across screens.
4. **No component examples or mockups** — no annotated screens showing typography, color, and spacing in real context. Without a reference, the first screen the engineer builds may not match the design intent.
5. **Custom mushroom icon spec is vague on stroke/weight** — logo-concept.md says the custom mushroom vector "must match Material Symbols Rounded weight/opticalSize" but does not specify which weight (100–700) or optical size (20–48). This needs to be pinned.

---

## Suggestions

1. **Provide the logo as a vector file** — Zosia should export `ic_launcher_foreground.xml` directly; the written spec is excellent but an actual file eliminates ambiguity and saves 1–2 engineer hours.
2. **Add an explicit light theme token table** — copy the dark table structure, fill in the light values; even approximate values are better than "invert the scale."
3. **Define a spacing system in one sentence** — e.g. "8dp base grid; 16dp screen margins; 8dp internal card padding." Add it as a new `## Spacing` section in BRAND.md.
4. **Add one annotated mockup** — a single screen (e.g. the map screen) showing typography levels, color tokens, and icon sizes in context would let the engineer self-check without asking questions.
5. **Pin Material Symbols weight and optical size** — add `weight=400, opticalSize=24` (or chosen values) to the Iconography section so the custom mushroom icon can be matched precisely.

---

## Overall Assessment

The brand direction is strong and well-thought-out. BRAND.md is production-quality for color, typography, motion, and tone. logo-concept.md is thorough enough that an engineer can build the icon — but a real file would be better. The main gaps are spacing system and light theme tokens; both are small and can be added in a follow-up. No blockers for starting Phase 0 technical spikes. Engineering can proceed with the dark theme; light theme and spacing can be nailed down before Phase 1 screens.

**Recommended next step:** Ask Zosia to deliver (1) the logo XML or SVG, (2) explicit light theme token values, and (3) a spacing spec — before the first feature screen goes to implementation.
