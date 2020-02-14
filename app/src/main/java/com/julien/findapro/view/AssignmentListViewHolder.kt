package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_assignments_list_item.view.*

class AssignmentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



    fun update(assignment:HashMap<String,String>,clickListener: (HashMap<String,String>,isProfil:Boolean) -> Unit) {


        itemView.fragment_assignments_list_item_name_textview.text = assignment["full name"]
        itemView.fragment_assignments_list_item_city_textview.text = assignment["city"]

        Picasso.get().load(assignment["photo"]).transform(CircleTransform()).into(itemView.fragment_assignments_list_item_photo_imageview)

        if(assignment["rating"] != "null"){
            itemView.fragment_assignment_item_list_ratingbar.rating = assignment["rating"]!!.toFloat()
        }else{
            itemView.fragment_assignment_item_list_ratingbar.visibility = View.GONE
            itemView.fragment_assignment_item_list_no_rating.visibility = View.VISIBLE
        }

        itemView.fragment_assignments_list_item_photo_imageview.setOnClickListener{clickListener(assignment,true)}
        itemView.fragment_assignments_list_item_click_linearlayout.setOnClickListener{clickListener(assignment,false)}


    }
}