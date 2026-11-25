package org.setu.placemark

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.CopyrightOverlay
import org.setu.placemark.models.PlacemarkModel

class MapListActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView
    private lateinit var editButton: Button

    private var selectedPlacemark: PlacemarkModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_map_list)

        // Initialise views
        mapView = findViewById(R.id.mapList)
        txtTitle = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace = findViewById(R.id.mapImageView)
        editButton = findViewById(R.id.editMapButton)

        // Configure OpenStreetMap
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Return to main menu
        findViewById<Button>(R.id.backMapButton).setOnClickListener {
            finish()
        }

        // Edit selected placemark
        editButton.setOnClickListener {
            selectedPlacemark?.let { placemark ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("id", placemark.id)
                startActivity(intent)
            }
        }
    }

    private fun loadMarkers() {

        val placemarks = AppData.placemarks.findAll()

        mapView.overlays.clear()
        mapView.overlays.add(CopyrightOverlay(this))

        val selectedId = selectedPlacemark?.id
        selectedPlacemark = null
        editButton.isEnabled = false

        txtTitle.text = ""
        txtDescription.text = ""
        imgPlace.setImageDrawable(null)

        // Handle empty lists
        if (placemarks.isEmpty()) {
            txtTitle.text = "No placemarks saved"
            mapView.invalidate()
            return
        }

        txtTitle.text = "Select a marker"

        val positions = ArrayList<GeoPoint>()

        placemarks.forEach { placemark ->

            val position = GeoPoint(
                placemark.x,
                placemark.y
            )

            positions.add(position)

            val marker = Marker(mapView)

            marker.position = position
            marker.title = placemark.title
            marker.snippet = placemark.description

            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            // Handle marker selection
            marker.setOnMarkerClickListener { _, _ ->
                displayPlace(placemark)
                true
            }

            mapView.overlays.add(marker)

            if (placemark.id == selectedId) {
                displayPlace(placemark)
            }
        }

        // Automatically zoom to show all markers
        mapView.post {
            if (positions.distinctBy {
                    Pair(it.latitude, it.longitude)
                }.size == 1
            ) {
                mapView.controller.setZoom(12.0)
                mapView.controller.setCenter(positions.first())
            } else {
                val boundingBox = BoundingBox.fromGeoPoints(positions)
                mapView.zoomToBoundingBox(boundingBox, false, 50)
            }

            mapView.invalidate()
        }
    }

    private fun displayPlace(placemark: PlacemarkModel) {

        selectedPlacemark = placemark
        editButton.isEnabled = true

        txtTitle.text = placemark.title
        txtDescription.text = placemark.description

        if (placemark.imageUri != null) {
            imgPlace.setImageURI(
                Uri.parse(placemark.imageUri)
            )
        } else {
            imgPlace.setImageDrawable(null)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        loadMarkers()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
    }
}