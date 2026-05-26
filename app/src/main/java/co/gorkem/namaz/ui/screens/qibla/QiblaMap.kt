package co.gorkem.namaz.ui.screens.qibla

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.util.GreatCircleUtil

@Composable
fun QiblaMap(
    userLocation: LocationInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var mapInstance by remember { mutableStateOf<MapLibreMap?>(null) }
    var isSatelliteMode by remember { mutableStateOf(true) } // Varsayılan olarak detaylı uydu
    var initialCameraSet by remember { mutableStateOf(false) }

    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).apply {
            getMapAsync { map ->
                mapInstance = map
            }
        }
    }

    LaunchedEffect(mapInstance, isSatelliteMode) {
        val map = mapInstance ?: return@LaunchedEffect
        val styleUri = if (isSatelliteMode) "asset://raster_satellite_style.json" else "asset://raster_osm_style.json"

        map.setStyle(Style.Builder().fromUri(styleUri)) { style ->
            val kaabaLat = 21.4225
            val kaabaLon = 39.8262

            val points = GreatCircleUtil.getGreatCirclePath(
                userLocation.latitude, userLocation.longitude,
                kaabaLat, kaabaLon
            ).map { Point.fromLngLat(it.first, it.second) }

            val lineString = LineString.fromLngLats(points)
            val feature = Feature.fromGeometry(lineString)
            val source = GeoJsonSource("qibla-line-source", FeatureCollection.fromFeature(feature))
            style.addSource(source)

            val casingLayer = LineLayer("qibla-line-casing-layer", "qibla-line-source").apply {
                setProperties(
                    lineColor("#FFFFFF"), // White outline for contrast
                    lineWidth(8f),
                    lineCap("round"),
                    lineJoin("round")
                )
            }
            style.addLayer(casingLayer)

            val lineLayer = LineLayer("qibla-line-layer", "qibla-line-source").apply {
                setProperties(
                    lineColor("#10B981"), // Emerald green
                    lineWidth(4f),
                    lineCap("round"),
                    lineJoin("round")
                )
            }
            style.addLayer(lineLayer)

            if (!initialCameraSet) {
                val bounds = LatLngBounds.Builder()
                    .include(LatLng(userLocation.latitude, userLocation.longitude))
                    .include(LatLng(kaabaLat, kaabaLon))
                    .build()

                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 1000)
                initialCameraSet = true
            }
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
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = { isSatelliteMode = !isSatelliteMode },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = if (isSatelliteMode) Icons.Default.Map else Icons.Default.Public,
                    contentDescription = "Harita Türü Değiştir"
                )
            }

            FloatingActionButton(
                onClick = {
                    val position = CameraPosition.Builder()
                        .target(LatLng(userLocation.latitude, userLocation.longitude))
                        .zoom(18.0) // Sokak/Bina seviyesi ekstra zoom
                        .build()
                    mapInstance?.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.qibla_set_location)
                )
            }
        }
    }
}
