package com.julien.findapro.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R
import com.julien.findapro.Utils.Notification

class NotificationListAdapter(var notificationList: ArrayList<Notification>,val clickListener:(Notification,isProfil:Boolean)->Unit,val userType:String):
    RecyclerView.Adapter<NotificationListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val v = LayoutInflater.from(parent?.context)
            .inflate(R.layout.activity_notification_list_item, parent, false)
        return NotificationListViewHolder(v)

    }


    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        holder.update(notificationList.get(position),clickListener,userType)

    }




    override fun getItemCount() = notificationList.size
}