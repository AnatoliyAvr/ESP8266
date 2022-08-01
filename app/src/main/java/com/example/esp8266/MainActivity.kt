package com.example.esp8266

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.esp8266.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var request: Request
    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPreferences
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("MyPref", MODE_PRIVATE)
        onClickSaveIp()
        getIp()
        binding.apply {
            bLed1.setOnClickListener(onClickListener())
            bLed2.setOnClickListener(onClickListener())
            bLed3.setOnClickListener(onClickListener())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sync) post("temperature")
        return true
    }

    private fun onClickListener(): View.OnClickListener {
        return View.OnClickListener {
            when (it.id) {
                R.id.bLed1 -> {
                    post("led1")
                }
                R.id.bLed2 -> {
                    post("led2")
                }
                R.id.bLed3 -> {
                    post("led3")
                }
            }
        }
    }

    private fun getIp() = with(binding) {
        val ip = pref.getString("ip", "")
        if (ip != null) {
            if (ip.isNotEmpty()) edIp.setText(ip)
        }
    }

    private fun onClickSaveIp() = with(binding) {
        bSave.setOnClickListener {
            if (edIp.text.isNotEmpty()) saveIp(edIp.text.toString())
        }
    }

    private fun saveIp(ip: String) {
        val editor = pref.edit()
        editor.putString("ip", ip)
        editor.apply()
    }

    private fun post(post: String) {
        Thread {
            request = Request.Builder().url("http://${binding.edIp.text}/$post").build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val resultText = response.body()?.string()
                    runOnUiThread {
                        val temp = resultText + "Cº"
                        binding.tvTemp.text = temp
                    }
                }
            } catch (_: IOException) {
            }
        }.start()
    }
}