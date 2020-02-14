package com.julien.findapro.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R

class AssignmentsInProgressAdapter(private var assignmentList: ArrayList<HashMap<String,Any?>>, val context: Context, val clickListener: (HashMap<String,Any?>, isProfil:Boolean) -> Unit):
    RecyclerView.Adapter<AssignmentsInProgressViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentsInProgressViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_assignments_in_progress_item, parent, false)
        return AssignmentsInProgressViewHolder(v)

    }


    override fun onBindViewHolder(holder: AssignmentsInProgressViewHolder, position: Int) {
        holder.update(assignmentList[position],clickListener)

    }




    override fun getItemCount() = assignmentList.size
}