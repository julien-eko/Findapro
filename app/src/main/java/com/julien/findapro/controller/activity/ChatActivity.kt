package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.julien.findapro.R
import com.julien.findapro.Utils.Message
import com.julien.findapro.api.MessageHelper
import com.julien.findapro.view.ChatAdapter
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        configureRecyclerView()

        activity_chat_send_button.setOnClickListener {
            if(!TextUtils.isEmpty(activity_chat_message_edit_text.text) && FirebaseAuth.getInstance().currentUser != null){
                MessageHelper.createMessageForChat(activity_chat_message_edit_text.text.toString(),FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),FirebaseAuth.getInstance().currentUser?.uid!!,intent.getStringExtra("assignment"))
                    .addOnFailureListener{exeption ->
                        Log.e("add message in chat","get fail with",exeption)
                    }
                activity_chat_message_edit_text.setText("")
            }
        }
    }


    fun configureRecyclerView(){

        val chatAdapter = ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessage(intent.getStringExtra("assignment"))),FirebaseAuth.getInstance().currentUser?.uid!!)


        recycler_view_chat_activity.layoutManager = LinearLayoutManager(this)
        recycler_view_chat_activity.adapter = chatAdapter

    }

    fun generateOptionsForAdapter(query: Query):FirestoreRecyclerOptions<Message>{
        return FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query,Message::class.java)
            .setLifecycleOwner(this)
            .build()
    }
}
