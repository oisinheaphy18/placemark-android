package org.setu.placemark

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUserInterface()
    }

    override fun onResume() {
        super.onResume()
        if (::listLayout.isInitialized) {
            displayMarks()
        }
    }

    private fun createUserInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 320, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Placemarks"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Placemark"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, AddEditActivity::class.java))
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        root.addView(title, params)
        root.addView(addButton, params)
        root.addView(listLayout, params)

        setContentView(root)
        displayMarks()
    }

    private fun displayMarks() {
        listLayout.removeAllViews()

        val marks = AppData.placemarks.findAll()

        if (marks.isEmpty()) {
            listLayout.addView(TextView(this).apply {
                text = "No placemarks yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            })
            return
        }

        for (mark in marks) {
            val markLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val markTitle = TextView(this).apply {
                text = "${mark.id}: ${mark.title}"
                textSize = 20f
            }

            val markDescription = TextView(this).apply {
                text = mark.description
                textSize = 16f
            }

            val coordinates = TextView(this).apply {
                text = "X: ${mark.x}, Y: ${mark.y}"
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"
                setOnClickListener {
                    val intent = Intent(this@MainActivity, AddEditActivity::class.java)
                    intent.putExtra("id", mark.id)
                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    AppData.placemarks.delete(mark.id)
                    displayMarks()
                }
            }

            markLayout.addView(markTitle)
            markLayout.addView(markDescription)
            markLayout.addView(coordinates)
            markLayout.addView(editButton)
            markLayout.addView(deleteButton)

            listLayout.addView(markLayout)
        }
    }
}