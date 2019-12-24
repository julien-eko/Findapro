package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_assignments_list_item.view.*
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*

class AssignmentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



    fun update(assignment:HashMap<String,String>,clickListener: (HashMap<String,String>) -> Unit) {


        itemView.fragment_assignments_list_item_name_textview.text = assignment["full name"]
        itemView.fragment_assignments_list_item_status_textview.text = assignment["status"]

        Picasso.get().load(assignment["photo"]).into(itemView.fragment_assignments_list_item_photo_imageview)

        itemView.setOnClickListener{clickListener(assignment)}

    }
}