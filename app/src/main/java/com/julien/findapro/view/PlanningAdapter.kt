package com.julien.findapro.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R

class PlanningAdapter(var planningList: List<HashMap<String,Any?>>,val clickListener: (HashMap<String,Any?>, button:String) -> Unit):
    RecyclerView.Adapter<PlanningViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanningViewHolder {
        val v = LayoutInflater.from(parent?.context)
            .inflate(R.layout.activity_planning_item, parent, false)
        return PlanningViewHolder(v)

    }


    override fun onBindViewHolder(holder: PlanningViewHolder, position: Int) {
        holder.update(planningList.get(position),clickListener)

    }




    override fun getItemCount() = planningList.size
}