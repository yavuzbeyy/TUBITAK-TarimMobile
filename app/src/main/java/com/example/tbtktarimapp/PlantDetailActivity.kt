package com.example.tbtktarimapp

import Plant
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PlantDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_detail)

        val plant = intent.getSerializableExtra("plant") as Plant

        val plantNameTextView: TextView = findViewById(R.id.plant_detail_name)
        val plantImageView: ImageView = findViewById(R.id.plant_detail_image)
        val plantDescriptionTextView: TextView = findViewById(R.id.plant_detail_description)

        plantNameTextView.text = plant.name
        plantDescriptionTextView.text = plant.description

        // Resmi yükle
        thread {
            try {
                val imageUrl = URL("https://10.0.2.2:7107/api/Upload/GetImageByFotokey?filekey=${plant.photoKey}")
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                runOnUiThread {
                    plantImageView.setImageBitmap(bitmap)
                }

                connection.disconnect()
            } catch (e: Exception) {
                // Hata durumunu yönet
            }
        }
    }
}
