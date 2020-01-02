package com.julien.findapro.controller.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.julien.findapro.R
import com.julien.findapro.Utils.CircleTransform
import com.julien.findapro.Utils.Message
import com.julien.findapro.api.MessageHelper
import com.julien.findapro.view.ChatAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat_item.*
import kotlinx.android.synthetic.main.activity_chat_item.view.*
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var uriImageSelected: Uri
    private lateinit var  imageViewPreview:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        configureRecyclerView()

        this.imageViewPreview =  activity_chat_image_chosen_preview

        activity_chat_send_button.setOnClickListener {
            if(!TextUtils.isEmpty(activity_chat_message_edit_text.text) && FirebaseAuth.getInstance().currentUser != null){
                if(this.imageViewPreview.drawable == null){
                    MessageHelper.createMessageForChat(activity_chat_message_edit_text.text.toString(),FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),FirebaseAuth.getInstance().currentUser?.uid!!,intent.getStringExtra("assignment"))
                        .addOnFailureListener{exeption ->
                            Log.e("add message in chat","get fail with",exeption)
                        }
                    activity_chat_message_edit_text.setText("")
                }else{
                    this.uploadPhotoInFirebaseAndSendMessage(activity_chat_message_edit_text.text.toString())
                    activity_chat_message_edit_text.setText("")
                    this.imageViewPreview.setImageDrawable(null)
                }

            }
        }

        activity_chat_add_file_button.setOnClickListener{
            checkPermition()
        }
    }


    private fun configureRecyclerView(){

        val chatAdapter = ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessage(intent.getStringExtra("assignment"))),FirebaseAuth.getInstance().currentUser?.uid!!)


        recycler_view_chat_activity.layoutManager = LinearLayoutManager(this)
        recycler_view_chat_activity.adapter = chatAdapter

    }

    private fun generateOptionsForAdapter(query: Query):FirestoreRecyclerOptions<Message>{
        return FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query,Message::class.java)
            .setLifecycleOwner(this)
            .build()
    }


    fun checkPermition() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RC_CHOOSE_PHOTO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Companion.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, RC_CHOOSE_PHOTO)
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 6 - Calling the appropriate method after activity result
        if (data != null) {
            this.handleResponse(requestCode, resultCode, data)
        }else{
            Toast.makeText(
                this,
                "erreur",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // 4 - Handle activity response (after user has chosen or not a picture)
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

    private fun uploadPhotoInFirebaseAndSendMessage(message:String){
        val uuid =UUID.randomUUID().toString()

        val mImageRef = FirebaseStorage.getInstance().getReference(uuid)
        val uploadTask =mImageRef.putFile(this.uriImageSelected)
            .addOnFailureListener {

                // Handle unsuccessful uploads
            }.addOnSuccessListener {


                MessageHelper.createMessageWhithImageForChat(message,FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),FirebaseAuth.getInstance().currentUser?.uid!!,intent.getStringExtra("assignment"),it.toString())
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

            }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mImageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                MessageHelper.createMessageWhithImageForChat(message,FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),FirebaseAuth.getInstance().currentUser?.uid!!,intent.getStringExtra("assignment"),downloadUri.toString())

            } else {
                // Handle failures
                // ...
            }
        }
    }



    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100
        private const val RC_CHOOSE_PHOTO = 200
    }
}
