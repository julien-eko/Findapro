package com.julien.findapro.model

import android.content.res.Resources
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.julien.findapro.R
import java.util.*

class Notification(
    @get:ServerTimestamp
    var dateCreated: Date? = null,
    var otherUserId: String? = null,
    var assignmentId: String? = null,
    var titleNotification: String? = null,
    var textNotification: String? = null,
    var cause: String? = null,
    var token: String? = null
) {


    companion object {

        //create notification in db
        fun createNotificationInDb(
            userType: String,
            idUserRecieve: String,
            otherUserId: String?,
            assignmentId: String,
            titleNotification: String?,
            textNotification: String?,
            cause: String?
        ) {
            val db = FirebaseFirestore.getInstance()

            db.collection(userType).document(idUserRecieve).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val notification =
                            Notification(
                                null,
                                otherUserId,
                                assignmentId,
                                titleNotification,
                                textNotification,
                                cause,
                                document[Resources.getSystem().getString(R.string.token)].toString()
                            )

                        db.collection(userType).document(idUserRecieve).collection(Resources.getSystem().getString(R.string.notificationC))
                            .document().set(notification)
                    } else {
                        Log.d("Notification", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Notification", "get failed with ", exception)
                }


        }
    }

}