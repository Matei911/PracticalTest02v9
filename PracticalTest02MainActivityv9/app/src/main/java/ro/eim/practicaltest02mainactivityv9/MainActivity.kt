package ro.eim.practicaltest02mainactivityv9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wordInput = findViewById<EditText>(R.id.wordInput)
        val minLengthInput = findViewById<EditText>(R.id.minLengthInput)
        val fetchButton = findViewById<Button>(R.id.fetchButton)
        val resultView = findViewById<TextView>(R.id.resultView)
        val mapButton = findViewById<Button>(R.id.mapButton)

        fetchButton.setOnClickListener {
            val word = wordInput.text.toString().trim()
            val minLength = minLengthInput.text.toString().toIntOrNull() ?: 0

            if (word.isNotEmpty()) {
                fetchAnagrams(word, minLength) { result ->
                    runOnUiThread {
                        resultView.text = result
                    }
                }
            } else {
                resultView.text = "Introdu un cuvânt valid."
            }
        }

        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAnagrams(word: String, minLength: Int, callback: (String) -> Unit) {
        val url = "http://www.anagramica.com/all/$word"
        Log.d("AnagramApp", "Cerere către URL: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AnagramApp", "Conexiunea a eșuat", e)
                callback("Eroare: Nu s-a putut conecta la server.")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.d("AnagramApp", "Răspuns brut de la server: $responseBody")
                        try {
                            val json = JSONObject(responseBody)
                            val anagrams = json.getJSONArray("all")
                            val filteredAnagrams = mutableListOf<String>()

                            for (i in 0 until anagrams.length()) {
                                val anagram = anagrams.getString(i)
                                if (anagram.length >= minLength) {
                                    filteredAnagrams.add(anagram)
                                }
                            }

                            // Log parsed and filtered anagrams
                            Log.d("AnagramApp", "Anagrame filtrate: ${filteredAnagrams.joinToString(", ")}")

                            callback(filteredAnagrams.joinToString(", "))
                        } catch (e: Exception) {
                            Log.e("AnagramApp", "Eroare la parsarea JSON", e)
                            callback("Eroare: Format de răspuns invalid.")
                        }
                    } ?: callback("Eroare: Răspuns gol de la server.")
                } else {
                    Log.e("AnagramApp", "Cod eroare: ${response.code}")
                    callback("Eroare: Cerere nereușită. Cod: ${response.code}")
                }
            }
        })
    }
}