package com.example.myquote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myquote.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRandomQuote()

        binding.btnAllQuotes.setOnClickListener {
            startActivity(Intent(this@MainActivity, ListQuotesActivity::class.java))
        }
    }

    private fun getRandomQuote() {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient() // untuk membuat koneksi ke server secara asynchronous
        val url = "https://quote-api.dicoding.dev/random"
        /*
        Kemudian karena kita hanya mengambil data (READ), maka Anda menggunakan kode client.get()
         */
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                // ProgressBar digunakan untuk memberitahu proses yang sedang berjalan, ketika data sudah tampil ProgressBar akan hilang
                binding.progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                /*
                Jika data yang di Logcat ada tapi list tidak tampil, maka kemungkinan kesalahan terjadi pada saat parsing JSON atau pada saat menampilkan RecyclerView
                 */
                Log.d(TAG, result) //  menampilkan response di Logcat
                try {
                    val responseObject = JSONObject(result) // JSONOBject karena dia bertipe JSONObject dimmulai dengan {} dan untuk mengambil data
                    /*
                     untuk mengambil data quote dan author, sesuai dengan tipe data dari value tersebut
                     Pastikan juga parameter yang digunakan sama persis dengan key yang ada di web API.
                     */
                    val quote = responseObject.getString("en")
                    val author = responseObject.getString("author")
                    binding.tvQuote.text = quote
                    binding.tvAuthor.text = author
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()

            }

        })
    }
}