package com.example.tbtktarimapp

import Plant
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
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

        val plantIklimAdi: TextView = findViewById(R.id.plant_detail_iklimAdi)
        val plantIklimAciklama: TextView = findViewById(R.id.plant_detail_iklimAciklama)
        val plantToprakAdi: TextView = findViewById(R.id.plant_detail_toprakAdi)
        val plantToprakAciklama: TextView = findViewById(R.id.plant_detail_toprakAciklama)
        val plantSulamaAdi: TextView = findViewById(R.id.plant_detail_sulamaAdi)
        val plantSulamaAciklama: TextView = findViewById(R.id.plant_detail_sulamaAciklama)
        val plantGubrelemeAdi: TextView = findViewById(R.id.plant_detail_gubrelemeAdi)
        val plantGubrelemeAciklama: TextView = findViewById(R.id.plant_detail_gubrelemeAciklama)

        plantNameTextView.text = plant.name
        plantDescriptionTextView.text = plant.description
        plantIklimAdi.text = "İklim Adı: ${plant.iklimAdi}"
        plantIklimAciklama.text = "İklim Açıklama: ${plant.iklimAciklama}"
        plantToprakAdi.text = "Toprak Adı: ${plant.toprakAdi}"
        plantToprakAciklama.text = "Toprak Açıklama: ${plant.toprakAciklama}"
        plantSulamaAdi.text = "Sulama Adı: ${plant.sulamaAdi}"
        plantSulamaAciklama.text = "Sulama Açıklama: ${plant.sulamaAciklama}"
        plantGubrelemeAdi.text = "Gübreleme Adı: ${plant.gubrelemeAdi}"
        plantGubrelemeAciklama.text = "Gübreleme Açıklama: ${plant.gubrelemeAciklama}"

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

        // Geri dönüş butonu işlevi
        val backButton: Button = findViewById(R.id.button_back)
        backButton.setOnClickListener {
            finish()  // Bu, mevcut aktiviteyi kapatır ve bir önceki aktiviteye döner
        }
    }
}
