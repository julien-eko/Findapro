package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Message
import kotlinx.android.synthetic.main.activity_assignments_choice.*

class AssignmentsChoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments_choice)
        val db = FirebaseFirestore.getInstance()

        loadData(db)



        assignments_choice_activity_accept_button.setOnClickListener {
            db.collection("assignments").document(intent.getStringExtra("id"))
                .update("status", "accept")
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
        }

            assignments_choice_activity_decline_button.setOnClickListener {
                db.collection("assignments").document(intent.getStringExtra("id"))
                    .update("status", "decline")
                    .addOnSuccessListener {
                        Log.d(
                            "update status",
                            "DocumentSnapshot successfully updated!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w("update status", "Error updating document", e)
                    }

            }

    }

    private fun loadData(db:FirebaseFirestore){
        db.collection("assignments").document(intent.getStringExtra("id")).get().addOnSuccessListener { document ->
            if (document.data != null){
                assignments_choice_activity_describe_textview.text = document["describe"].toString()
            }else{
                Log.e("db", "no document")
            }
        }.addOnFailureListener{exeption ->
            Log.e("db","get fail with",exeption)
        }
    }
}
