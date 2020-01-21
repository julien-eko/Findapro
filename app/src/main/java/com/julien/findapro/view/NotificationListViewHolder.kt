package com.julien.findapro.view

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.Utils.CircleTransform
import com.julien.findapro.Utils.Notification
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_notification_list_item.view.*
import kotlinx.android.synthetic.main.fragment_assignments_list_item.view.*
import kotlinx.android.synthetic.main.fragment_chat_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(notification:Notification,clickListener:(Notification,isProfil:Boolean)->Unit,userType:String) {


        //itemView.activity_notification_list_date.text = "date"
        itemView.activity_notification_list_notufication_text_textview.text = notification.textNotification
        itemView.activity_notification_list_notufication_title_textview.text = notification.titleNotification


        loadImage(notification,userType)
        dateFormat(notification)

        itemView.activity_notification_list_clik_imageview.setOnClickListener { clickListener(notification,true) }
        itemView.activity_notification_list_clik_linearlayout.setOnClickListener { clickListener(notification,false) }
    }

    private fun loadImage(notification: Notification,userType: String){
        val user = if (userType == "users") "pro users" else "users"
        val db = FirebaseFirestore.getInstance()
        db.collection(user).document(notification.otherUserId!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document["photo"].toString()).transform(CircleTransform()).into(itemView.activity_notification_list_clik_imageview)

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    private fun dateFormat(notification: Notification){
        var date:String?
        val dateCreatedDate  =notification.dateCreated

        val realDate = Date()

        val dateFormatDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        date = if ( dateFormatDay.format(dateCreatedDate).toString() == dateFormatDay.format(realDate).toString()){
            dateFormat.format(dateCreatedDate).toString()
        }else{
            dateFormatDay.format(dateCreatedDate).toString()
        }


        itemView.activity_notification_list_notufication_time_textview.text = date
    }

    companion object{
        private const val TAG = "NotificationVH"
    }
}