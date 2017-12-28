package com.apps.elliotgrin.cryptobot.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.apps.elliotgrin.cryptobot.BR
import com.apps.elliotgrin.cryptobot.databinding.BotMessageBinding
import com.apps.elliotgrin.cryptobot.databinding.MyMessageBinding
import com.apps.elliotgrin.cryptobot.models.Message


/**
 * Created by elliotgrin on 28.12.2017.
 */
class MessagesAdapter(val messages: List<Message>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val MY_MESSAGE_VIEW_TYPE = 0
        val BOT_MESSAGE_VIEW_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MY_MESSAGE_VIEW_TYPE) {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val binding: MyMessageBinding = MyMessageBinding.inflate(inflater, parent, false)
            return MyViewHolder(binding)
        } else {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val binding: BotMessageBinding = BotMessageBinding.inflate(inflater, parent, false)
            return BotViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val message = messages[position]

        if (holder is MyViewHolder) {
            holder.bind(message)
        } else if (holder is BotViewHolder){
            holder.bind(message)

        }
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].type
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MyViewHolder(val binding: MyMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.message = message
            binding.executePendingBindings()
        }
    }

    class BotViewHolder(val binding: BotMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.message = message
            binding.executePendingBindings()
        }
    }
}