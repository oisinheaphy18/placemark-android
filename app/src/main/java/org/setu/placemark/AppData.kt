package org.setu.placemark

import android.content.Context
import java.io.File
import org.setu.placemark.models.PlacemarkMemStore

object AppData {

    lateinit var placemarks: PlacemarkMemStore
        private set

    fun initialize(context: Context) {
        val file = File(
            context.filesDir,
            "place_list.json"
        )

        placemarks = PlacemarkMemStore(file)
    }
}