package org.setu.placemark

import android.os.Bundle
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.setu.placemark.models.PlacemarkModel
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private lateinit var imagePreview: ImageView
    private var selectedImageUri: Uri? = null

    private var editingId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        xInput = findViewById(R.id.xInput)
        yInput = findViewById(R.id.yInput)

        imagePreview = findViewById(R.id.imageViewAdd)

        val imagePicker =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                        // Some providers may not grant persistable access.
                    }

                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                }
            }

        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        editingId = intent.getLongExtra("id", -1L)
        Timber.d("AddEditActivity created, editingId=$editingId")

        if (editingId != -1L) {
            findViewById<TextView>(R.id.formTitle).text = "Edit Placemark"
            loadExistingMark(editingId)
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveMark()
        }

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

        selectedImageUri = mark.imageUri?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            imagePreview.setImageURI(selectedImageUri)
        } else {
            imagePreview.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }
    }

    private fun saveMark() {
        try {
            val mark = PlacemarkModel(
                id = if (editingId == -1L) 0L else editingId,
                title = titleInput.text.toString().trim(),
                description = descriptionInput.text.toString().trim(),
                x = xInput.text.toString().toDouble(),
                y = yInput.text.toString().toDouble(),
                imageUri = selectedImageUri?.toString()
            )

            mark.validate()

            if (editingId == -1L) {
                AppData.placemarks.create(mark)
                Toast.makeText(this, "Placemark created", Toast.LENGTH_SHORT).show()
            } else {
                if (!AppData.placemarks.update(mark)) {
                    Toast.makeText(this, "Placemark not found", Toast.LENGTH_SHORT).show()
                    return
                }

                Toast.makeText(this, "Placemark updated", Toast.LENGTH_SHORT).show()
            }

            finish()

        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                this,
                e.message ?: "Please check the entered values.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}