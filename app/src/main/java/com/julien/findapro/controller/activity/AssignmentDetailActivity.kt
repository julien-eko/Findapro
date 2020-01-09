package com.julien.findapro.controller.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Assignment
import com.julien.findapro.Utils.CircleTransform
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_assignment_detail.*
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.fragment_assignments_in_progress_item.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AssignmentDetailActivity : AppCompatActivity() {

    private lateinit var assignmentId: String
    private var assignment: Assignment? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_detail)
        assignmentId = intent.getStringExtra("id") ?: "default value"

        configureToolbar()

        loadAssignment()

        sharedPreferences = getSharedPreferences("isPro", 0)
        //Toast.makeText(this,sharedPreferences.getBoolean("isPro",false).toString(),Toast.LENGTH_SHORT).show()


    }

    private fun loadAssignment() {
        val db = FirebaseFirestore.getInstance()
        //var assignment:Assignment?
        val docRef = db.collection("assignments").document(intent.getStringExtra("id"))
        docRef.get().addOnSuccessListener { documentSnapshot ->
            assignment = documentSnapshot.toObject(Assignment::class.java)

            activity_assignment_detail_describe_text_view.text = assignment?.describe

            displayDataUser(sharedPreferences.getBoolean("isPro", false))

            val assignmentStatus = assignment?.status

            when (assignmentStatus) {
                "inProgress" -> inProgressAssignment()
                "cancel" -> cancel()
                "refuse" -> refuseAssignment()
                "pending" -> pendingAssignment()
                "finish" -> finishAssignment()
            }
        }


    }

    private fun displayDataUser(isPro: Boolean) {
        val db = FirebaseFirestore.getInstance()

        if (isPro) {
            val docRef = db.collection("users").document(assignment?.userId.toString())
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document["photo"].toString())
                            .transform(CircleTransform())
                            .into(activity_assignment_detail_photo_imageview)
                        activity_assignment_detail_name_textview.text =
                            document["full name"].toString()
                        activity_assignment_detail_adress_textview.text =
                            document["adress"].toString()
                        activity_assignment_detail_city_textview.text = document["city"].toString()
                        activity_assignment_detail_postal_code_textview.text =
                            document["postal code"].toString()

                    } else {
                        Log.d("load user data", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("load user data", "get failed with ", exception)
                }
        } else {
            val docRef = db.collection("pro users").document(assignment?.proUserId.toString())
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Picasso.get().load(document["photo"].toString())
                            .transform(CircleTransform())
                            .into(activity_assignment_detail_photo_imageview)
                        activity_assignment_detail_name_textview.text =
                            document["full name"].toString()
                        activity_assignment_detail_name_textview.text =
                            document["full name"].toString()
                        activity_assignment_detail_adress_textview.text = document["job"].toString()
                        activity_assignment_detail_city_textview.text = document["city"].toString()
                        activity_assignment_detail_postal_code_textview.text =
                            document["postal code"].toString()

                    } else {
                        Log.d("load user data", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("load user data", "get failed with ", exception)
                }
        }


    }

    private fun pendingAssignment() {
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)
        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE


        if (sharedPreferences.getBoolean("isPro", false)) {
            activity_assignment_detail_more_information_textview.text =
                getString(R.string.pending_assignment)
            activity_assignment_detail_accept_assignment_button.visibility = View.VISIBLE

            activity_assignment_detail_accept_assignment_button.setOnClickListener {
                val intent = Intent(
                    this,
                    AssignmentsChoiceActivity::class.java
                )
                intent.putExtra("id", assignmentId)
                startActivity(intent)
            }
        } else {

            activity_assignment_detail_more_information_textview.text =
                getString(R.string.waiting_for_response)
        }

    }

    private fun inProgressAssignment() {
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.VISIBLE


        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)


        if (assignment?.dateAssignment == null) {
            if (sharedPreferences.getBoolean("isPro", false)) {
                activity_assignment_detail_intervention_date_textview.visibility = View.GONE
                activity_assignment_detail_intervention_date_button.visibility = View.VISIBLE
            } else {
                //activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.visibility = View.GONE

                activity_assignment_detail_intervention_date_textview.text =
                    "En attente d'une datte "
            }
        } else {
            activity_assignment_detail_intervention_date_textview.text =
                convertDate(assignment?.dateAssignment, true)
        }

        activity_assignment_detail_cancel_assignment_button.visibility = View.VISIBLE

        activity_assignment_detail_finish_assignment_button.visibility = View.VISIBLE
    }

    private fun refuseAssignment() {
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)

        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE

        if (sharedPreferences.getBoolean("isPro", false)) {
            activity_assignment_detail_more_information_textview.text =
                getString(R.string.pro_refuse_assignment)
        } else {
            activity_assignment_detail_more_information_textview.text =
                getString(R.string.user_refuse_assignment)
        }

    }

    private fun finishAssignment() {
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_finish_date_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_more_information_linearlayout.visibility =View.VISIBLE

        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)

        activity_assignment_detail_finish_date_textview.text =
            convertDate(assignment?.dateEnd, true)

        activity_assignment_detail_intervention_date_textview.text =
            convertDate(assignment?.dateAssignment, false)


        val db = FirebaseFirestore.getInstance()
        val user =
            if (sharedPreferences.getBoolean("isPro", false)) {
                "pro users"
            } else {
                "users"
            }
        val userId =
            if(user == "pro users"){
                assignment?.userId
            }
            else{
                assignment?.proUserId
            }
        val userInv =
            if(user == "pro users"){
                "users"
            }
            else{
                "pro users"
            }
        db.collection(userInv).document(userId.toString()).collection("rating")
            .whereEqualTo("assignmentsId",assignmentId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size()==0){
                    //not rated
                    activity_assignment_detail_more_information_textview.text = getString(R.string.not_rated)
                    activity_assignment_detail_rating_button.visibility = View.VISIBLE
                    activity_assignment_detail_rating_button.setOnClickListener {

                        val intent = Intent(this,
                            RatingActivity::class.java)
                        intent.putExtra("user",user)
                        intent.putExtra("userId", userId)
                        intent.putExtra("assignment",assignmentId)
                        startActivity(intent)
                    }
                    //Toast.makeText(this,"pas noté",Toast.LENGTH_SHORT).show()
                }else{
                    //rated
                    activity_assignment_detail_more_information_textview.text =getString(R.string.assignment_finish)
                    //Toast.makeText(this,"déja noté",Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { exception ->

                Log.w("rating", "Error getting documents: ", exception)
            }
    }

    private fun cancel() {
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)

        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE

        if (sharedPreferences.getBoolean("isPro", false)) {
            activity_assignment_detail_more_information_textview.text = "Mission annulé"
        } else {
            activity_assignment_detail_more_information_textview.text = "Mission annulé"
        }
    }

    private fun convertDate(date: Date?, whithHour: Boolean): String {
        var dateFormatDay: SimpleDateFormat
        dateFormatDay = if (whithHour) {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        }


        return dateFormatDay.format(date).toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.assignment_detail_toolbar, menu)


        return super.onCreateOptionsMenu(menu)
    }


    private fun configureToolbar() {
        setSupportActionBar(activity_assignment_detail_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Description"


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var itemid = item?.itemId


        if (itemid == R.id.action_open_chat) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("assignment", assignmentId)
            startActivity(intent)
        } else {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}
