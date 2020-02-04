package com.julien.findapro.controller.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Internet
import com.julien.findapro.view.PlanningAdapter
import com.julien.findapro.view.UserListAdapater
import kotlinx.android.synthetic.main.activity_notification_list.*
import kotlinx.android.synthetic.main.activity_planning.*
import kotlinx.android.synthetic.main.fragment_assignments_list.*
import kotlinx.android.synthetic.main.fragment_users_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlanningActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val planningList: ArrayList<HashMap<String, Any?>> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning)
        configureToolbar()

        sharedPreferences = getSharedPreferences("isPro", 0)

        //check internet connexion
        if (Internet.isInternetAvailable(this)) {
            loadPlanning()
            GlobalScope.launch {
                delay(2000)
                if (planningList.isEmpty()) {
                    this@PlanningActivity.runOnUiThread(java.lang.Runnable {
                        activity_planning_list_no_item.visibility = View.VISIBLE
                    })

                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }

    }


    //search in db assignment with date and add in list and load recycler view
    private fun loadPlanning() {
        val db = FirebaseFirestore.getInstance()
        val user: String =
            if (sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"
        val otherUserId: String =
            if (sharedPreferences.getBoolean("isPro", false)) "userId" else "proUserId"
        val myUserId: String =
            if (sharedPreferences.getBoolean("isPro", false)) "proUserId" else "userId"
        val userType: String =
            if (sharedPreferences.getBoolean("isPro", false)) "proUserId" else "userId"

        db.collection("assignments")
            .whereEqualTo(myUserId, FirebaseAuth.getInstance().currentUser?.uid!!)
            .whereEqualTo("status", "inProgress")
            .orderBy("dateAssignment").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    db.collection(user).document(document[otherUserId].toString()).get()
                        .addOnSuccessListener { documentUser ->
                            if (documentUser != null) {
                                if (document["dateAssignment"] != null) {
                                    val planning: HashMap<String, Any?> = hashMapOf(
                                        "full name" to documentUser["full name"].toString(),
                                        "photo" to documentUser["photo"].toString(),
                                        "date" to document["dateAssignment"],
                                        "isPro" to sharedPreferences.getBoolean("isPro", false),
                                        "assignmentId" to document.id,
                                        "latitude" to documentUser["latitude"],
                                        "longitude" to documentUser["longitude"],
                                        "uid" to documentUser.id
                                    )

                                    planningList.add(planning)

                                    recycler_view_planning_activity.layoutManager =
                                        LinearLayoutManager(this)
                                    val controller = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation_fall_down)
                                    recycler_view_planning_activity.layoutAnimation = controller
                                    recycler_view_planning_activity.adapter = PlanningAdapter(
                                        planningList,

                                        { planningItem: HashMap<String, Any?>, button: String ->
                                            planningItemClicked(
                                                planningItem,
                                                button
                                            )
                                        })
                                    recycler_view_planning_activity.scheduleLayoutAnimation()
                                }


                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                        }


                }


            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


    }


    private fun configureToolbar() {
        setSupportActionBar(activity_planning_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Planning"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    //click on item in list
    private fun planningItemClicked(planningItem: HashMap<String, Any?>, button: String) {

        if (Internet.isInternetAvailable(this)) {
            when (button) {
                "profil" -> {
                    //open profil
                    val intent = Intent(
                        this,
                        ProfilActivity::class.java
                    )
                    intent.putExtra("id", planningItem["uid"].toString())
                    startActivity(intent)
                }
                "message" -> {
                    //open chat
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("assignment", planningItem["assignmentId"].toString())
                    startActivity(intent)
                }
                "detail" -> {
                    //open detail assignment acticity
                    val intent = Intent(this, AssignmentDetailActivity::class.java)
                    intent.putExtra("id", planningItem["assignmentId"].toString())
                    startActivity(intent)
                }
                "map" -> {
                    //open google map with position's user
                    val intent = Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${planningItem["latitude"].toString()},${planningItem["longitude"].toString()}")
                    );
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }

    companion object {
        private const val TAG = "Planning activity"
    }
}
