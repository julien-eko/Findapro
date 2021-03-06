package com.julien.findapro.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R

class ChatListAdapter(
    private var chatList: List<HashMap<String, Any?>>,
    val context: Context,
    val clickListener: (HashMap<String, Any?>, isProfil: Boolean) -> Unit
) :
    RecyclerView.Adapter<ChatListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_chat_list_item, parent, false)
        return ChatListViewHolder(v)

    }


    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.update(chatList[position], clickListener)

    }


    override fun getItemCount() = chatList.size
}