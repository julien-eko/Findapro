package com.julien.findapro.controller.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.view.PlanningAdapter
import com.julien.findapro.view.UserListAdapater
import kotlinx.android.synthetic.main.activity_notification_list.*
import kotlinx.android.synthetic.main.activity_planning.*
import kotlinx.android.synthetic.main.fragment_users_list.*

class PlanningActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val planningList:ArrayList<HashMap<String,Any?>> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planning)
        configureToolbar()

        sharedPreferences = getSharedPreferences("isPro", 0)


        loadPlanning()
    }


    private fun loadPlanning(){
        val db = FirebaseFirestore.getInstance()
        val user:String = if(sharedPreferences.getBoolean("isPro",false)) "users" else "pro users"
        val otherUserId:String = if(sharedPreferences.getBoolean("isPro",false)) "userId" else "proUserId"
        val myUserId:String = if(sharedPreferences.getBoolean("isPro",false)) "proUserId" else "userId"
        val userType:String = if (sharedPreferences.getBoolean("isPro",false)) "proUserId" else "userId"

        db.collection("assignments").whereEqualTo(myUserId,FirebaseAuth.getInstance().currentUser?.uid!!).whereEqualTo("status","inProgress")
            .orderBy("dateAssignment").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    db.collection(user).document(document[otherUserId].toString()).get()
                        .addOnSuccessListener { documentUser ->
                            if (documentUser != null) {
                                if (document["dateAssignment"] != null){
                                    val planning:HashMap<String,Any?> = hashMapOf(
                                        "full name" to documentUser["full name"].toString(),
                                        "photo" to documentUser["photo"].toString(),
                                        "date" to document["dateAssignment"],
                                        "isPro" to sharedPreferences.getBoolean("isPro",false),
                                        "assignmentId" to document.id,
                                        "latitude" to documentUser["latitude"],
                                        "longitude" to documentUser["longitude"],
                                        "uid" to documentUser.id)

                                    planningList.add(planning)

                                    recycler_view_planning_activity.layoutManager =
                                        LinearLayoutManager(this)
                                    recycler_view_planning_activity.adapter = PlanningAdapter(
                                        planningList,

                                        { planningItem: HashMap<String, Any?>, button: String ->
                                            planningItemClicked(
                                                planningItem,
                                                button
                                            )
                                        })
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

    private fun planningItemClicked(planningItem : HashMap<String,Any?>,button:String) {

        when (button) {
            "profil" -> {
                val intent = Intent(
                    this,
                    ProfilActivity::class.java
                )
                intent.putExtra("id", planningItem["uid"].toString())
                startActivity(intent)
            }
            "message" -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("assignment",planningItem["assignmentId"].toString())
                startActivity(intent)
            }
            "detail" -> {
                val intent = Intent(this, AssignmentDetailActivity::class.java)
                intent.putExtra("id",planningItem["assignmentId"].toString())
                startActivity(intent)
            }
            "map" -> {
                val intent = Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${planningItem["latitude"].toString()},${planningItem["longitude"].toString()}"));
                startActivity(intent);
            }
        }


    }

    companion object{
        private const val TAG = "Planning activity"
    }
}