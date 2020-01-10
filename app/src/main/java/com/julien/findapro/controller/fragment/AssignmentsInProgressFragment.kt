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
import com.julien.findapro.controller.activity.AssignmentDetailActivity
import com.julien.findapro.controller.activity.AssignmentsChoiceActivity
import com.julien.findapro.view.AssignmentListAdaptater
import com.julien.findapro.view.AssignmentsInProgressAdapter
import kotlinx.android.synthetic.main.fragment_assignments_in_progress.*
import kotlinx.android.synthetic.main.fragment_assignments_list.*

/**
 * A simple [Fragment] subclass.
 */
class AssignmentsInProgressFragment : Fragment() {

    val assigmentsList: ArrayList<HashMap<String, Any?>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignments_in_progress, container, false)
    }



    private fun loadData() {
        val db = FirebaseFirestore.getInstance()
        val user:String = if(tag!! == "proUserId") "users" else "pro users"
        val userId:String = if(tag!! == "proUserId") "userId" else "proUserId"
        db.collection("assignments").whereEqualTo(tag!!, FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    db.collection(user).document(document[userId].toString()).get()
                        .addOnSuccessListener { data ->

                            val assignment:HashMap<String,Any?> = hashMapOf(
                                "full name" to data["full name"].toString(),
                                "photo" to data["photo"].toString(),
                                "status" to document["status"].toString(),
                                "dateEnd" to document["dateEnd"],
                                "dateCreated" to document["dateCreated"],
                                "id" to document.id
                            )
                            assigmentsList.add(assignment)


                        }
                        .addOnFailureListener { exception ->
                            Log.w("access db", "Error getting data", exception)
                        }
                        .addOnCompleteListener {  recycler_view_assignments_in_progress_fragment.layoutManager = LinearLayoutManager(context)
                            recycler_view_assignments_in_progress_fragment.adapter = AssignmentsInProgressAdapter(
                                assigmentsList,
                                context!!,
                                { assignmentItem: HashMap<String, Any?> -> assignmentItemClicked(assignmentItem) })
                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }

    private fun assignmentItemClicked(assignmentItem : HashMap<String,Any?>) {
        val intent = Intent(context, AssignmentDetailActivity::class.java)
        intent.putExtra("id",assignmentItem["id"].toString())
        startActivity(intent)
    }

    override fun onResume() {
        assigmentsList.clear()
        loadData()
        super.onResume()

    }
}
