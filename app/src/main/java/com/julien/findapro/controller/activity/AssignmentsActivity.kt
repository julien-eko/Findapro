package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import kotlinx.android.synthetic.main.activity_assignments.*

class AssignmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments)

        configureToolbar()

        assignments_save_button.setOnClickListener {
           saveInDb()
        }
    }

    private fun saveInDb(){
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

    private fun configureToolbar() {
        setSupportActionBar(activity_map_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setTitle("!!!")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}
