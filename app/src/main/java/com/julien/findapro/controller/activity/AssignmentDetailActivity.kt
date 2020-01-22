package com.julien.findapro.controller.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Assignment
import com.julien.findapro.Utils.CircleTransform
import com.julien.findapro.Utils.Notification
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_assignment_detail.*
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.fragment_assignments_in_progress_item.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


class AssignmentDetailActivity : AppCompatActivity() {

    private lateinit var assignmentId: String
    private var assignment: Assignment? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var userType =""
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_detail)
        assignmentId = intent.getStringExtra("id") ?: "default value"

        configureToolbar()

        loadAssignment()

        sharedPreferences = getSharedPreferences("isPro", 0)
        //Toast.makeText(this,sharedPreferences.getBoolean("isPro",false).toString(),Toast.LENGTH_SHORT).show()
        userType = if(sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"


        activity_assignment_detail_intervention_date_button.setOnClickListener {
            pickDateTime()
            //activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE

        }

        activity_assignment_detail_cancel_assignment_button.setOnClickListener {
            cancelAssignment()
        }

        activity_assignment_detail_finish_assignment_button.setOnClickListener {
            finishAssignmentDialog()
        }

        activity_assignment_detail_photo_imageview.setOnClickListener {
            val id = if(sharedPreferences.getBoolean("isPro", false)) assignment?.proUserId else assignment?.userId
            val intent = Intent(this,
                ProfilActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
        }

        activity_assignment_detail_direction_button.setOnClickListener {
            openGoogleMap()
        }

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
                        latitude = document["latitude"] as Double
                        longitude = document["longitude"] as Double
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

        if (sharedPreferences.getBoolean("isPro", false)){
            activity_assignment_detail_direction_button.visibility = View.VISIBLE
        }



        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)


        if (assignment?.dateAssignment == null) {
            if (sharedPreferences.getBoolean("isPro", false)) {
                activity_assignment_detail_intervention_date_textview.visibility = View.GONE
                activity_assignment_detail_intervention_date_button.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.setBackgroundResource(R.drawable.baseline_add_circle_black_24)
            } else {
                //activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.visibility = View.GONE


                activity_assignment_detail_intervention_date_textview.text =
                    "En attente d'une date "
            }
        } else {
            activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
            if (sharedPreferences.getBoolean("isPro", false)) {
                //activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.setBackgroundResource(R.drawable.baseline_edit_black_24)

                activity_assignment_detail_intervention_date_textview.text =
                    convertDate(assignment?.dateAssignment, true)
            } else {
                //activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.visibility = View.GONE

                activity_assignment_detail_intervention_date_textview.text =
                    convertDate(assignment?.dateAssignment, true)
            }

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
        activity_assignment_detail_cancel_assignment_button.visibility = View.GONE
        activity_assignment_detail_finish_assignment_button.visibility =View.GONE
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_finish_date_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_more_information_linearlayout.visibility =View.VISIBLE
        activity_assignment_detail_intervention_date_button.visibility = View.GONE

        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)

        activity_assignment_detail_finish_date_textview.text =
            convertDate(assignment?.dateEnd, true)


        if (assignment!!.dateAssignment == null){
            activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
            activity_assignment_detail_intervention_date_textview.text = getString(R.string.no_date_found)
        }else{
            activity_assignment_detail_intervention_date_textview.text =
                convertDate(assignment?.dateAssignment, false)
        }



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
                    activity_assignment_detail_rating_button.visibility = View.GONE
                    activity_assignment_detail_more_information_textview.text =getString(R.string.assignment_finish)
                    //Toast.makeText(this,"déja noté",Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { exception ->

                Log.w("rating", "Error getting documents: ", exception)
            }
    }

    private fun cancel() {
        activity_assignment_detail_cancel_assignment_button.visibility = View.GONE
        activity_assignment_detail_finish_assignment_button.visibility =View.GONE
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.GONE

        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE

        val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        val date = dateFormat.format(assignment?.dateEnd).toString()
        if (sharedPreferences.getBoolean("isPro", false)) {
            activity_assignment_detail_more_information_textview.text = "Mission annulé le $date"
        } else {
            activity_assignment_detail_more_information_textview.text = "Mission annulé le $date"
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

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)

                val db = FirebaseFirestore.getInstance()
                db.collection("assignments").document(assignmentId)
                    .update("dateAssignment", pickedDateTime.time)
                    .addOnSuccessListener { Log.d("update date", "DocumentSnapshot successfully updated!")
                        val idReceiver = if(sharedPreferences.getBoolean("isPro", false)) assignment?.userId else assignment?.proUserId
                        Notification.createNotificationInDb(userType,
                            idReceiver.toString(),
                            FirebaseAuth.getInstance().currentUser?.uid!!,
                            assignmentId,
                            "Nouvelle date d'intervention",
                            "Une nouvelle date d'intervention vient d'être ajouté à votre mission",
                            "add date assignment")
                        loadAssignment()}
                    .addOnFailureListener { e -> Log.w("update date", "Error updating document", e) }

            }, startHour, startMinute, DateFormat.is24HourFormat(this)).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun cancelAssignment(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.cancel_assignment))
        builder.setMessage(getString(R.string.cancel_assignment_question))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("Oui") { dialog, which ->
            val db = FirebaseFirestore.getInstance()
            db.collection("assignments").document(assignmentId)
                .update("status", "cancel")
                .addOnSuccessListener { Log.d("update status", "DocumentSnapshot successfully updated!")
                    val idReceiver = if(sharedPreferences.getBoolean("isPro", false)) assignment?.userId else assignment?.proUserId
                    Notification.createNotificationInDb(userType,
                        idReceiver.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        assignmentId,
                        "Mission Annulée",
                        "Votre mission a été annulée",
                        "status update cancel")
                    }
                .addOnFailureListener { e -> Log.w("update status", "Error updating document", e) }

            db.collection("assignments").document(assignmentId)
                .update("dateEnd", FieldValue.serverTimestamp())
                .addOnSuccessListener { Log.d("update date", "DocumentSnapshot successfully updated!")
                    loadAssignment()}
                .addOnFailureListener { e -> Log.w("update date", "Error updating document", e) }
        }

        builder.setNegativeButton("Non") { dialog, which ->

        }


        builder.show()
    }

    private fun openGoogleMap(){

        //Toast.makeText(this,latitude.toString() + longitude.toString(),Toast.LENGTH_SHORT).show()

        val intent = Intent(android.content.Intent.ACTION_VIEW,
        Uri.parse("https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}"));
        startActivity(intent);


    }

    private fun finishAssignmentDialog(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.finish_assignment))
        builder.setMessage(getString(R.string.finish_missio_question))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton("Oui") { dialog, which ->
            val db = FirebaseFirestore.getInstance()
            db.collection("assignments").document(assignmentId)
                .update("status", "finish")
                .addOnSuccessListener { Log.d("update status", "DocumentSnapshot successfully updated!")
                    val idReceiver = if(sharedPreferences.getBoolean("isPro", false)) assignment?.userId else assignment?.proUserId
                    Notification.createNotificationInDb(userType,
                        idReceiver.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        assignmentId,
                        "Mission Terminée",
                        "Votre mission a été terminée",
                        "status update finish")
                }
                .addOnFailureListener { e -> Log.w("update status", "Error updating document", e) }

            db.collection("assignments").document(assignmentId)
                .update("dateEnd", FieldValue.serverTimestamp())
                .addOnSuccessListener { Log.d("update date", "DocumentSnapshot successfully updated!")
                    loadAssignment()}
                .addOnFailureListener { e -> Log.w("update date", "Error updating document", e) }
        }

        builder.setNegativeButton("Non") { dialog, which ->

        }


        builder.show()
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
            when (assignment?.status) {
                "pending" -> {
                    Toast.makeText(this,getString(R.string.chat_restrict_pending),Toast.LENGTH_SHORT).show()
                }
                "refuse" -> {
                    Toast.makeText(this,getString(R.string.chat_restrict_refuse),Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("assignment", assignmentId)
                    startActivity(intent)
                }
            }

        } else {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadAssignment()
    }
}
