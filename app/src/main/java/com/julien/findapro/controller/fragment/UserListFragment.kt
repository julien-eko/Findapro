package com.julien.findapro.controller.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.controller.activity.AssignmentsActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.UserListAdapater
import kotlinx.android.synthetic.main.fragment_users_list.*

/**
 * A simple [Fragment] subclass.
 */
class UserListFragment : Fragment() {

    val userList:ArrayList<HashMap<String,String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_users_list, container, false)



    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

         loadData()


    }

    private fun loadData(){
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { resullt ->
                db.collection("pro users").whereEqualTo("postal code",resullt["postal code"]).get()
                    .addOnSuccessListener { documents ->

                        for (document in documents){

                            val user = hashMapOf(
                                "full name" to document["full name"].toString(),
                                "job" to document["job"].toString(),
                                "photo" to document["photo"].toString(),
                                "city" to document["city"].toString(),
                                "rating" to document["rating"].toString(),
                                "uid" to document.id
                            )

                            userList.add(user)
                        }
                        recycler_view_users_list_fragment.layoutManager = LinearLayoutManager(context)
                        recycler_view_users_list_fragment.adapter = UserListAdapater(userList,context!!,{ userItem : HashMap<String,String>,isProfil:Boolean -> userItemClicked(userItem,isProfil) })
                    }
                    .addOnFailureListener {exception ->
                        Log.w("access db","Error getting data", exception)
                    }
            }
            .addOnFailureListener{exception ->
                Log.w("access db","Error getting data", exception)
            }

    }


    private fun userItemClicked(userItem : HashMap<String,String>,isProfil:Boolean) {

        if (isProfil){
            val intent = Intent(context,
                ProfilActivity::class.java)
            intent.putExtra("id",userItem["uid"])
            startActivity(intent)
        }else{
            val intent = Intent(context,
                AssignmentsActivity::class.java)
            intent.putExtra("proId",userItem["uid"])
            startActivity(intent)
        }

    }

}
