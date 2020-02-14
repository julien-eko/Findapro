package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.julien.findapro.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(chat:HashMap<String,Any?>,clickListener: (HashMap<String,Any?>,isProfil:Boolean) -> Unit) {



        itemView.fragment_chat_list_item_name_textview.text =chat["full name"].toString()

        itemView.fragment_chat_list_item_last_message_textview.text = chat["lastMessage"].toString()


        val date:String?
        val dateCreatedTimestamp =chat["createdDate"] as? Timestamp
        val dateCreatedDate = dateCreatedTimestamp?.toDate()
        val realDate = Date()

        val dateFormatDay = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        val dateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())

        date = if ( dateFormatDay.format(dateCreatedDate!!).toString() == dateFormatDay.format(realDate).toString()){
            dateFormat.format(dateCreatedDate).toString()
        }else{
            dateFormatDay.format(dateCreatedDate).toString()
        }




        itemView.fragment_chat_list_item_time_textview.text = date

        Picasso.get().load(chat["photo"].toString()).transform(CircleTransform()).into(itemView.fragment_chat_list_item_photo_imageview)

        itemView.fragment_chat_list_item_clik_linearlayout.setOnClickListener{clickListener(chat,false)}
        itemView.fragment_chat_list_item_photo_imageview.setOnClickListener{clickListener(chat,true)}

    }


}