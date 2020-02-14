package com.julien.findapro.controller.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.julien.findapro.R
import kotlinx.android.synthetic.main.activity_firebase_ui.*

class FirebaseUIActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)

        auth_google_button.setOnClickListener {
            createSignInIntent()
        }
    }


    //sign in with google
    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val db = FirebaseFirestore.getInstance()

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        val token = task.result?.token

                        //update token in db
                        db.collection("users")
                            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                            .update("token", token)
                            .addOnSuccessListener {
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot successfully updated!"
                                )
                            }
                            .addOnFailureListener {
                                db.collection("pro users")
                                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                                    .update("token", token)
                                    .addOnSuccessListener {
                                        Log.d(
                                            TAG,
                                            "DocumentSnapshot successfully updated!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            TAG,
                                            "Error updating document",
                                            e
                                        )
                                    }
                            }

                    })




                db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
                    .addOnSuccessListener { document ->

                        if (document.data != null) {
                            val sharedPref: SharedPreferences = getSharedPreferences("isPro", 0)
                            val editor = sharedPref.edit()
                            editor.putBoolean("isPro", false)
                            editor.apply()
                            val intent = Intent(
                                this,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            db.collection("pro users")
                                .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
                                .addOnSuccessListener { documentPro ->
                                    if (documentPro.data != null) {
                                        val sharedPref: SharedPreferences =
                                            getSharedPreferences("isPro", 0)
                                        val editor = sharedPref.edit()
                                        editor.putBoolean("isPro", true)
                                        editor.apply()
                                        val intent = Intent(
                                            this,
                                            MainActivity::class.java
                                        )
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(
                                            this,
                                            InformationForm::class.java
                                        )
                                        startActivity(intent)
                                    }
                                }.addOnFailureListener { exeption ->
                                Log.e("db", "get fail with", exeption)
                            }
                        }
                    }.addOnFailureListener { exeption ->
                    Log.e("db", "get fail with", exeption)
                }
            }
        }
    }


    companion object {
        private const val TAG = "FirebaseUIActivity"
        private const val RC_SIGN_IN = 123
    }
}
