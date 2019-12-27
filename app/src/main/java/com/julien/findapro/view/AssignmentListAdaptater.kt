package com.julien.findapro.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R

class AssignmentListAdaptater(var assignmentList: ArrayList<HashMap<String,String>>, val context: Context, val clickListener: (HashMap<String,String>) -> Unit):
    RecyclerView.Adapter<AssignmentListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentListViewHolder {
        val v = LayoutInflater.from(parent?.context)
            .inflate(R.layout.fragment_assignments_list_item, parent, false)
        return AssignmentListViewHolder(v)

    }


    override fun onBindViewHolder(holder: AssignmentListViewHolder, position: Int) {
        holder.update(assignmentList.get(position),clickListener)

    }




    override fun getItemCount() = assignmentList.size

}