package com.julien.findapro.api

import android.content.res.Resources
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julien.findapro.R
import com.julien.findapro.model.Message

class MessageHelper {

    companion object {
        fun getAllMessage(assignmentsId: String): Query {
            val db = FirebaseFirestore.getInstance()

            return db.collection(Resources.getSystem().getString(R.string.assignments)).document(assignmentsId).collection(Resources.getSystem().getString(R.string.chat))
                .orderBy(Resources.getSystem().getString(R.string.dateCreated))
        }

        fun createMessageForChat(
            textMessage: String,
            urlImageSender: String,
            userSender: String,
            assignmentsId: String
        ): Task<DocumentReference> {
            val db = FirebaseFirestore.getInstance()
            val message = Message(
                textMessage,
                null,
                userSender,
                urlImageSender,
                null
            )

            return db.collection(Resources.getSystem().getString(R.string.assignments)).document(assignmentsId).collection(Resources.getSystem().getString(R.string.chat))
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
            val message = Message(
                textMessage,
                null,
                userSender,
                urlImageSender,
                urlImageMessage
            )

            return db.collection(Resources.getSystem().getString(R.string.assignments)).document(assignmentsId).collection(Resources.getSystem().getString(R.string.chat))
                .add(message)
        }
    }


}