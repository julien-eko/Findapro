package com.julien.findapro.controller.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.utils.Internet
import com.julien.findapro.model.Message
import com.julien.findapro.model.Notification
import kotlinx.android.synthetic.main.activity_assignments_choice.*

class AssignmentsChoiceActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignments_choice)

        configureToolbar()

        val db = FirebaseFirestore.getInstance()

        //check internet connexion
        if (Internet.isInternetAvailable(this)) {
            loadData(db)
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


        //accept button, update statut in db and create new notification
        assignments_choice_activity_accept_button.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                db.collection(getString(R.string.assignments)).document(intent.getStringExtra(getString(R.string.id))!!)
                    .update(getString(R.string.status), getString(R.string.inProgress))
                    .addOnSuccessListener {

                        db.collection(getString(R.string.assignments)).document(intent.getStringExtra(getString(R.string.id))!!).get()
                            .addOnSuccessListener { document ->
                                if (document.data != null) {
                                    Notification.createNotificationInDb(
                                        getString(R.string.users),
                                        document[getString(R.string.userId)].toString(),
                                        FirebaseAuth.getInstance().currentUser?.uid!!,
                                        intent.getStringExtra(getString(R.string.id))!!,
                                        getString(R.string.accept_assignment_notification_title),
                                        getString(R.string.acept_assignment_text_notification),
                                        "status update in progress"
                                    )
                                }

                            }.addOnFailureListener { exeption ->
                                Log.e("db", "get fail with", exeption)
                            }


                        Log.d(
                            "update status",
                            "DocumentSnapshot successfully updated!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w("update status", "Error updating document", e)
                    }

                val message = Message(
                    getString(R.string.first_message_in_chat),
                    null,
                    "bot",
                    null,
                    null
                )
                db.collection(getString(R.string.assignments)).document(intent.getStringExtra(getString(R.string.id))!!)
                    .collection(getString(R.string.chat)).add(message)

                finish()
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }

        assignments_choice_activity_decline_button.setOnClickListener {
            if (Internet.isInternetAvailable(this)) {
                db.collection(getString(R.string.assignments)).document(intent.getStringExtra(getString(R.string.id))!!)
                    .update(getString(R.string.status), getString(R.string.refuse))
                    .addOnSuccessListener {
                        Log.d(
                            "update status",
                            "DocumentSnapshot successfully updated!"
                        )

                        db.collection(getString(R.string.assignments)).document(intent.getStringExtra(getString(R.string.id))!!).get()
                            .addOnSuccessListener { document ->
                                if (document.data != null) {
                                    Notification.createNotificationInDb(
                                        getString(R.string.users),
                                        document[getString(R.string.userId)].toString(),
                                        FirebaseAuth.getInstance().currentUser?.uid!!,
                                        intent.getStringExtra(getString(R.string.id))!!,
                                        getString(R.string.assignment_refuse_notification_title),
                                        getString(R.string.assignment_refuse_text_notification),
                                        "status update refuse"
                                    )
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("update status", "Error updating document", e)
                            }

                        finish()
                    }
            } else {
                Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }


        }
    }

    //load date and update view
    private fun loadData(db: FirebaseFirestore) {
        db.collection(getString(R.string.assignments)).document(intent.getStringExtra(getString(R.string.id))!!).get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    assignments_choice_activity_describe_textview.text =
                        document["describe"].toString()
                    db.collection(getString(R.string.users)).document(document[getString(R.string.userId)].toString()).get()
                        .addOnSuccessListener { document2 ->
                            if (document2.data != null) {
                                activity_assignements_choice_full_name_text_view.text =
                                    document2["full name"].toString()
                                activity_assignements_choice_adress_text_view.text =
                                    document2["adress"].toString()
                                activity_assignements_choice_city_text_view.text =
                                    document2["city"].toString()

                                if (document2[getString(R.string.rating)] != null) {
                                    val nbRating: String =
                                        "(" + document2[getString(R.string.ratingNb)].toString() + ") : "
                                    activity_assignements_choice_nb_rating_text_view.text = nbRating
                                    activity_assignment_choice_ratingbar.rating =
                                        document2[getString(R.string.rating)].toString().toFloat()
                                } else {
                                    activity_assignment_choice_ratingbar_linearlayout.visibility =
                                        View.GONE
                                }

                            } else {
                                Log.e("db", "no document")
                            }
                        }.addOnFailureListener { exeption ->
                            Log.e("db", "get fail with", exeption)
                        }
                } else {
                    Log.e("db", "no document")
                }
            }.addOnFailureListener { exeption ->
                Log.e("db", "get fail with", exeption)
            }
    }

    private fun configureToolbar() {
        setSupportActionBar(activity_assignment_choice_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.toolbar_title_assignment_choice)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
