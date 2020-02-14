package com.julien.findapro.controller.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.utils.CircleTransform
import com.julien.findapro.utils.Internet
import com.julien.findapro.utils.Rating
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_rating.*

class RatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        configureToolbar()
        if(Internet.isInternetAvailable(this)){
            load()
        }else{
            Toast.makeText(this,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
        }

        activity_rating_button.setOnClickListener {

            if(Internet.isInternetAvailable(this)){
                addRating()
            }else{
                Toast.makeText(this,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
            }
        }
    }


    //read in db user information and update view
    private fun load(){
        val db = FirebaseFirestore.getInstance()
        val user = if(intent.getStringExtra("user") == "users") "pro users" else "users"
            val docRef = db.collection(user).document(intent.getStringExtra("userId")!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document["photo"].toString())
                            .transform(CircleTransform())
                            .into(activity_rating_photo_imageview)
                        activity_rating_name_textview.text =
                            document["full name"].toString()


                    } else {
                        Log.d("load user data", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("load user data", "get failed with ", exception)
                }
    }

    //create new rate in db
    private fun addRating(){
        val db = FirebaseFirestore.getInstance()

        val rating = Rating(FirebaseAuth.getInstance().currentUser?.uid!!,activity_rating_rating_bar.rating,activity_rating_edit_text.text.toString(),intent.getStringExtra("assignment"))
        val user = if(intent.getStringExtra("user") == "users") "pro users" else "users"
        db.collection(user).document(intent.getStringExtra("userId")!!)
            .collection("rating").add(rating)

        updateRatingUser()
    }

    //update rating user in db
    private fun updateRatingUser(){
        val db = FirebaseFirestore.getInstance()


        val user = if(intent.getStringExtra("user") == "users") "pro users" else "users"
        db.collection(user).document(intent.getStringExtra("userId")!!).get().addOnSuccessListener { document ->
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
                db.collection(user).document(intent.getStringExtra("userId")!!).update(rate as Map<String, Any>)
                    .addOnSuccessListener { Log.d("update rating", "DocumentSnapshot successfully updated!")
                        finish()
                    }
                    .addOnFailureListener { e -> Log.w("update rating", "Error updating document", e) }
            }
        }
            .addOnFailureListener { exception ->
                Log.d("rating", "get failed with ", exception)
            }
    }

    private fun configureToolbar() {
        setSupportActionBar(activity_rating_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.toolbar_title_ratin_activity)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()

        return super.onOptionsItemSelected(item)
    }
}
