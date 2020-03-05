package com.julien.findapro.controller.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.model.Assignment
import com.julien.findapro.utils.CircleTransform
import com.julien.findapro.utils.Internet
import com.julien.findapro.model.Notification
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_assignment_detail.*
import java.text.SimpleDateFormat
import java.util.*


class AssignmentDetailActivity : AppCompatActivity() {

    private lateinit var assignmentId: String
    private var assignment: Assignment? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var userType = ""
    private var latitude = 0.0
    private var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_detail)
        assignmentId = intent.getStringExtra(getString(R.string.id)) ?: "default value"

        configureToolbar()

        //check internet connexion
        if (Internet.isInternetAvailable(this)) {
            loadAssignment()
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }

        sharedPreferences = getSharedPreferences(getString(R.string.isPro), 0)

        userType = if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) getString(R.string.users) else getString(R.string.pro_users)


        //button add date in db
        activity_assignment_detail_intervention_date_button.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                pickDateTime()
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }


        }

        //button cancel assignment
        activity_assignment_detail_cancel_assignment_button.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                cancelAssignment()
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }

        //button finish assignment
        activity_assignment_detail_finish_assignment_button.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                finishAssignmentDialog()
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }

        //button display user profil
        activity_assignment_detail_photo_imageview.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                val id = if (sharedPreferences.getBoolean(
                        getString(R.string.isPro),
                        false
                    )
                ) assignment?.userId else assignment?.proUserId
                val intent = Intent(
                    this,
                    ProfilActivity::class.java
                )
                intent.putExtra(getString(R.string.id), id)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }

        //button open google map
        activity_assignment_detail_direction_button.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                openGoogleMap()
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }

    }


    //read assignment in db
    private fun loadAssignment() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(getString(R.string.assignment)).document(intent.getStringExtra(getString(R.string.id))!!)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            assignment = documentSnapshot.toObject(Assignment::class.java)

            activity_assignment_detail_describe_text_view.text = assignment?.describe

            displayDataUser(sharedPreferences.getBoolean(getString(R.string.isPro), false))

            when (assignment?.status) {
                getString(R.string.inProgress) -> inProgressAssignment()
                getString(R.string.cancel) -> cancel()
                getString(R.string.refuse) -> refuseAssignment()
                getString(R.string.pending) -> pendingAssignment()
                getString(R.string.finish) -> finishAssignment()
            }
        }


    }

    //update view with user info in db
    private fun displayDataUser(isPro: Boolean) {
        val db = FirebaseFirestore.getInstance()

        if (isPro) {
            val docRef = db.collection(getString(R.string.users)).document(assignment?.userId.toString())
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
            val docRef = db.collection(getString(R.string.pro_users)).document(assignment?.proUserId.toString())
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
                        activity_assignment_detail_job_textview.visibility = View.VISIBLE
                        activity_assignment_detail_job_textview.text = document["job"].toString()
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

    // button for open AssignmentChoiceActivity
    private fun pendingAssignment() {
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)
        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE


        if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
            activity_assignment_detail_more_information_textview.text =
                getString(R.string.pending_assignment)
            activity_assignment_detail_accept_assignment_button.visibility = View.VISIBLE

            activity_assignment_detail_accept_assignment_button.setOnClickListener {
                val intent = Intent(
                    this,
                    AssignmentsChoiceActivity::class.java
                )
                intent.putExtra(getString(R.string.id), assignmentId)
                startActivity(intent)
            }
        } else {

            activity_assignment_detail_more_information_textview.text =
                getString(R.string.waiting_for_response)
        }

    }

    //Show button for finish or cancel assignment and add option for create new date assignment
    private fun inProgressAssignment() {
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.VISIBLE

        if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
            activity_assignment_detail_direction_button.visibility = View.VISIBLE
        }



        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)


        if (assignment?.dateAssignment == null) {
            if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                activity_assignment_detail_intervention_date_textview.visibility = View.GONE
                activity_assignment_detail_intervention_date_button.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.setBackgroundResource(R.drawable.baseline_add_circle_black_24)
            } else {
                activity_assignment_detail_intervention_date_button.visibility = View.GONE


                activity_assignment_detail_intervention_date_textview.text =
                    getString(R.string.wait_date)
            }
        } else {
            activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
            if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                activity_assignment_detail_intervention_date_button.visibility = View.VISIBLE
                activity_assignment_detail_intervention_date_button.setBackgroundResource(R.drawable.baseline_edit_black_24)

                activity_assignment_detail_intervention_date_textview.text =
                    convertDate(assignment?.dateAssignment, true)
            } else {
                activity_assignment_detail_intervention_date_button.visibility = View.GONE

                activity_assignment_detail_intervention_date_textview.text =
                    convertDate(assignment?.dateAssignment, true)
            }

        }

        activity_assignment_detail_cancel_assignment_button.visibility = View.VISIBLE

        activity_assignment_detail_finish_assignment_button.visibility = View.VISIBLE
    }

    //refuse assignment view
    private fun refuseAssignment() {
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)

        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE

        if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
            activity_assignment_detail_more_information_textview.text =
                getString(R.string.pro_refuse_assignment)
        } else {
            activity_assignment_detail_more_information_textview.text =
                getString(R.string.user_refuse_assignment)
        }

    }

    // if assignment not rated show message at user
    private fun finishAssignment() {
        activity_assignment_detail_cancel_assignment_button.visibility = View.GONE
        activity_assignment_detail_finish_assignment_button.visibility = View.GONE
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_finish_date_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE
        activity_assignment_detail_intervention_date_button.visibility = View.GONE

        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)

        activity_assignment_detail_finish_date_textview.text =
            convertDate(assignment?.dateEnd, true)


        if (assignment!!.dateAssignment == null) {
            activity_assignment_detail_intervention_date_textview.visibility = View.VISIBLE
            activity_assignment_detail_intervention_date_textview.text =
                getString(R.string.no_date_found)
        } else {
            activity_assignment_detail_intervention_date_textview.text =
                convertDate(assignment?.dateAssignment, false)
        }


        val db = FirebaseFirestore.getInstance()
        val user =
            if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                getString(R.string.pro_users)
            } else {
                getString(R.string.users)
            }
        val userId =
            if (user == getString(R.string.pro_users)) {
                assignment?.userId
            } else {
                assignment?.proUserId
            }
        val userInv =
            if (user == getString(R.string.pro_users)) {
                getString(R.string.users)
            } else {
                getString(R.string.pro_users)
            }
        db.collection(userInv).document(userId.toString()).collection(getString(R.string.rating))
            .whereEqualTo(getString(R.string.assignmentsId), assignmentId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    //not rated
                    activity_assignment_detail_more_information_textview.text =
                        getString(R.string.not_rated)
                    activity_assignment_detail_rating_button.visibility = View.VISIBLE
                    activity_assignment_detail_rating_button.setOnClickListener {

                        val intent = Intent(
                            this,
                            RatingActivity::class.java
                        )
                        intent.putExtra(getString(R.string.user), user)
                        intent.putExtra(getString(R.string.userId), userId)
                        intent.putExtra(getString(R.string.assignment), assignmentId)
                        startActivity(intent)
                    }
                } else {
                    //rated
                    activity_assignment_detail_rating_button.visibility = View.GONE
                    activity_assignment_detail_more_information_textview.text =
                        getString(R.string.assignment_finish)
                }

            }
            .addOnFailureListener { exception ->

                Log.w("rating", "Error getting documents: ", exception)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun cancel() {
        activity_assignment_detail_cancel_assignment_button.visibility = View.GONE
        activity_assignment_detail_finish_assignment_button.visibility = View.GONE
        activity_assignment_detail_created_date_textview.text =
            convertDate(assignment?.dateCreated, false)
        activity_assignment_detail_intervention_date_linearlayout.visibility = View.GONE

        activity_assignment_detail_more_information_linearlayout.visibility = View.VISIBLE

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = dateFormat.format(assignment?.dateEnd!!).toString()
        if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
            activity_assignment_detail_more_information_textview.text = "Mission annulé le $date"
        } else {
            activity_assignment_detail_more_information_textview.text = "Mission annulé le $date"
        }
    }


    //formating date
    fun convertDate(date: Date?, whithHour: Boolean): String {
        val dateFormatDay: SimpleDateFormat = if (whithHour) {
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        } else {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        }


        return dateFormatDay.format(date!!).toString()
    }

    //pick date and update in db
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
                db.collection(getString(R.string.assignments)).document(assignmentId)
                    .update(getString(R.string.dateAssignment), pickedDateTime.time)
                    .addOnSuccessListener {
                        Log.d("update date", "DocumentSnapshot successfully updated!")
                        val idReceiver = if (sharedPreferences.getBoolean(
                                getString(R.string.isPro),
                                false
                            )
                        ) assignment?.userId else assignment?.proUserId
                        Notification.createNotificationInDb(
                            userType,
                            idReceiver.toString(),
                            FirebaseAuth.getInstance().currentUser?.uid!!,
                            assignmentId,
                            getString(R.string.new_date_title_notif),
                            getString(R.string.new_date_text_notif),
                            getString(R.string.add_date_assignment)
                        )
                        loadAssignment()
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "update date",
                            "Error updating document",
                            e
                        )
                    }

            }, startHour, startMinute, DateFormat.is24HourFormat(this)).show()
        }, startYear, startMonth, startDay).show()
    }

    //request confirmation and save in db if user confirm
    private fun cancelAssignment() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.cancel_assignment))
        builder.setMessage(getString(R.string.cancel_assignment_question))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val db = FirebaseFirestore.getInstance()
            db.collection(getString(R.string.assignments)).document(assignmentId)
                .update(getString(R.string.status), getString(R.string.cancel))
                .addOnSuccessListener {
                    Log.d("update status", "DocumentSnapshot successfully updated!")
                    val idReceiver = if (sharedPreferences.getBoolean(
                            getString(R.string.isPro),
                            false
                        )
                    ) assignment?.userId else assignment?.proUserId
                    Notification.createNotificationInDb(
                        userType,
                        idReceiver.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        assignmentId,
                        getString(R.string.cancel_assignment_title_notif),
                        getString(R.string.cancel_assignment_notif_text),
                        "status update cancel"
                    )
                }
                .addOnFailureListener { e -> Log.w("update status", "Error updating document", e) }

            db.collection(getString(R.string.assignments)).document(assignmentId)
                .update(getString(R.string.date_end), FieldValue.serverTimestamp())
                .addOnSuccessListener {
                    Log.d("update date", "DocumentSnapshot successfully updated!")
                    loadAssignment()
                }
                .addOnFailureListener { e -> Log.w("update date", "Error updating document", e) }
        }

        builder.setNegativeButton(getString(R.string.no)) { _, _ ->

        }


        builder.show()
    }

    //open google map with position of assignment's user
    private fun openGoogleMap() {

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}")
        )
        startActivity(intent)


    }


    //request confirmtion finish the assignment
    private fun finishAssignmentDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.finish_assignment))
        builder.setMessage(getString(R.string.finish_missio_question))

        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            val db = FirebaseFirestore.getInstance()
            db.collection(getString(R.string.assignments)).document(assignmentId)
                .update(getString(R.string.status), getString(R.string.finish))
                .addOnSuccessListener {
                    Log.d("update status", "DocumentSnapshot successfully updated!")
                    val idReceiver = if (sharedPreferences.getBoolean(
                            getString(R.string.isPro),
                            false
                        )
                    ) assignment?.userId else assignment?.proUserId
                    Notification.createNotificationInDb(
                        userType,
                        idReceiver.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        assignmentId,
                        getString(R.string.end_assignment_title_notif),
                        getString(R.string.end_assignment_notif_text),
                        "status update finish"
                    )
                }
                .addOnFailureListener { e -> Log.w("update status", "Error updating document", e) }

            db.collection(getString(R.string.assignments)).document(assignmentId)
                .update(getString(R.string.date_end), FieldValue.serverTimestamp())
                .addOnSuccessListener {
                    Log.d("update date", "DocumentSnapshot successfully updated!")
                    loadAssignment()
                }
                .addOnFailureListener { e -> Log.w("update date", "Error updating document", e) }
        }

        builder.setNegativeButton(getString(R.string.no)) { _, _ ->

        }


        builder.show()
    }


    //configure Toolbar and button


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.assignment_detail_toolbar, menu)


        return super.onCreateOptionsMenu(menu)
    }


    private fun configureToolbar() {
        setSupportActionBar(activity_assignment_detail_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.toolbar_title_describe)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemid = item?.itemId


        if (itemid == R.id.action_open_chat) {
            when (assignment?.status) {
                getString(R.string.pending) -> {
                    Toast.makeText(
                        this,
                        getString(R.string.chat_restrict_pending),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                getString(R.string.refuse) -> {
                    Toast.makeText(
                        this,
                        getString(R.string.chat_restrict_refuse),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra(getString(R.string.assignment), assignmentId)
                    startActivity(intent)
                }
            }

        } else {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item!!)
    }


    //check internet connexion
    override fun onResume() {
        super.onResume()
        if (Internet.isInternetAvailable(this)) {
            loadAssignment()
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }

    }
}
