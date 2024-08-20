package com.example.tbtktarimapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.concurrent.thread

class GetCoordinatActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_get_coordinat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SSL sertifikalarını geçici olarak kabul etmek için TrustManager'ı ayarlama
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        try {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationTextView = findViewById(R.id.textView)

        val getLocationButton: Button = findViewById(R.id.button_get_location)
        getLocationButton.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getLastKnownLocation()
        } else {
            locationTextView.text = "Konum izni verilmedi."
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // API İsteği Burada yapabilirim.
                makeApiRequest(location.latitude, location.longitude)
                locationTextView.text = "Enlem: ${location.latitude}, Boylam: ${location.longitude}"
            } else {
                // Konum bilgii alınamadıysa ben rastgele konum giriyorum
                makeApiRequest(30.2, 40.1)
              //  locationTextView.text = "Konum bilgisi alınamadı."
            }
        }
    }

    private fun makeApiRequest(latitude: Double, longitude: Double) {
        thread {
            try {
                val url = URL("https://10.0.2.2:7107/api/Bitki/GetCityAndPlantsByLocation?enlemKoordinat=$latitude&boylamKoordinat=$longitude")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val dataArray = jsonResponse.getJSONArray("data")

                    val plantInfo = StringBuilder()
                    for (i in 0 until dataArray.length()) {
                        val plant = dataArray.getJSONObject(i)
                        plantInfo.append("Ad: ${plant.getString("ad")}\n")
                        plantInfo.append("Açıklama: ${plant.getString("aciklama")}\n")
                        plantInfo.append("İklim: ${plant.getString("iklimAdi")}\n")
                        plantInfo.append("Toprak: ${plant.getString("toprakAdi")}\n")
                        plantInfo.append("Sulama: ${plant.getString("sulamaAdi")}\n")
                        plantInfo.append("Gübreleme: ${plant.getString("gubrelemeAdi")}\n\n")
                    }

                    runOnUiThread {
                        locationTextView.text = plantInfo.toString()
                    }
                } else {
                    runOnUiThread {
                        locationTextView.text = "API isteği başarısız: $responseCode"
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                runOnUiThread {
                    locationTextView.text = "Hata: ${e.message}"
                }
            }
        }
    }


}
