package com.julien.findapro.controller.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.utils.CircleTransform
import com.julien.findapro.utils.Internet
import com.julien.findapro.view.ProfilAdaptater
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfilActivity : AppCompatActivity() {

    private lateinit var userId: String
    private val profilList: ArrayList<HashMap<String, String>> = ArrayList()
    private var typeUser: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        userId = intent.getStringExtra("id") ?: "default value"

        configureToolbar()

        //check internet connexion
        if (Internet.isInternetAvailable(this)) {
            displayInformation()

            GlobalScope.launch {
                delay(2000)
                if (profilList.isEmpty()) {
                    this@ProfilActivity.runOnUiThread {
                        activity_profil_no_item.visibility = View.VISIBLE
                    }

                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }

    }

    //read information user in db and update view
    @SuppressLint("SetTextI18n")
    private fun displayInformation() {
        val db = FirebaseFirestore.getInstance()

        db.collection("pro users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    typeUser = "pro users"
                    Picasso.get().load(document["photo"].toString()).transform(CircleTransform())
                        .into(activity_profil_photo_imageview)
                    activity_profil_name_textview.text = document["full name"].toString()
                    activity_profil_job_textview.visibility = View.VISIBLE
                    activity_profil_job_textview.text = document["job"].toString()
                    activity_profil_city_textview.text = document["city"].toString()

                    if (document["rating"] != null) {
                        activity_profil_ratingbar.rating = document["rating"].toString().toFloat()
                        activity_profil_nb_rate_textview.text =
                            document["ratingNb"].toString() + " avis"
                    } else {
                        activity_profil_ratingbar_linearlayout.visibility = View.GONE
                    }

                    load("users", typeUser)
                } else {
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { documentUser ->
                            if (documentUser.data != null) {
                                typeUser = "users"
                                Picasso.get().load(documentUser["photo"].toString())
                                    .transform(CircleTransform())
                                    .into(activity_profil_photo_imageview)
                                activity_profil_name_textview.text =
                                    documentUser["full name"].toString()
                                activity_profil_city_textview.text = documentUser["city"].toString()


                                if (documentUser["rating"] != null) {
                                    activity_profil_ratingbar.rating =
                                        documentUser["rating"].toString().toFloat()
                                    activity_profil_nb_rate_textview.text =
                                        documentUser["ratingNb"].toString() + " avis"
                                } else {
                                    activity_profil_ratingbar_linearlayout.visibility = View.GONE
                                }
                                load("pro users", typeUser)
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

    //read all rate and add in list for display recycler view
    private fun load(userRater: String, userType: String) {
        val db = FirebaseFirestore.getInstance()

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
                                recycler_view_user_profil_activity.layoutManager =
                                    LinearLayoutManager(this)
                                val controller = AnimationUtils.loadLayoutAnimation(
                                    this,
                                    R.anim.layout_animation_fall_down
                                )
                                recycler_view_user_profil_activity.layoutAnimation = controller
                                recycler_view_user_profil_activity.adapter =
                                    ProfilAdaptater(profilList) { userItem: HashMap<String, String>, isProfil: Boolean ->
                                        userItemClicked(
                                            userItem,
                                            isProfil
                                        )
                                    }
                                recycler_view_user_profil_activity.scheduleLayoutAnimation()
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
        actionBar?.title = getString(R.string.toolbar_title_profil_activity)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        onBackPressed()


        return super.onOptionsItemSelected(item!!)
    }

    private fun userItemClicked(userItem: HashMap<String, String>, isProfil: Boolean) {

        if (Internet.isInternetAvailable(this)) {
            if (isProfil) {
                val intent = Intent(
                    this,
                    ProfilActivity::class.java
                )
                intent.putExtra("id", userItem["userId"])
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }
}
