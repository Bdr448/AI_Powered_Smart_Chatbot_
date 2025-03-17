package com.example.aipoweredsmartchatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val chatList: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (viewType == VIEW_TYPE_USER) R.layout.chat_item_user else R.layout.chat_item_bot,
            parent, false
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatList[position]
        holder.messageText.text = message.text
    }

    override fun getItemCount(): Int = chatList.size

    fun addMessage(message: ChatMessage, recyclerView: RecyclerView) {
        chatList.add(message)
        notifyItemInserted(chatList.size - 1)
        recyclerView.scrollToPosition(chatList.size - 1) // Ensure latest message is visible
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
    }
}
