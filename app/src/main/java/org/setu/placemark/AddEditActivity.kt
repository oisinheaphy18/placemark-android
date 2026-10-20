package org.setu.placemark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.setu.placemark.models.PlacemarkModel
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        xInput = findViewById(R.id.xInput)
        yInput = findViewById(R.id.yInput)

        editingId = intent.getLongExtra("id", -1L)
        Timber.d("AddEditActivity created, editingId=$editingId")

        if (editingId != -1L) {
            findViewById<TextView>(R.id.formTitle).text = "Edit Placemark"
            loadExistingMark(editingId)
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener { saveMark() }
        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            Timber.d("Add/edit cancelled")
            finish()
        }
    }

    private fun loadExistingMark(id: Long) {
        val mark = AppData.placemarks.findOne(id)

        if (mark == null) {
            Timber.w("Placemark id=$id not found")
            Toast.makeText(this, "Placemark not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.description)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())
    }

    private fun saveMark() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val x = xInput.text.toString().toDoubleOrNull()
        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y = yInput.text.toString().toDoubleOrNull()
        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        if (editingId == -1L) {
            AppData.placemarks.create(
                PlacemarkModel(title = title, description = description, x = x, y = y)
            )
            Toast.makeText(this, "Placemark created", Toast.LENGTH_SHORT).show()
        } else {
            AppData.placemarks.update(
                PlacemarkModel(id = editingId, title = title, description = description, x = x, y = y)
            )
            Toast.makeText(this, "Placemark updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}