package com.github.ljarka.toad.ui.map

import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression.get
import org.maplibre.android.style.expressions.Expression.heatmapDensity
import org.maplibre.android.style.expressions.Expression.interpolate
import org.maplibre.android.style.expressions.Expression.linear
import org.maplibre.android.style.expressions.Expression.literal
import org.maplibre.android.style.expressions.Expression.rgba
import org.maplibre.android.style.layers.HeatmapLayer
import org.maplibre.android.style.layers.PropertyFactory.heatmapColor
import org.maplibre.android.style.layers.PropertyFactory.heatmapIntensity
import org.maplibre.android.style.layers.PropertyFactory.heatmapRadius
import org.maplibre.android.style.layers.PropertyFactory.heatmapWeight
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val HEATMAP_SOURCE_ID = "probability-heatmap"
private const val HEATMAP_LAYER_ID = "probability-heatmap-layer"

fun updateHeatmapLayer(style: Style, points: List<HeatmapPoint>) {
    val features = points.map { p ->
        Feature.fromGeometry(Point.fromLngLat(p.lon, p.lat)).apply {
            addNumberProperty("score", p.weight)
        }
    }
    val collection = FeatureCollection.fromFeatures(features)

    val existingSource = style.getSource(HEATMAP_SOURCE_ID) as? GeoJsonSource
    if (existingSource != null) {
        existingSource.setGeoJson(collection)
        return
    }

    style.addSource(GeoJsonSource(HEATMAP_SOURCE_ID, collection))
    style.addLayer(
        HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID).apply {
            setProperties(
                heatmapWeight(get("score")),
                heatmapIntensity(0.6f),
                heatmapColor(
                    interpolate(
                        linear(),
                        heatmapDensity(),
                        literal(0.0), rgba(0.0, 0.0, 255.0, 0.0),
                        literal(0.4), rgba(255.0, 165.0, 0.0, 0.5),
                        literal(1.0), rgba(0.0, 128.0, 0.0, 0.8),
                    ),
                ),
                heatmapRadius(80f),
            )
        },
    )
}

fun removeHeatmapLayer(style: Style) {
    if (style.getLayer(HEATMAP_LAYER_ID) != null) style.removeLayer(HEATMAP_LAYER_ID)
    if (style.getSource(HEATMAP_SOURCE_ID) != null) style.removeSource(HEATMAP_SOURCE_ID)
}
