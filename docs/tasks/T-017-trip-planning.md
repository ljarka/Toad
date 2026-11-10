# T-017 — Trip planning (pre-trip route sketch)

**Status:** draft  
**Phase:** Etap 8  

## Goal

Before going to the forest, the user can draw a planned route on the map, add waypoints, and save it.
During the trip the planned route is shown as a dashed overlay alongside the GPS-recorded track.

## Open questions

1. Route drawing UX: sequential taps to place waypoints, or freehand drawing?
2. Should planned routes be shareable with other users?
3. How closely does the actual track need to follow the plan to show deviation warnings?

## Rough scope

- "Planuj wycieczkę" button on the map
- Tap to place ordered waypoints on the map
- Drag waypoints to reposition
- "Zapisz plan" → `planned_trips` table in Supabase
- During live recording: show planned route as dashed blue line
- After trip: "Zgodność z planem X%" stat on trip detail screen

**Not starting this task until the open questions above are resolved.**
