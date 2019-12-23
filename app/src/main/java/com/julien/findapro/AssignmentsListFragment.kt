package com.julien.findapro


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.julien.findapro.R
import com.julien.findapro.view.AssignmentListAdaptater
import com.julien.findapro.view.UserListAdapater
import kotlinx.android.synthetic.main.fragment_assignments_list.*
import kotlinx.android.synthetic.main.fragment_users_list.*

/**
 * A simple [Fragment] subclass.
 */
class AssignmentsListFragment : Fragment() {

    val assigmentsList: ArrayList<HashMap<String, String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignments_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()

    }

    private fun loadData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("assignments").whereEqualTo("pro user id",FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { documents ->

                    for (document in documents) {
                    db.collection("users").document(document["user id"].toString()).get()
                        .addOnSuccessListener { data ->

                            val assignment = hashMapOf(
                                "full name" to data["full name"].toString(),
                                "photo" to data["photo"].toString(),
                                "status" to document["status"].toString(),
                                "uid" to document["user id"].toString()
                            )
                            assigmentsList.add(assignment)


                             }
                        .addOnFailureListener { exception ->
                            Log.w("access db", "Error getting data", exception)
                        }
                        .addOnCompleteListener {  recycler_view_assignments_list_fragment.layoutManager = LinearLayoutManager(context)
                            recycler_view_assignments_list_fragment.adapter = AssignmentListAdaptater(
                                assigmentsList,
                                context!!,
                                { assignmentItem: HashMap<String, String> -> assignmentItemClicked(assignmentItem) })
                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }

    private fun assignmentItemClicked(assignmentItem : HashMap<String,String>) {
        //val intent = Intent(context,AssignmentsActivity::class.java)
        //intent.putExtra("proId",assignmentItem["uid"])
        //startActivity(intent)
    }

}

