package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat_list_item.view.*
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*

class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(chat:HashMap<String,String>,clickListener: (HashMap<String,String>) -> Unit) {


        itemView.fragment_chat_list_item_name_textview.text =chat["full name"]
        itemView.fragment_chat_list_item_textview.text =chat["id"]

        Picasso.get().load(chat["photo"]).into(itemView.fragment_chat_list_item_photo_imageview)

        itemView.setOnClickListener{clickListener(chat)}

    }
}