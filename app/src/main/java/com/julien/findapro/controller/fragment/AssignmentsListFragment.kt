package com.julien.findapro.controller.fragment


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R

import com.julien.findapro.controller.activity.AssignmentsChoiceActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.AssignmentListAdaptater
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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_assignments_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragment_assignment_list_cancel_search_button.setOnClickListener {
            fragment_assignment_list_cancel_search_button.visibility = View.GONE
            assigmentsList.clear()
            loadData()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activity_toolbar, menu)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                //Toast.makeText(context,"test",Toast.LENGTH_SHORT).show()
                val searchAssignmenFragment = SearchAssignmenFragment()
                val transaction = fragmentManager!!.beginTransaction()
                searchAssignmenFragment.show(transaction, "")

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("assignments").whereEqualTo("proUserId",FirebaseAuth.getInstance().currentUser?.uid!!).whereEqualTo("status","pending").get()
            .addOnSuccessListener { documents ->

                    for (document in documents) {
                    db.collection("users").document(document["userId"].toString()).get()
                        .addOnSuccessListener { data ->

                            val assignment = hashMapOf(
                                "full name" to data["full name"].toString(),
                                "photo" to data["photo"].toString(),
                                "city" to data["city"].toString(),
                                "rating" to data["rating"].toString(),
                                "userId" to data.id,
                                "id" to document.id
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
                                { assignmentItem: HashMap<String, String>,isProfil:Boolean -> assignmentItemClicked(assignmentItem,isProfil) })
                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }


    private fun searchAssignment(minRating:Double,maxDistance:Float){
        val db = FirebaseFirestore.getInstance()

        db.collection("assignments").whereEqualTo("proUserId",FirebaseAuth.getInstance().currentUser?.uid!!).whereEqualTo("status","pending").get()
            .addOnSuccessListener { documents ->
                db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
                    .addOnSuccessListener { dataProUser ->

                        var myLocation = Location("")
                        myLocation.latitude = dataProUser["latitude"].toString().toDouble()
                        myLocation.longitude = dataProUser["longitude"].toString().toDouble()
                        for (document in documents) {



                            db.collection("users").document(document["userId"].toString()).get()
                                .addOnSuccessListener { dataUser ->


                                    var locationUser = Location("")
                                    locationUser.latitude = dataUser["latitude"].toString().toDouble()
                                    locationUser.longitude = dataUser["longitude"].toString().toDouble()

                                    val distanceInMeters: Float = myLocation.distanceTo(locationUser)

                                    val rating:Double = if (dataUser["rating"] == null) -1.0 else dataUser["rating"].toString().toDouble()

                                    if (distanceInMeters < maxDistance * 1000 && rating > minRating){
                                        val assignment = hashMapOf(
                                            "full name" to dataUser["full name"].toString(),
                                            "photo" to dataUser["photo"].toString(),
                                            "city" to dataUser["city"].toString(),
                                            "rating" to dataUser["rating"].toString(),
                                            "userId" to dataUser.id,
                                            "id" to document.id
                                        )
                                        assigmentsList.add(assignment)
                                    }



                                }
                                .addOnFailureListener { exception ->
                                    Log.w("access db", "Error getting data", exception)
                                }
                                .addOnCompleteListener {  recycler_view_assignments_list_fragment.layoutManager = LinearLayoutManager(context)
                                    recycler_view_assignments_list_fragment.adapter = AssignmentListAdaptater(
                                        assigmentsList,
                                        context!!,
                                        { assignmentItem: HashMap<String, String>,isProfil:Boolean -> assignmentItemClicked(assignmentItem,isProfil) })
                                }

                        }



                    }
                    .addOnFailureListener { exception ->
                        Log.w("access db", "Error getting data", exception)
                    }


            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }
    private fun assignmentItemClicked(assignmentItem : HashMap<String,String>,isProfil:Boolean) {
        if (isProfil){
            val intent = Intent(context,
                ProfilActivity::class.java)
            intent.putExtra("id",assignmentItem["userId"])
            startActivity(intent)
        }else{
            val intent = Intent(context,
                AssignmentsChoiceActivity::class.java)
            intent.putExtra("id",assignmentItem["id"])
            startActivity(intent)
        }

    }

    override fun onResume() {


        if (arguments != null) {
            fragment_assignment_list_cancel_search_button.visibility = View.VISIBLE
            assigmentsList.clear()
            val rating: Double =
                if (arguments?.getDouble("rating") == null) 0.0 else arguments?.getDouble("rating")!!

            searchAssignment(rating,arguments?.getFloat("maxDistance")!!)
            //Toast.makeText(context,arguments?.getFloat("maxDistance")!!.toString(),Toast.LENGTH_SHORT).show()

        } else {
            fragment_assignment_list_cancel_search_button.visibility = View.GONE
            assigmentsList.clear()
            loadData()

        }
        super.onResume()

    }

}

