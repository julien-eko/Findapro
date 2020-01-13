package com.julien.findapro.view

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julien.findapro.Utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*
import kotlinx.android.synthetic.main.fragment_chat_list_item.view.*
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(chat:HashMap<String,String>,clickListener: (HashMap<String,String>,isProfil:Boolean) -> Unit) {

        load(chat)

        itemView.fragment_chat_list_item_name_textview.text =chat["full name"].toString()



        Picasso.get().load(chat["photo"].toString()).transform(CircleTransform()).into(itemView.fragment_chat_list_item_photo_imageview)

        itemView.fragment_chat_list_item_clik_linearlayout.setOnClickListener{clickListener(chat,false)}
        itemView.fragment_chat_list_item_photo_imageview.setOnClickListener{clickListener(chat,true)}

    }

    private fun load(chat:HashMap<String,String>){
        val db = FirebaseFirestore.getInstance()
        var createdDate:Any? = Timestamp(Date())
        var lastMessage:String =""

        db.collection("assignments").document(chat["id"].toString()).collection("chat").orderBy("dateCreated",Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener {chatDocument ->

                for (message in chatDocument){
                    createdDate = message["dateCreated"]
                    lastMessage = message["message"].toString()
                }


                itemView.fragment_chat_list_item_last_message_textview.text = lastMessage

                var date:String?
                val dateCreatedTimestamp =createdDate as? Timestamp
                val dateCreatedDate = dateCreatedTimestamp?.toDate()
                val realDate:Date = Date()

                val dateFormatDay = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
                val dateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())

                date = if ( dateFormatDay.format(dateCreatedDate).toString() == dateFormatDay.format(realDate).toString()){
                    dateFormat.format(dateCreatedDate).toString()
                }else{
                    dateFormatDay.format(dateCreatedDate).toString()
                }


                //date =   dateFormat.format(dateCreatedDate).toString()

                itemView.fragment_chat_list_item_time_textview.text = date




            }
            .addOnFailureListener { exception ->
                Log.w("chatlist", "Error getting documents: ", exception)
            }
    }
}