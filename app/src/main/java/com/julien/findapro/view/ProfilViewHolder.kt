package com.julien.findapro.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.Utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil_item.view.*
import kotlinx.android.synthetic.main.fragment_assignments_list_item.view.*
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*

class ProfilViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun update(profilRate:HashMap<String,String>,clickListener: (HashMap<String,String>,isProfil:Boolean) -> Unit) {

        itemView.activity_profil_item_comment_textview.text=profilRate["comment"]
        itemView.activity_profil_item_name_textview.text = profilRate["full name"]
        itemView.activity_profil_item_ratingbar.rating = profilRate["rating"]!!.toFloat()



        Picasso.get().load(profilRate["photo"]).transform(CircleTransform()).into(itemView.activity_profil_item_photo_imageview)

        itemView.activity_profil_item_photo_imageview.setOnClickListener{clickListener(profilRate,true)}


    }
}