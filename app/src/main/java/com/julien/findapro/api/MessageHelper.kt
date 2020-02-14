package com.julien.findapro.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julien.findapro.utils.Message

class MessageHelper {

    companion object {
        fun getAllMessage(assignmentsId: String): Query {
            val db = FirebaseFirestore.getInstance()

            return db.collection("assignments").document(assignmentsId).collection("chat")
                .orderBy("dateCreated")
        }

        fun createMessageForChat(
            textMessage: String,
            urlImageSender: String,
            userSender: String,
            assignmentsId: String
        ): Task<DocumentReference> {
            val db = FirebaseFirestore.getInstance()
            val message = Message(textMessage, null, userSender, urlImageSender, null)

            return db.collection("assignments").document(assignmentsId).collection("chat")
                .add(message)
        }

        fun createMessageWhithImageForChat(
            textMessage: String,
            urlImageSender: String,
            userSender: String,
            assignmentsId: String,
            urlImageMessage: String
        ): Task<DocumentReference> {
            val db = FirebaseFirestore.getInstance()
            val message = Message(textMessage, null, userSender, urlImageSender, urlImageMessage)

            return db.collection("assignments").document(assignmentsId).collection("chat")
                .add(message)
        }
    }


}