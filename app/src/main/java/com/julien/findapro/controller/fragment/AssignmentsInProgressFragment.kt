package com.julien.findapro.controller.fragment


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.julien.findapro.R
import com.julien.findapro.utils.Internet
import com.julien.findapro.controller.activity.AssignmentDetailActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.AssignmentsInProgressAdapter
import kotlinx.android.synthetic.main.fragment_assignments_in_progress.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AssignmentsInProgressFragment : Fragment() {

    private val assigmentsList: ArrayList<HashMap<String, Any?>> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_assignments_in_progress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedPreferences = activity!!.getSharedPreferences("isPro", 0)

        //search button
        fragment_assignment_inprogress_list_cancel_search_button.setOnClickListener {
            fragment_assignment_inprogress_list_cancel_search_button.visibility = View.GONE
            assigmentsList.clear()
            if (Internet.isInternetAvailable(context)) {
                loadData()
            } else {
                Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activity_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val searchAssignmentsInProgressFragment = SearchAssignmentInProgressFragment()
                val transaction = fragmentManager!!.beginTransaction()
                searchAssignmentsInProgressFragment.show(transaction, "")

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //load data of db and display recycler view
    private fun loadData() {
        val db = FirebaseFirestore.getInstance()
        val user: String =
            if (sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"
        val userId: String =
            if (sharedPreferences.getBoolean("isPro", false)) "userId" else "proUserId"
        val userType: String =
            if (sharedPreferences.getBoolean("isPro", false)) "proUserId" else "userId"
        db.collection("assignments")
            .whereEqualTo(userType, FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    db.collection(user).document(document[userId].toString()).get()
                        .addOnSuccessListener { data ->
                            if (document["status"].toString() == "finish") {
                                db.collection(user).document(document[userId].toString())
                                    .collection("rating")
                                    .whereEqualTo("assignmentsId", document.id)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (documents.size() == 0) {
                                            //not rated
                                            val assignment: HashMap<String, Any?> = hashMapOf(
                                                "full name" to data["full name"].toString(),
                                                "photo" to data["photo"].toString(),
                                                "status" to "notRated",
                                                "dateEnd" to document["dateEnd"],
                                                "dateCreated" to document["dateCreated"],
                                                "idUser" to data.id,
                                                "id" to document.id
                                            )
                                            assigmentsList.add(assignment)


                                        } else {
                                            //rated
                                            val assignment: HashMap<String, Any?> = hashMapOf(
                                                "full name" to data["full name"].toString(),
                                                "photo" to data["photo"].toString(),
                                                "status" to document["status"].toString(),
                                                "dateEnd" to document["dateEnd"],
                                                "dateCreated" to document["dateCreated"],
                                                "idUser" to data.id,
                                                "id" to document.id
                                            )
                                            assigmentsList.add(assignment)

                                        }

                                    }
                                    .addOnFailureListener { exception ->

                                        Log.w("rating", "Error getting documents: ", exception)
                                    }
                            } else {
                                val assignment: HashMap<String, Any?> = hashMapOf(
                                    "full name" to data["full name"].toString(),
                                    "photo" to data["photo"].toString(),
                                    "status" to document["status"].toString(),
                                    "dateEnd" to document["dateEnd"],
                                    "dateCreated" to document["dateCreated"],
                                    "idUser" to data.id,
                                    "id" to document.id
                                )
                                assigmentsList.add(assignment)
                                diplayRecyclerView()
                            }


                        }
                        .addOnFailureListener { exception ->
                            Log.w("access db", "Error getting data", exception)
                        }
                        .addOnCompleteListener {
                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }

    //update recycler view with search filter
    private fun searchByStatus(status: String) {

        val statusAssignment: Any = when (status) {
            "Fini" -> {
                "finish"
            }
            "En cours" -> {
                "inProgress"
            }
            "Annuler" -> {
                "cancel"
            }
            "En attente" -> {
                "pending"
            }
            "Refuser" -> {
                "refuse"
            }
            else -> {
                true
            }
        }


        val db = FirebaseFirestore.getInstance()
        val user: String =
            if (sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"
        val userId: String =
            if (sharedPreferences.getBoolean("isPro", false)) "userId" else "proUserId"
        val userType: String =
            if (sharedPreferences.getBoolean("isPro", false)) "proUserId" else "userId"
        db.collection("assignments")
            .whereEqualTo(userType, FirebaseAuth.getInstance().currentUser?.uid!!)
            .whereEqualTo("status", statusAssignment).get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    db.collection(user).document(document[userId].toString()).get()
                        .addOnSuccessListener { data ->

                            if (document["status"].toString() == "finish") {
                                db.collection(user).document(document[userId].toString())
                                    .collection("rating")
                                    .whereEqualTo("assignmentsId", document.id)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (documents.size() == 0) {
                                            //not rated

                                            val assignment: HashMap<String, Any?> = hashMapOf(
                                                "full name" to data["full name"].toString(),
                                                "photo" to data["photo"].toString(),
                                                "status" to "notRated",
                                                "dateEnd" to document["dateEnd"],
                                                "dateCreated" to document["dateCreated"],
                                                "idUser" to data.id,
                                                "id" to document.id
                                            )
                                            assigmentsList.add(assignment)
                                            diplayRecyclerView()


                                        } else {
                                            //rated
                                            val assignment: HashMap<String, Any?> = hashMapOf(
                                                "full name" to data["full name"].toString(),
                                                "photo" to data["photo"].toString(),
                                                "status" to document["status"].toString(),
                                                "dateEnd" to document["dateEnd"],
                                                "dateCreated" to document["dateCreated"],
                                                "idUser" to data.id,
                                                "id" to document.id
                                            )
                                            assigmentsList.add(assignment)
                                            diplayRecyclerView()
                                        }

                                    }
                                    .addOnFailureListener { exception ->

                                        Log.w("rating", "Error getting documents: ", exception)
                                    }
                            } else {
                                val assignment: HashMap<String, Any?> = hashMapOf(
                                    "full name" to data["full name"].toString(),
                                    "photo" to data["photo"].toString(),
                                    "status" to document["status"].toString(),
                                    "dateEnd" to document["dateEnd"],
                                    "dateCreated" to document["dateCreated"],
                                    "idUser" to data.id,
                                    "id" to document.id
                                )
                                assigmentsList.add(assignment)
                                diplayRecyclerView()
                            }


                        }
                        .addOnFailureListener { exception ->
                            Log.w("access db", "Error getting data", exception)
                        }
                        .addOnCompleteListener {
                        }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }

    //click listerner on item recycler view
    private fun assignmentItemClicked(assignmentItem: HashMap<String, Any?>, isProfil: Boolean) {
        if (Internet.isInternetAvailable(context)) {
            if (isProfil) {
                //open profil
                val intent = Intent(
                    context,
                    ProfilActivity::class.java
                )
                intent.putExtra("id", assignmentItem["idUser"].toString())
                startActivity(intent)
            } else {
                //oppen detail activity
                val intent = Intent(context, AssignmentDetailActivity::class.java)
                intent.putExtra("id", assignmentItem["id"].toString())
                startActivity(intent)
            }
        } else {
            Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }

    private fun diplayRecyclerView() {
        if (context != null) {
            recycler_view_assignments_in_progress_fragment.layoutManager =
                LinearLayoutManager(context)
            val controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
            recycler_view_assignments_in_progress_fragment.layoutAnimation = controller
            recycler_view_assignments_in_progress_fragment.adapter = AssignmentsInProgressAdapter(
                assigmentsList,
                context!!
            ) { assignmentItem: HashMap<String, Any?>, isProfil: Boolean ->
                assignmentItemClicked(
                    assignmentItem,
                    isProfil
                )
            }
            recycler_view_assignments_in_progress_fragment.scheduleLayoutAnimation()
        }


    }

    override fun onResume() {

        //check internet
        if (Internet.isInternetAvailable(context)) {
            if (arguments != null) {
                fragment_assignment_inprogress_list_cancel_search_button.visibility = View.VISIBLE
                assigmentsList.clear()
                searchByStatus(arguments?.getString("status")!!)

            } else {
                fragment_assignment_inprogress_list_cancel_search_button.visibility = View.GONE
                assigmentsList.clear()
                loadData()
                GlobalScope.launch {
                    delay(2000)
                    if (assigmentsList.isEmpty()) {
                        activity?.runOnUiThread {
                            fragment_assignment_in_progress_list_no_item.visibility = View.VISIBLE
                        }

                    }

                }
            }
        } else {
            Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


        super.onResume()

    }
}
