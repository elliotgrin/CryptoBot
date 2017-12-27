package com.apps.elliotgrin.cryptobot

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.apps.elliotgrin.cryptobot.application.App
import com.apps.elliotgrin.cryptobot.databinding.ActivityMainBinding
import com.apps.elliotgrin.cryptobot.models.CryptoList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        App.getApi().getData( 50).enqueue(object : Callback<List<CryptoList>> {
            override fun onResponse(call: Call<List<CryptoList>>, response: Response<List<CryptoList>>) {
                /*posts.addAll(response.body())
                recyclerView.getAdapter().notifyDataSetChanged()*/
                Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<CryptoList>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "An error occurred during networking", Toast.LENGTH_SHORT).show()
            }
        })

    }
}