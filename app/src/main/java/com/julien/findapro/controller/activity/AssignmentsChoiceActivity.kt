package com.julien.findapro.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Message
import kotlinx.android.synthetic.main.activity_assignments.*
import kotlinx.android.synthetic.main.activity_assignments_choice.*

class AssignmentsChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments_choice)

        configureToolbar()

        val db = FirebaseFirestore.getInstance()

        loadData(db)



        assignments_choice_activity_accept_button.setOnClickListener {
            db.collection("assignments").document(intent.getStringExtra("id"))
                .update("status", "inProgress")
                .addOnSuccessListener {
                    Log.d(
                        "update status",
                        "DocumentSnapshot successfully updated!"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("update status", "Error updating document", e)
                }

            val message = Message("debut","bot")
            db.collection("assignments").document(intent.getStringExtra("id")).collection("chat").add(message)

            finish()
        }

            assignments_choice_activity_decline_button.setOnClickListener {
                db.collection("assignments").document(intent.getStringExtra("id"))
                    .update("status", "refuse")
                    .addOnSuccessListener {
                        Log.d(
                            "update status",
                            "DocumentSnapshot successfully updated!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w("update status", "Error updating document", e)
                    }

                finish()
            }

    }



    private fun loadData(db:FirebaseFirestore){
        db.collection("assignments").document(intent.getStringExtra("id")).get()
            .addOnSuccessListener { document ->
            if (document.data != null){
                assignments_choice_activity_describe_textview.text = document["describe"].toString()
                db.collection("users").document(document["userId"].toString()).get()
                    .addOnSuccessListener { document2 ->
                        if (document2.data != null){
                            activity_assignements_choice_full_name_text_view.text = document2["full name"].toString()
                            activity_assignements_choice_adress_text_view.text = document2["adress"].toString()
                            activity_assignements_choice_city_text_view.text = document2["city"].toString()

                            val nbRating:String = "(" + document2["ratingNb"].toString() + ") : "
                            activity_assignements_choice_nb_rating_text_view.text = nbRating
                            activity_assignment_choice_ratingbar.rating = document2["rating"].toString().toFloat()
                        }else{
                            Log.e("db", "no document")
                        }
                    }.addOnFailureListener{exeption ->
                        Log.e("db","get fail with",exeption)
                    }
            }else{
                Log.e("db", "no document")
            }
        }.addOnFailureListener{exeption ->
            Log.e("db","get fail with",exeption)
        }
    }

    private fun configureToolbar() {
        setSupportActionBar(activity_assignment_choice_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Proposition de mission"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
