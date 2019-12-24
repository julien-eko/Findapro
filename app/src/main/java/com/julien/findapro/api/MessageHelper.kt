package com.julien.findapro.api

import android.app.DownloadManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julien.findapro.Utils.Message

public class MessageHelper {

    companion object{
        public fun getAllMessage(assignmentsId: String): Query{
            val db = FirebaseFirestore.getInstance()

            return db.collection("assignments").document(assignmentsId).collection("chat").orderBy("dateCreated")
        }

        fun createMessageForChat(textMessage:String,urlImageSender:String,userSender:String,assignmentsId: String):Task<DocumentReference>{
            val db = FirebaseFirestore.getInstance()
            val message = Message(textMessage,urlImageSender,userSender)

            return db.collection("assignments").document(assignmentsId).collection("chat").add(message)
        }
    }

}