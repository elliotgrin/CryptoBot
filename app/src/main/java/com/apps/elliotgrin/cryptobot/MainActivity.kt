package com.apps.elliotgrin.cryptobot

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.apps.elliotgrin.cryptobot.adapters.MessagesAdapter
import com.apps.elliotgrin.cryptobot.application.App
import com.apps.elliotgrin.cryptobot.databinding.ActivityMainBinding
import com.apps.elliotgrin.cryptobot.models.Currency
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.apps.elliotgrin.cryptobot.models.Message

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        makeCurrenciesRequest()
        setOnTextChangeListener()
    }

    private fun makeCurrenciesRequest() {
        App.getApi().getData( 50).enqueue(object : Callback<List<Currency>> {
            override fun onResponse(call: Call<List<Currency>>, response: Response<List<Currency>>) {
                val currencies = response.body()
                var message = "Hi!\nThis is all available currencies:\n\n"

                for (cur in currencies) {
                    message += cur.symbol + "\n"
                }

                message += "\nChoose one!"

                val example = "For example: BTC-USD,EUR,RUB"

                val messages: List<Message> = listOf<Message>(
                        Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE, message),
                        Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE, example)
                )

                val adapter = MessagesAdapter(messages, this@MainActivity)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                binding.recyclerView.adapter = adapter

            }

            override fun onFailure(call: Call<List<Currency>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "An error occurred during networking", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setOnTextChangeListener() {
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    binding.send.setImageResource(R.drawable.ic_send_active)
                } else {
                    binding.send.setImageResource(R.drawable.ic_send_inactive)
                }
            }
        })
    }

}