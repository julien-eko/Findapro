package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.julien.findapro.Utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_planning_item.view.*
import kotlinx.android.synthetic.main.activity_profil_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PlanningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(planning:HashMap<String,Any?>,clickListener: (HashMap<String,Any?>,button:String) -> Unit) {


        if (planning["isPro"] as Boolean){
            itemView.activity_planning_item_map_button.visibility = View.VISIBLE
        }

        itemView.activity_planning_name_textview.text = planning["full name"].toString()

        Picasso.get().load(planning["photo"].toString()).transform(CircleTransform()).into(itemView.activity_planning_clik_imageview)


        var date:String?
        val dateCreatedTimestamp =planning["date"] as? Timestamp
        val dateCreatedDate:Date? = dateCreatedTimestamp?.toDate()
        //val realDate = Date()

        val dateFormatDay = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        //val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


        date = dateFormatDay.format(dateCreatedDate!!).toString()


        itemView.activity_planning_date_textview.text = date

        itemView.activity_planning_item_map_button.setOnClickListener{clickListener(planning,"map")}
        itemView.activity_planning_clik_imageview.setOnClickListener{clickListener(planning,"profil")}
        itemView.activity_planning_clik_linearlayout.setOnClickListener{clickListener(planning,"detail")}
        itemView.activity_planning_item_message_button.setOnClickListener{clickListener(planning,"message")}

    }
}