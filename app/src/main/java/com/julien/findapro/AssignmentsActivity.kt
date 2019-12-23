package com.julien.findapro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_assignments.*
import kotlinx.android.synthetic.main.activity_information_form.*

class AssignmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments)

        assignments_save_button.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            val assignments = hashMapOf(
                "user id" to FirebaseAuth.getInstance().currentUser?.uid!!,
                "pro user id" to intent.getStringExtra("proId"),
                "describe" to "test",
                "status" to "pending"
            )

            db.collection("assignments").document().set(assignments)

                .addOnSuccessListener { documentReference ->
                    Log.d("addDB", "DocumentSnapshot added ")
                }
                .addOnFailureListener { e ->
                    Log.w("addDB", "Error adding document", e)
                }
            finish()
        }
    }
}
