package com.apps.elliotgrin.cryptobot

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apps.elliotgrin.cryptobot.adapters.MessagesAdapter
import com.apps.elliotgrin.cryptobot.aiml.Aiml
import com.apps.elliotgrin.cryptobot.application.App
import com.apps.elliotgrin.cryptobot.databinding.ActivityMainBinding
import com.apps.elliotgrin.cryptobot.models.Currency
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.apps.elliotgrin.cryptobot.models.Message
import kotlinx.android.synthetic.main.activity_main.*
import org.alicebot.ab.Bot
import org.alicebot.ab.Chat
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: MessagesAdapter

    lateinit var cryptoList: List<Currency>
    lateinit var messages: ArrayList<Message>

    lateinit var requestQueue: RequestQueue

    lateinit var bot: Bot

    companion object {
        lateinit var chat: Chat
    }

    lateinit var aiml: Aiml

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        makeCurrenciesRequest()
        setOnTextChangeListener()
        setSendClickListener()

        aiml = Aiml(this)
        aiml.setupAiml()
    }

    private fun makeCurrenciesRequest() {
        App.getApi().getData( 50).enqueue(object : Callback<List<Currency>> {
            override fun onResponse(call: Call<List<Currency>>, response: Response<List<Currency>>) {
                cryptoList = response.body()
                var message = "Hi!\nThis is all available crypto-currencies:\n\n"

                for (cur in cryptoList) {
                    message += cur.symbol + " (" + cur.name + ")\n"
                }

                message += "\nChoose one!"

                val example = "For example: BTC-USD\nTo get valid currencies list type \"Currencies\""

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
                    binding.send.setImageResource(R.drawable.ic_send_clicked)
                }
            }
        })
    }

    /*private fun setSendClickListener() {
        binding.send.setOnClickListener({
            _ ->
            if (binding.editText.text.isNotEmpty()) { sendMessage() }
        })
    }*/

    private fun setSendClickListener() {
        binding.send.setOnClickListener({
            _ ->
            if (binding.editText.text.isNotEmpty()) {
                val message = binding.editText.text.toString()
                showMyMessage(message)

                val response = aiml.chat.multisentenceRespond(message)
                showBotMessage(response)

                binding.editText.text.clear()
            }
        })
    }

    private fun sendMessage() {
        val command = binding.editText.text.toString()
        binding.editText.text.clear()
        showMyMessage(command)

        if (command.toLowerCase() == "currencies") {
            showCurrenciesList()
            return
        }

        val splitCommand = command.split("-")

        if (splitCommand.size == 1) {
            showBotMessage("Invalid data")
            showBotMessage("Try again")
            return
        }

        val cryptoCur = splitCommand[0].trim().toUpperCase()
        val currency = splitCommand[1].trim().toUpperCase()

        var id = "-1"
        cryptoList.map { c -> if (c.symbol == cryptoCur) id = c.id }

        if (id == "-1") {
            showBotMessage("Invalid crypto currency")
            showBotMessage("Try another")
        } else {
            makeRequestForCryptoPrice(id, currency)
        }
    }

    private fun showBotMessage(message: String) {
        messages.add(Message(MessagesAdapter.BOT_MESSAGE_VIEW_TYPE, message))

        adapter.notifyItemInserted(adapter.itemCount - 1)
        recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun showMyMessage(message: String) {
        messages.add(Message(MessagesAdapter.MY_MESSAGE_VIEW_TYPE, message))

        adapter.notifyItemInserted(adapter.itemCount - 1)
        recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun makeRequestForCryptoPrice(cryptoId: String, currency: String) {
        val url = "https://api.coinmarketcap.com/v1/ticker/"
        val builder = Uri.parse(url).buildUpon()

        builder.appendPath(cryptoId)
        builder.appendQueryParameter("convert", currency)

        val stringRequest = StringRequest(
                Request.Method.GET,
                builder.build().toString(),
                com.android.volley.Response.Listener { response: String ->
                    parseResponse(response, cryptoId, currency)
                },
                com.android.volley.Response.ErrorListener { _: VolleyError? ->
                    showBotMessage("Error occurred!")
                }
        )

        requestQueue.add(stringRequest)
    }

    private fun parseResponse(response: String, crypto: String, currency: String) {
        try {

            val jsonResponse = JSONArray(response)
            val price = jsonResponse.getJSONObject(0).getInt(
                    "price_" + currency.toLowerCase()
            )

            val message = "1 $crypto = $price $currency"
            showBotMessage(message)

        } catch (e: JSONException) {

            showBotMessage("Invalid currency")
            showBotMessage("Try another")

        }
    }

    private fun showCurrenciesList() {
        val currencies = resources.getStringArray(R.array.valid_currencies)
        var message = "Valid currencies:\n\n"

        currencies.map { c -> message += c + "\n" }

        showBotMessage(message)
    }

}