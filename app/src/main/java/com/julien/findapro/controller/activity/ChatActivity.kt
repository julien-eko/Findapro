package com.julien.findapro.controller.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.julien.findapro.R
import com.julien.findapro.model.Message
import com.julien.findapro.model.Notification
import com.julien.findapro.api.MessageHelper
import com.julien.findapro.view.ChatAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var uriImageSelected: Uri
    private lateinit var imageViewPreview: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userType: String
    private lateinit var assignmentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        configureToolbar()
        configureRecyclerView()
        sharedPreferences = getSharedPreferences("isPro", 0)

        userType = if (sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"
        assignmentId = intent.getStringExtra("assignment")!!

        this.imageViewPreview = activity_chat_image_chosen_preview

        //send message button
        activity_chat_send_button.setOnClickListener {
            if (!TextUtils.isEmpty(activity_chat_message_edit_text.text) && FirebaseAuth.getInstance().currentUser != null) {
                //message with image or not
                if (this.imageViewPreview.drawable == null) {
                    MessageHelper.createMessageForChat(
                        activity_chat_message_edit_text.text.toString(),
                        FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        assignmentId
                    )
                    createNotification(true, activity_chat_message_edit_text.text.toString())
                    activity_chat_message_edit_text.setText("")
                } else {
                    this.uploadPhotoInFirebaseAndSendMessage(activity_chat_message_edit_text.text.toString())
                    createNotification(false, null)
                    activity_chat_message_edit_text.setText("")
                    this.imageViewPreview.setImageDrawable(null)
                }

            }
        }

        //check if permission read image is ok
        activity_chat_add_file_button.setOnClickListener {
            checkPermition()
        }
    }


    private fun configureRecyclerView() {

        val chatAdapter = ChatAdapter(
            generateOptionsForAdapter(
                MessageHelper.getAllMessage(
                    intent.getStringExtra("assignment") ?: "default value"
                )
            ), FirebaseAuth.getInstance().currentUser?.uid!!
        )


        recycler_view_chat_activity.layoutManager = LinearLayoutManager(this)
        recycler_view_chat_activity.adapter = chatAdapter

    }

    private fun generateOptionsForAdapter(query: Query): FirestoreRecyclerOptions<Message> {
        return FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .setLifecycleOwner(this)
            .build()
    }


    private fun checkPermition() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )

            }
        } else {
            // Permission has already been granted

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RC_CHOOSE_PHOTO)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, RC_CHOOSE_PHOTO)
                    // permission was granted
                }
                return
            }


            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Calling the appropriate method after activity result
        if (data != null) {
            this.handleResponse(requestCode, resultCode, data)
        } else {
            Toast.makeText(
                this,
                "erreur",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Handle activity response (after user has chosen or not a picture)
    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) { //SUCCESS
                uriImageSelected = data.data!!

                Picasso.get().load(uriImageSelected).into(imageViewPreview)
            } else {
                Toast.makeText(
                    this,
                    "Aucune image",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadPhotoInFirebaseAndSendMessage(message: String) {
        val uuid = UUID.randomUUID().toString()

        val mImageRef = FirebaseStorage.getInstance().getReference(uuid)
        val uploadTask = mImageRef.putFile(this.uriImageSelected)
            .addOnFailureListener {

                // Handle unsuccessful uploads
            }.addOnSuccessListener {


                MessageHelper.createMessageWhithImageForChat(
                    message,
                    FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    intent.getStringExtra("assignment")!!,
                    it.toString()
                )


            }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mImageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                MessageHelper.createMessageWhithImageForChat(
                    message,
                    FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    intent.getStringExtra("assignment")!!,
                    downloadUri.toString()
                )

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_activity_toolbar, menu)


        return super.onCreateOptionsMenu(menu)
    }

    private fun configureToolbar() {
        setSupportActionBar(activity_chat_toolbar)

        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.tootlbar_title_chat_activity)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemid = item.itemId


        if (itemid == R.id.action_open_assignment) {


            val intent = Intent(this, AssignmentDetailActivity::class.java)
            intent.putExtra("id", assignmentId)
            startActivity(intent)


        } else {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    //add notification in db
    private fun createNotification(isTextMessage: Boolean, message: String?) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("assignments").document(assignmentId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userId = if (userType == "pro users") "proUserId" else "userId"
                    if (isTextMessage) {
                        Notification.createNotificationInDb(
                            userType,
                            document[userId].toString(),
                            FirebaseAuth.getInstance().currentUser?.uid!!,
                            assignmentId,
                            getString(R.string.new_message_notification_title),
                            message,
                            "new message"
                        )
                    } else {
                        Notification.createNotificationInDb(
                            userType,
                            document[userId].toString(),
                            FirebaseAuth.getInstance().currentUser?.uid!!,
                            assignmentId,
                            getString(R.string.new_image_message_notif_title),
                            getString(R.string.new_image_notif_text),
                            "new image"
                        )
                    }

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }


    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100
        private const val RC_CHOOSE_PHOTO = 200
        private const val TAG = "Chat activity"
    }
}
