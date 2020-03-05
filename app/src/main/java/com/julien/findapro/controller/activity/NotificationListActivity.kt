package com.julien.findapro.controller.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julien.findapro.R
import com.julien.findapro.utils.Internet
import com.julien.findapro.model.Notification
import com.julien.findapro.view.NotificationListAdapter
import kotlinx.android.synthetic.main.activity_notification_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationListActivity : AppCompatActivity() {

    private lateinit var userType: String
    private lateinit var sharedPreferences: SharedPreferences
    private var notificationList: ArrayList<Notification> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)
        configureToolbar()

        sharedPreferences = getSharedPreferences(getString(R.string.isPro), 0)
        userType = if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) getString(R.string.pro_users) else getString(R.string.users)

        if (Internet.isInternetAvailable(this)) {
            loadRecyclerView()

            GlobalScope.launch {
                delay(2000)
                if (notificationList.isEmpty()) {
                    this@NotificationListActivity.runOnUiThread {
                        activity_notification_list_no_item.visibility = View.VISIBLE
                    }

                }
            }
        } else {
            Toast.makeText(this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }

    //read db and add in list every notification's user
    private fun loadRecyclerView() {
        val db = FirebaseFirestore.getInstance()
        db.collection(userType).document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .collection(getString(R.string.notificationC)).orderBy(getString(R.string.dateCreated), Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val notification = document.toObject(Notification::class.java)
                    notificationList.add(notification)
                }

                recycler_view_notification_list_activity.layoutManager = LinearLayoutManager(this)
                val controller =
                    AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
                recycler_view_notification_list_activity.layoutAnimation = controller
                recycler_view_notification_list_activity.adapter =
                    NotificationListAdapter(
                        notificationList,
                        { notificationItem: Notification, isProfil: Boolean ->
                            notificationItemClicked(
                                notificationItem,
                                isProfil
                            )
                        }, userType
                    )
                recycler_view_notification_list_activity.scheduleLayoutAnimation()
            }

            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }


    private fun configureToolbar() {
        setSupportActionBar(activity_notification_list_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.Toolbar_title_notification_activity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }


    //click on notification
    private fun notificationItemClicked(notificationItem: Notification, isProfil: Boolean) {


        if (isProfil) {
            val intent = Intent(this, ProfilActivity::class.java)
            intent.putExtra(getString(R.string.id), notificationItem.otherUserId)
            startActivity(intent)
        } else {
            when (notificationItem.cause) {
                "new message" -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra(getString(R.string.assignment), notificationItem.assignmentId)
                    startActivity(intent)
                }
                "new assignment created" -> {
                    val intent = Intent(
                        this,
                        AssignmentsChoiceActivity::class.java
                    )
                    intent.putExtra(getString(R.string.id), notificationItem.assignmentId)
                    startActivity(intent)
                }
                else -> {
                    val intent = Intent(this, AssignmentDetailActivity::class.java)
                    intent.putExtra(getString(R.string.id), notificationItem.assignmentId)
                    startActivity(intent)
                }
            }
        }


    }

    companion object {
        private const val TAG = "NotificationList"
    }
}
