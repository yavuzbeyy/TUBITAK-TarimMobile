import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tbtktarimapp.R
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

data class Plant(
    val name: String,
    val photoKey: String?,
    val description: String,
    val iklimAdi: String,
    val iklimAciklama: String,
    val toprakAdi: String,
    val toprakAciklama: String,
    val sulamaAdi: String,
    val sulamaAciklama: String,
    val gubrelemeAdi: String,
    val gubrelemeAciklama: String
) : Serializable

class PlantAdapter(private val plantList: List<Plant>, private val onItemClick: (Plant) -> Unit) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val plantName: TextView = itemView.findViewById(R.id.plant_name)
        val plantImage: ImageView = itemView.findViewById(R.id.plant_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.plantName.text = plant.name

        // Resmi yükle (API çağrısı)
        thread {
            try {
                val imageUrl = URL("https://10.0.2.2:7107/api/Upload/GetImageByFotokey?filekey=${plant.photoKey}")
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                (holder.itemView.context as AppCompatActivity).runOnUiThread {
                    holder.plantImage.setImageBitmap(bitmap)
                }

                connection.disconnect()
            } catch (e: Exception) {
                // Hata durumunu yönet
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(plant)
        }
    }

    override fun getItemCount(): Int = plantList.size
}
