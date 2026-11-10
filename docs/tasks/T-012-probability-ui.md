# T-012 — Probability display on home screen and map

**Status:** ready  
**Phase:** Etap 6  
**Depends on:** T-008, T-011

## Goal

Show mushroom probability score on the home screen (card) and as a heat layer on the community map.

## Design

### Home screen probability card

Located between the season species section and the community feed.

```kotlin
@Composable
fun ProbabilityCard(result: ProbabilityResult, modifier: Modifier = Modifier) {
    Card(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProbabilityGauge(score = result.score)  // animated arc 0–100
            Column {
                Text(result.label.displayName())     // e.g. "Dobre warunki"
                Text("Szansa na grzyby: ${result.score}%", style = MaterialTheme.typography.bodySmall)
            }
        }
        TextButton("Szczegóły") { /* expand to show factor breakdown */ }
        AnimatedVisibility(expanded) {
            FactorBreakdownTable(result.factors)
        }
    }
}
```

Gauge: `Canvas` arc, animated with `animateFloatAsState` on score change.

Color scheme:
| Label | Color |
|---|---|
| LOW | Red 400 |
| MODERATE | Orange 300 |
| GOOD | Light Green 400 |
| EXCELLENT | Green 600 |

`ProbabilityLabel.displayName()`:
- LOW → "Małe szanse"
- MODERATE → "Umiarkowane"
- GOOD → "Dobre warunki"
- EXCELLENT → "Doskonałe warunki"

### Map heat layer

On the community map (T-010), add a toggle "Pokaż prawdopodobieństwo". When toggled:
1. Load probability scores on a 50 km grid for the visible viewport (compute for each grid point using `ProbabilityEngine` with Open-Meteo data fetched per point — cache aggressively; max 9 points per viewport).
2. Build `HeatmapLayer` from scores.

```kotlin
val heatmapSource = GeoJsonSource("probability-heatmap", heatmapFeatures)
val heatmapLayer = HeatmapLayer("probability-heatmap-layer", "probability-heatmap").apply {
    setProperties(
        heatmapWeight(get("score")),
        heatmapIntensity(0.6f),
        heatmapColor(interpolate(linear(), heatmapDensity(),
            stop(0, rgba(0, 0, 255, 0)),
            stop(0.4, rgba(255, 165, 0, 0.5)),
            stop(1, rgba(0, 128, 0, 0.8))
        )),
        heatmapRadius(80f)
    )
}
```

### New files
- `ui/home/ProbabilityCard.kt`
- `ui/home/ProbabilityGauge.kt`
- `ui/map/ProbabilityHeatLayer.kt`

## Acceptance criteria

- [ ] Probability card renders on home screen with correct color for each label
- [ ] Gauge animates when score value changes
- [ ] Factor breakdown expandable section shows all 5 factors
- [ ] "Pokaż prawdopodobieństwo" toggle on map adds heat layer
- [ ] Heat layer updates when panning to a new area
