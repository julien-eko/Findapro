package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*

class UserListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun update(
        user: HashMap<String, String>,
        clickListener: (HashMap<String, String>, isProfil: Boolean) -> Unit
    ) {


        itemView.fragment_users_list_item_job_textview.text = user["full name"]
        itemView.fragment_users_list_item_name_textview.text = user["job"]
        itemView.fragment_users_list_item_city_textview.text = user["city"]


        if (user["rating"] != "null") {
            itemView.fragment_user_list_item_ratingbar.rating = user["rating"]!!.toFloat()
        } else {
            itemView.fragment_user_list_item_ratingbar.visibility = View.GONE
            itemView.fragment_user_list_item_no_rating.visibility = View.VISIBLE
        }

        Picasso.get().load(user["photo"]).transform(CircleTransform())
            .into(itemView.fragment_users_list_item_photo_imageview)



        itemView.fragment_users_list_item_photo_imageview.setOnClickListener {
            clickListener(
                user,
                true
            )
        }

        itemView.fragment_users_list_item_click_linearlayout.setOnClickListener {
            clickListener(
                user,
                false
            )
        }

    }
}