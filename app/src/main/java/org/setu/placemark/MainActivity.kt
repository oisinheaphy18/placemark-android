package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.viewMarksButton).setOnClickListener {
            startActivity(Intent(this, MarkListActivity::class.java))
        }

        findViewById<Button>(R.id.addMarkButton).setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }
}