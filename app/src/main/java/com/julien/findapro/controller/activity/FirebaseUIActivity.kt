package com.julien.findapro.controller.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import kotlinx.android.synthetic.main.activity_firebase_ui.*

class FirebaseUIActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)

        auth_google_button.setOnClickListener{
            createSignInIntent()
        }
    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .build(),
            RC_SIGN_IN
        )
        // [END auth_fui_create_intent]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                //val user = FirebaseAuth.getInstance().currentUser
                //val intent = Intent(this,MainActivity::class.java)
                //startActivity(intent)

                val db = FirebaseFirestore.getInstance()

                db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->

                    if (document.data != null){
                        val sharedPref: SharedPreferences = getSharedPreferences("isPro", 0)
                        val editor = sharedPref.edit()
                        editor.putBoolean("isPro",false)
                        editor.apply()
                        val intent = Intent(this,
                            MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->
                            if (document.data != null){
                                val sharedPref: SharedPreferences = getSharedPreferences("isPro", 0)
                                val editor = sharedPref.edit()
                                editor.putBoolean("isPro",true)
                                editor.apply()
                                val intent = Intent(this,
                                    MainActivity::class.java)
                                startActivity(intent)
                            }else{
                                val intent = Intent(this,
                                    InformationForm::class.java)
                                startActivity(intent)
                            }
                        }.addOnFailureListener{exeption ->
                            Log.e("db","get fail with",exeption)
                        }
                    }
                }.addOnFailureListener{exeption ->
                    Log.e("db","get fail with",exeption)
                }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_signout]
    }

    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_delete]
    }


    companion object {

        private const val RC_SIGN_IN = 123
    }
}
