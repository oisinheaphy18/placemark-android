package org.setu.placemark.models

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import timber.log.Timber

class PlacemarkMemStore(private val file: File) {

    private val placemarks = ArrayList<PlacemarkModel>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<PlacemarkModel>>() {}.type

        val loaded: ArrayList<PlacemarkModel> =
            gson.fromJson(file.readText(), type) ?: arrayListOf()

        placemarks.clear()
        placemarks.addAll(loaded)

        lastId.set(placemarks.maxOfOrNull { it.id } ?: 0L)
    }

    private fun save() {
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(placemarks))
    }

    fun findAll(): List<PlacemarkModel> {
        Timber.d("findAll: ${placemarks.size} placemarks")
        return placemarks
    }

    fun create(placemark: PlacemarkModel) {
        placemark.validate()
        placemark.id = lastId.incrementAndGet()
        placemarks.add(placemark)
        save()
        Timber.d("create: id=${placemark.id}, title=${placemark.title}")
    }

    fun update(placemark: PlacemarkModel): Boolean {
        placemark.validate()

        val found = findOne(placemark.id)
        if (found == null) {
            Timber.w("update: id=${placemark.id} not found")
            return false
        }

        found.title = placemark.title
        found.description = placemark.description
        found.x = placemark.x
        found.y = placemark.y
        found.imageUri = placemark.imageUri

        save()

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

        val removed = placemarks.remove(found)

        if (removed) {
            save()
        }

        return removed
    }

    fun findOne(id: Long): PlacemarkModel? =
        placemarks.find { it.id == id }
}