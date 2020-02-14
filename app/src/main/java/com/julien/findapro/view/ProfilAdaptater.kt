package com.julien.findapro.view


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R

class ProfilAdaptater (private var profilList: ArrayList<HashMap<String,String>>, val clickListener: (HashMap<String,String>, isProfil:Boolean) -> Unit): RecyclerView.Adapter<ProfilViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_profil_item, parent, false)
        return ProfilViewHolder(v)

    }


    override fun onBindViewHolder(holder: ProfilViewHolder, position: Int) {
        holder.update(profilList[position],clickListener)

    }




    override fun getItemCount() = profilList.size
}