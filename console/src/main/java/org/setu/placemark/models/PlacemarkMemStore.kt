package org.setu.placemark.models

import java.util.concurrent.atomic.AtomicLong

class PlacemarkMemStore : PlacemarkStore {
    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)

    override fun findAll(): List<PlacemarkModel> = placemarks

    override fun create(placemark: PlacemarkModel) {
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
    }

    override fun update(placemark: PlacemarkModel): Boolean {
        val found = findOne(placemark.id) ?: return false
        found.title = placemark.title
        found.description = placemark.description
        return true
    }

    override fun delete(id: Long): Boolean {
        val found = findOne(id) ?: return false
        return placemarks.remove(found)
    }

    override fun findOne(id: Long): PlacemarkModel? =
        placemarks.find { it.id == id }
}