package com.julien.findapro.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Rating
import kotlinx.android.synthetic.main.activity_rating.*

class RatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        activity_rating_button.setOnClickListener {
            addRating()
        }
    }

    private fun addRating(){
        val db = FirebaseFirestore.getInstance()

        val rating = Rating(FirebaseAuth.getInstance().currentUser?.uid!!,activity_rating_rating_bar.rating,activity_rating_edit_text.text.toString(),intent.getStringExtra("assignment"))
        val user = if(intent.getStringExtra("user") == "users") "pro users" else "users"
        db.collection(user).document(intent.getStringExtra("userId"))
            .collection("rating").add(rating)

        updateRatingUser()
    }

    private fun updateRatingUser(){
        val db = FirebaseFirestore.getInstance()

        val rate = hashMapOf(
            "rating" to ""
        )
        val user = if(intent.getStringExtra("user") == "users") "pro users" else "users"
        db.collection(user).document(intent.getStringExtra("userId")).get().addOnSuccessListener { document ->
            var x:Double
            var y:Double
            val ratingBar:Double = activity_rating_rating_bar.rating.toDouble()
            if (document != null) {
                if (document["rating"] != null && document["ratingNb"] != null){
                    x = document["rating"] as Double
                    y = document["ratingNb"] as Double

                    x = (x*(y/(y+1.toDouble())))+(ratingBar*(1/(y+1.toDouble())))
                    y += 1.toDouble()
                }else{
                    x = ratingBar
                    y = 1.toDouble()
                }
                val rate = hashMapOf(
                    "rating" to x,
                    "ratingNb" to y
                )
                db.collection(user).document(intent.getStringExtra("userId")).update(rate as Map<String, Any>)
            } else {

            }
        }
            .addOnFailureListener { exception ->
                Log.d("rating", "get failed with ", exception)
            }
    }
}
