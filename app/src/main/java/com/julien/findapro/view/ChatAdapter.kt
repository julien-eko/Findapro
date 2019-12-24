package com.julien.findapro.view

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.julien.findapro.R
import com.julien.findapro.Utils.Message

class ChatAdapter(options: FirestoreRecyclerOptions<Message>,val currentUserId:String):FirestoreRecyclerAdapter<Message,MessageViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_chat_item,parent,false)
        return MessageViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        holder.updateWithMessage(model,currentUserId)
    }


}