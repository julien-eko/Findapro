package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.julien.findapro.Utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_assignments_in_progress_item.view.*
import kotlinx.android.synthetic.main.fragment_assignments_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AssignmentsInProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(assignment:HashMap<String,Any?>,clickListener: (HashMap<String,Any?>) -> Unit) {


        itemView.fragment_assignments_in_progress_item_name_textview.text = assignment["full name"].toString()

        var status:String? =""
        if(assignment["status"].toString() == "accept"){
            status = "Accepter"
        }
        if(assignment["status"].toString() == "in progress"){
            status = "En cours"
        }
        if(assignment["status"].toString() == "finish"){
            status = "Fini"
        }
        if(assignment["status"].toString() == "pending"){
            status = "En attente"
        }
        if(assignment["status"].toString() == "refuse"){
            status = "Refusé"
        }
        itemView.fragment_assignments_in_progress_item_status_textview.text = status

        Picasso.get().load(assignment["photo"].toString()).transform(CircleTransform()).into(itemView.fragment_assignments_in_progress_item_photo_imageview)


        var date:String?
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        date = if (assignment["dateEnd"] == null){
            val dateCreatedTimestamp =assignment["dateCreated"] as Timestamp
            val dateCreatedDate = dateCreatedTimestamp.toDate()
            "Débuté depuis le : " + dateFormat.format(dateCreatedDate).toString()
        }else{
            val dateEndCreatedTimestamp = assignment["dateEnd"] as Timestamp
            val dateEndDate = dateEndCreatedTimestamp.toDate()
            "Fini depuis le : " +dateFormat.format(dateEndDate).toString()

        }
        itemView.fragment_assignment_in_progress_date.text = date

        itemView.setOnClickListener{clickListener(assignment)}

    }
}