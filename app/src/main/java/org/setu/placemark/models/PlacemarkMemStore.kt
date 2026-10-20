package org.setu.placemark.models

import java.util.concurrent.atomic.AtomicLong

class PlacemarkMemStore {
    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<PlacemarkModel> = placemarks

    fun create(placemark: PlacemarkModel) {
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
    }

    fun update(placemark: PlacemarkModel): Boolean {
        val found = findOne(placemark.id) ?: return false
        found.title = placemark.title
        found.description = placemark.description
        found.x = placemark.x
        found.y = placemark.y
        return true
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id) ?: return false
        return placemarks.remove(found)
    }

    fun findOne(id: Long): PlacemarkModel? = placemarks.find { it.id == id }
}