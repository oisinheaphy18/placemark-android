package org.setu.placemark.models

import java.util.concurrent.atomic.AtomicLong
import timber.log.Timber

class PlacemarkMemStore {
    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacemarkModel> {
        Timber.d("findAll: ${placemarks.size} placemarks")
        return placemarks
    }

    fun create(placemark: PlacemarkModel) {
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        Timber.d("create: id=${placemark.id}, title=${placemark.title}")
    }

    fun update(placemark: PlacemarkModel): Boolean {
        val found = findOne(placemark.id)
        if (found == null) {
            Timber.w("update: id=${placemark.id} not found")
            return false
        }
        found.title = placemark.title
        found.description = placemark.description
        found.x = placemark.x
        found.y = placemark.y
        Timber.d("update: id=${found.id}, title=${found.title}")
        return true
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id)
        if (found == null) {
            Timber.w("delete: id=$id not found")
            return false
        }
        Timber.d("delete: id=$id")
        return placemarks.remove(found)
    }

    fun findOne(id: Long): PlacemarkModel? = placemarks.find { it.id == id }
}