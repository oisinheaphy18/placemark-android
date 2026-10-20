package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("MainActivity created")

        findViewById<Button>(R.id.viewMarksButton).setOnClickListener {
            Timber.d("View placemarks clicked")
            startActivity(Intent(this, MarkListActivity::class.java))
        }

        findViewById<Button>(R.id.addMarkButton).setOnClickListener {
            Timber.d("Add placemark clicked")
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }
}