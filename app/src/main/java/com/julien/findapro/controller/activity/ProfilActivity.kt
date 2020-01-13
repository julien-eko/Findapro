package com.julien.findapro.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.CircleTransform
import com.julien.findapro.view.AssignmentListAdaptater
import com.julien.findapro.view.ProfilAdaptater
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_assignment_detail.*
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.android.synthetic.main.fragment_assignments_in_progress.*
import kotlinx.android.synthetic.main.fragment_assignments_list.*
import kotlinx.android.synthetic.main.fragment_users_list_item.view.*

class ProfilActivity : AppCompatActivity() {

    private lateinit var userId: String
    val profilList: ArrayList<HashMap<String, String>> = ArrayList()
    var typeUser:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        userId = intent.getStringExtra("id") ?: "default value"

        configureToolbar()
        displayInformation()


        //load()
    }

    private fun displayInformation(){
        val db =FirebaseFirestore.getInstance()

        db.collection("pro users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    typeUser = "pro users"
                    Picasso.get().load(document["photo"].toString()).transform(CircleTransform()).into(activity_profil_photo_imageview)
                    activity_profil_name_textview.text = document["full name"].toString()
                    activity_profil_job_textview.visibility = View.VISIBLE
                    activity_profil_job_textview.text = document["job"].toString()
                    activity_profil_ratingbar.rating = document["rating"].toString().toFloat()
                    activity_profil_city_textview.text = document["city"].toString()
                    activity_profil_nb_rate_textview.text = document["ratingNb"].toString() + " avis"
                    load("users",typeUser)
                } else {
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document.data != null) {
                                typeUser = "users"
                                Picasso.get().load(document["photo"].toString()).transform(CircleTransform()).into(activity_profil_photo_imageview)
                                activity_profil_name_textview.text = document["full name"].toString()
                                activity_profil_ratingbar.rating = document["rating"].toString().toFloat()
                                activity_profil_city_textview.text = document["city"].toString()
                                activity_profil_nb_rate_textview.text = document["ratingNb"].toString() + " avis"
                                load("pro users",typeUser)
                            } else {

                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("user profil", "get failed with ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("user profil", "get failed with ", exception)
            }


    }

    private fun load(userRater:String,userType:String){
        val db =FirebaseFirestore.getInstance()

        db.collection(userType).document(userId).collection("rating").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection(userRater).document(document["raterId"].toString()).get()
                        .addOnSuccessListener { documentRater ->
                            if (documentRater != null) {
                                Log.e("user profil", "test")
                                val profil = hashMapOf(
                                    "full name" to documentRater["full name"].toString(),
                                    "photo" to documentRater["photo"].toString(),
                                    "comment" to document["comment"].toString(),
                                    "rating" to document["rating"].toString(),
                                    "userId" to documentRater.id,
                                    "id" to document.id
                                )
                                profilList.add(profil)
                                recycler_view_user_profil_activity.layoutManager = LinearLayoutManager(this)
                                recycler_view_user_profil_activity.adapter = ProfilAdaptater(profilList,{ userItem : HashMap<String,String>,isProfil:Boolean -> userItemClicked(userItem,isProfil) })
                            } else {
                                Log.d("user profil", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("user profil", "get failed with ", exception)
                        }
                }


            }
            .addOnFailureListener { exception ->
                Log.w("profil rate", "Error getting documents: ", exception)
            }.addOnCompleteListener {

            }
    }

    private fun configureToolbar() {
        setSupportActionBar(activity_profil_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Profil"


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

            onBackPressed()


        return super.onOptionsItemSelected(item)
    }

    private fun userItemClicked(userItem : HashMap<String,String>,isProfil:Boolean) {

        if (isProfil){
            val intent = Intent(this,
                ProfilActivity::class.java)
            intent.putExtra("id",userItem["userId"])
            startActivity(intent)
        }else{

        }

    }
}
