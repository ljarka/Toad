package com.github.ljarka.toad.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.ljarka.toad.data.supabase.MapPhotoDto
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textField
import org.maplibre.android.style.layers.PropertyFactory.textIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.textSize
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonOptions
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

private const val SOURCE_ID = "photos-source"
private const val LAYER_CLUSTERS = "cluster-circles"
private const val LAYER_CLUSTER_COUNT = "cluster-count"
private const val LAYER_PINS = "photo-pins"

private val MAP_STYLE_JSON = """
{
  "version": 8,
  "glyphs": "https://demotiles.maplibre.org/font/{fontstack}/{range}.pbf",
  "sources": {
    "osm": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "maxzoom": 19,
      "attribution": "© OpenStreetMap contributors"
    }
  },
  "layers": [
    { "id": "osm", "type": "raster", "source": "osm" }
  ]
}
""".trimIndent()

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }
    var photoSource by remember { mutableStateOf<GeoJsonSource?>(null) }
    var loadedStyle by remember { mutableStateOf<Style?>(null) }

    LaunchedEffect(uiState.photos, photoSource) {
        val source = photoSource ?: return@LaunchedEffect
        source.setGeoJson(buildFeatureCollection(uiState.photos))
    }

    LaunchedEffect(uiState.showHeatmap, uiState.heatmapPoints, loadedStyle) {
        val style = loadedStyle ?: return@LaunchedEffect
        if (uiState.showHeatmap && uiState.heatmapPoints.isNotEmpty()) {
            updateHeatmapLayer(style, uiState.heatmapPoints)
        } else if (!uiState.showHeatmap) {
            removeHeatmapLayer(style)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { _ ->
                mapView.apply {
                    getMapAsync { map ->
                        setupMap(map, context, viewModel,
                            onSourceReady = { source -> photoSource = source },
                            onStyleReady = { style -> loadedStyle = style },
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        FilterChip(
            selected = uiState.showHeatmap,
            onClick = { viewModel.toggleHeatmap() },
            label = { Text("Pokaż prawdopodobieństwo") },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        )
    }

    uiState.selectedPin?.let { pin ->
        PhotoPinBottomSheet(pin = pin, onDismiss = viewModel::clearSelection)
    }
}

private fun buildFeatureCollection(photos: List<MapPhotoDto>): FeatureCollection {
    val features = photos.map { photo ->
        Feature.fromGeometry(Point.fromLngLat(photo.lon, photo.lat)).apply {
            addStringProperty("id", photo.id)
            addStringProperty("species", photo.speciesNamePl ?: "Nieznany gatunek")
            addStringProperty("username", photo.username ?: "")
            addStringProperty("taken_at", photo.takenAt ?: "")
            addStringProperty("caption", photo.caption ?: "")
        }
    }
    return FeatureCollection.fromFeatures(features)
}

private fun setupMap(
    map: MapLibreMap,
    context: Context,
    viewModel: MapViewModel,
    onSourceReady: (GeoJsonSource) -> Unit,
    onStyleReady: (Style) -> Unit,
) {
    map.setStyle(Style.Builder().fromJson(MAP_STYLE_JSON)) { style ->
        enableLocationIfGranted(map, context, style)

        val source = GeoJsonSource(
            SOURCE_ID,
            FeatureCollection.fromFeatures(emptyList()),
            GeoJsonOptions()
                .withCluster(true)
                .withClusterMaxZoom(14)
                .withClusterRadius(50),
        )
        style.addSource(source)
        onSourceReady(source)

        addLayers(style)
        onStyleReady(style)

        map.addOnCameraIdleListener {
            val bounds = map.projection.visibleRegion.latLngBounds
            viewModel.onViewportChanged(
                bounds.longitudeWest, bounds.latitudeSouth,
                bounds.longitudeEast, bounds.latitudeNorth,
            )
        }

        map.addOnMapClickListener { latLng ->
            val screenPoint = map.projection.toScreenLocation(latLng)
            val features = map.queryRenderedFeatures(screenPoint, LAYER_CLUSTERS, LAYER_PINS)
            if (features.isNotEmpty()) {
                val feature = features[0]
                if (feature.getBooleanProperty("cluster") == true) {
                    val newZoom = (map.cameraPosition.zoom + 2.0).coerceAtMost(22.0)
                    map.animateCamera(
                        org.maplibre.android.camera.CameraUpdateFactory
                            .newLatLngZoom(latLng, newZoom),
                    )
                } else {
                    viewModel.selectPin(
                        SelectedPin(
                            id = feature.getStringProperty("id") ?: "",
                            speciesName = feature.getStringProperty("species") ?: "Nieznany gatunek",
                            username = feature.getStringProperty("username") ?: "",
                            takenAt = feature.getStringProperty("taken_at").takeIf { !it.isNullOrEmpty() },
                            caption = feature.getStringProperty("caption").takeIf { !it.isNullOrEmpty() },
                        ),
                    )
                }
                true
            } else {
                false
            }
        }

        map.moveCamera(
            org.maplibre.android.camera.CameraUpdateFactory
                .newLatLngZoom(org.maplibre.android.geometry.LatLng(52.0, 19.0), 6.0),
        )
    }
}

private fun enableLocationIfGranted(map: MapLibreMap, context: Context, style: Style) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) return
    try {
        val lc = map.locationComponent
        lc.activateLocationComponent(
            LocationComponentActivationOptions.builder(context, style).build(),
        )
        lc.isLocationComponentEnabled = true
        lc.cameraMode = CameraMode.NONE
        lc.renderMode = RenderMode.COMPASS
    } catch (_: Exception) {
        // Location not critical; swallow if unavailable
    }
}

private fun addLayers(style: Style) {
    style.addLayer(
        CircleLayer(LAYER_CLUSTERS, SOURCE_ID).apply {
            setFilter(Expression.has("point_count"))
            setProperties(
                circleColor("#43A047"),
                circleRadius(28f),
                circleOpacity(0.85f),
                circleStrokeColor("#ffffff"),
                circleStrokeWidth(2f),
            )
        },
    )

    style.addLayer(
        SymbolLayer(LAYER_CLUSTER_COUNT, SOURCE_ID).apply {
            setFilter(Expression.has("point_count"))
            setProperties(
                textField("{point_count}"),
                textSize(14f),
                textColor("#ffffff"),
                textIgnorePlacement(true),
                textAllowOverlap(true),
            )
        },
    )

    style.addLayer(
        CircleLayer(LAYER_PINS, SOURCE_ID).apply {
            setFilter(Expression.not(Expression.has("point_count")))
            setProperties(
                circleColor("#2E7D32"),
                circleRadius(10f),
                circleStrokeColor("#ffffff"),
                circleStrokeWidth(2f),
            )
        },
    )
}
