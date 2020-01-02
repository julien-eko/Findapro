package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import kotlinx.android.synthetic.main.activity_assignment_detail.*

class AssignmentDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_detail)
        load()
    }

    private fun load(){
        val db = FirebaseFirestore.getInstance()
        db.collection("assignments").document(intent.getStringExtra("id")).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    activity_assignment_detail_describe_text_view.text = document["describe"].toString()
                } else {
                    Log.d("assignment detail", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("assignment detail", "get failed with ", exception)
            }
    }
}
