package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.utils.Assignment
import com.julien.findapro.utils.Internet
import com.julien.findapro.utils.Notification
import kotlinx.android.synthetic.main.activity_assignments.*
import java.util.*

class AssignmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments)

        configureToolbar()

        //check internet connexion
        if(Internet.isInternetAvailable(this)){
            loadDb()
        }else{
            Toast.makeText(this,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
        }


        //button save in db if edit text is not empty
        assignments_save_button.setOnClickListener {
            if(activity_assignments_describe_edit_text.text.toString().trim() == ""){
                Toast.makeText(this,getString(R.string.no_blank_field),Toast.LENGTH_SHORT).show()
            }else{
                if(Internet.isInternetAvailable(this)){
                    checkValidateRequest()
                }else{
                    Toast.makeText(this,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
                }
            }

        }
        assignments_finish_button.setOnClickListener {
            finish()
        }
    }


    //load user info in db and update view
    private fun loadDb(){
        val db = FirebaseFirestore.getInstance()
        db.collection("pro users").document(intent.getStringExtra("proId")!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    activity_assignements_full_name_text_view.text = document["full name"].toString()
                    activity_assignements_job_text_view.text = document["job"].toString()

                    if (document["rating"] != null){
                        activity_assignment_ratingbar.rating = document["rating"].toString().toFloat()

                        val nbRating:String = "(" + document["ratingNb"].toString() + ") : "
                        activity_assignements_nb_rating_text_view.text = nbRating
                    }else{
                        activity_assignment_ratingbar_linearlayout.visibility =View.GONE
                    }

                } else {
                    Log.d("load document pro user", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("load document pro user", "get failed with ", exception)
            }
    }

    //create new assignment and notification in db
    private fun saveInDb(){
        val db = FirebaseFirestore.getInstance()

        val assignments = Assignment(null,null,FirebaseAuth.getInstance().currentUser?.uid!!,intent.getStringExtra("proId"),"pending", activity_assignments_describe_edit_text.text.toString(),null)

        val uuid = UUID.randomUUID()
        db.collection("assignments").document(uuid.toString()).set(assignments)

            .addOnSuccessListener {
                Log.d("addDB", "DocumentSnapshot added ")
                Notification.createNotificationInDb("pro users",
                    assignments.proUserId.toString(),
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    uuid.toString(),
                    getString(R.string.new_assignment_title_notif),
                    getString(R.string.new_assignment_text_notif),
                    "new assignment created")

            }
            .addOnFailureListener { e ->
                Log.w("addDB", "Error adding document", e)
            }
        finish()
    }

    private fun checkValidateRequest(){
        val db = FirebaseFirestore.getInstance()

        db.collection("assignments").whereEqualTo("status","pending").whereEqualTo("proUserId",intent.getStringExtra("proId")).whereEqualTo("userId",FirebaseAuth.getInstance().currentUser?.uid!!)
        .get()
            .addOnSuccessListener { documents ->
                if(documents.size() > 0){
                 Toast.makeText(this,getString(R.string.imposible_already_request),Toast.LENGTH_SHORT).show()
                }else{
                    saveInDb()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }
    }

    //configure toolbar
    private fun configureToolbar() {
        setSupportActionBar(activity_assignements_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.title_toolbar_assignment_activity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}
