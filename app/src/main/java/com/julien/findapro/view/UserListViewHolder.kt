package com.julien.findapro.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.julien.findapro.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*

class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {




    fun update(user:HashMap<String,String>,clickListener: (HashMap<String,String>) -> Unit) {


        itemView.fragment_users_list_item_job_textview.text = user["full name"]
        itemView.fragment_users_list_item_name_textview.text = user["job"]

        Picasso.get().load(user["photo"]).into(itemView.fragment_users_list_item_photo_imageview)

        itemView.setOnClickListener{clickListener(user)}

    }
}