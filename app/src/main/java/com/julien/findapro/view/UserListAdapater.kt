package com.julien.findapro.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R

class UserListAdapater(
    private var userList: ArrayList<HashMap<String, String>>,
    val context: Context,
    val clickListener: (HashMap<String, String>, isProfil: Boolean) -> Unit
) :
    RecyclerView.Adapter<UserListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_users_list_item, parent, false)
        return UserListViewHolder(v)

    }


    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.update(userList[position], clickListener)

    }


    override fun getItemCount() = userList.size


}