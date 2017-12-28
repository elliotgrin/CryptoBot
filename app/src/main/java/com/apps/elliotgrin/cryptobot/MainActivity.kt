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
    lateinit var adapter: MessagesAdapter

    lateinit var cryptoList: List<Currency>
    lateinit var messages: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        makeCurrenciesRequest()
        setOnTextChangeListener()
        setSendClickListener()
    }

    private fun makeCurrenciesRequest() {
        App.getApi().getData( 50).enqueue(object : Callback<List<Currency>> {
            override fun onResponse(call: Call<List<Currency>>, response: Response<List<Currency>>) {
                cryptoList = response.body()
                var message = "Hi!\nThis is all available currencies:\n\n"

                for (cur in cryptoList) {
                    message += cur.symbol + " (" + cur.name + ")\n"
                }

                message += "\nChoose one!"

                val example = "For example: BTC-USD"

                messages = arrayListOf(
                        Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE, message),
                        Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE, example)
                )

                adapter = MessagesAdapter(messages, this@MainActivity)
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

    private fun setSendClickListener() {
        binding.send.setOnClickListener({
            _ -> if (binding.editText.text.isNotEmpty()) { sendMessage() }
        })
    }

    private fun sendMessage() {
        val command = binding.editText.text
        val cryptoCur = command.split("-")[0].trim()
        val currencies = command.split("-")[1].trim()

        var id = "-1"
        cryptoList.map { c -> if (c.id == cryptoCur) id = c.id }

        if (id == "-1") {
            messages.addAll(arrayListOf(
                    Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE, "Invalid crypto currency"),
                    Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE,"Try again")
            ))

            adapter.notifyItemRangeInserted(adapter.itemCount - 2, adapter.itemCount)
        } else {

        }
    }

}