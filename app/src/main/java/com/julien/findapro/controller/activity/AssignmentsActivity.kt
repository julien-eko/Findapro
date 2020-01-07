package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Assignment
import kotlinx.android.synthetic.main.activity_assignments.*

class AssignmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments)

        configureToolbar()

        loadDb()

        assignments_save_button.setOnClickListener {
            if(activity_assignments_describe_edit_text.text.toString().trim() == ""){
                Toast.makeText(this,getString(R.string.no_blank_field),Toast.LENGTH_SHORT).show()
            }else{
                saveInDb()
            }

        }
        assignments_finish_button.setOnClickListener {
            finish()
        }
    }

    private fun loadDb(){
        val db = FirebaseFirestore.getInstance()

        db.collection("pro users").document(intent.getStringExtra("proId")).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    activity_assignements_full_name_text_view.text = document["full name"].toString()
                    activity_assignements_job_text_view.text = document["job"].toString()
                    activity_assignment_ratingbar.rating = document["rating"].toString().toFloat()

                    val nbRating:String = "(" + document["ratingNb"].toString() + ") : "
                    activity_assignements_nb_rating_text_view.text = nbRating
                } else {
                    Log.d("load document pro user", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("load document pro user", "get failed with ", exception)
            }
    }
    private fun saveInDb(){
        val db = FirebaseFirestore.getInstance()

        val assignments = Assignment(FirebaseAuth.getInstance().currentUser?.uid!!,intent.getStringExtra("proId"),"pending", activity_assignments_describe_edit_text.text.toString(),null)
        /*
        val assignments = hashMapOf(
            "dateCreated" to
            "user id" to FirebaseAuth.getInstance().currentUser?.uid!!,
            "pro user id" to intent.getStringExtra("proId"),
            "describe" to activity_assignments_describe_edit_text.text.toString(),
            "status" to "pending"
        )
*/
        db.collection("assignments").document().set(assignments)

            .addOnSuccessListener { documentReference ->
                Log.d("addDB", "DocumentSnapshot added ")
            }
            .addOnFailureListener { e ->
                Log.w("addDB", "Error adding document", e)
            }
        finish()
    }

    private fun configureToolbar() {
        setSupportActionBar(activity_assignements_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setTitle("Demande d'intervention")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}
