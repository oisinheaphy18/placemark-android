package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.setu.placemark.models.PlacemarkModel
import timber.log.Timber

class MarkListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlacemarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_list)
        Timber.d("MarkListActivity created")

        recyclerView = findViewById(R.id.marksRecyclerView)

        adapter = PlacemarkAdapter(
            marks = AppData.placemarks.findAll().toList(),
            listener = object : OnMarkListener {
                override fun onMarkClick(mark: PlacemarkModel) {
                    editMark(mark)
                }

                override fun onMarkLongClick(mark: PlacemarkModel): Boolean {
                    confirmDelete(mark)
                    return true
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.returnButton).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.updateMarks(AppData.placemarks.findAll())
        }
    }

    private fun editMark(mark: PlacemarkModel) {
        Timber.d("Editing mark id=${mark.id}, title=${mark.title}")
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", mark.id)
        startActivity(intent)
    }

    private fun confirmDelete(mark: PlacemarkModel) {
        Timber.d("Asking to delete mark id=${mark.id}")
        AlertDialog.Builder(this)
            .setTitle("Delete this placemark?")
            .setMessage("${mark.title} will be permanently removed.")
            .setPositiveButton("Delete") { _, _ -> deleteMark(mark) }
            .setNegativeButton("Cancel") { dialog, _ ->
                Timber.d("Delete cancelled for id=${mark.id}")
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteMark(mark: PlacemarkModel) {
        Timber.d("Deleting mark id=${mark.id}, title=${mark.title}")
        AppData.placemarks.delete(mark.id)
        adapter.updateMarks(AppData.placemarks.findAll())
    }
}