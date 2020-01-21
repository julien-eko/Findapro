package com.julien.findapro.controller.activity

import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.julien.findapro.R
import kotlinx.android.synthetic.main.activity_information_form.*

class InformationForm : AppCompatActivity() {

    private var latitude = 0.0
    private var longitude = 0.0
    private var token:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_form)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("information form", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
               this.token = token
            })


        spinner_status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


                if (position == 1) {
                    information_form_linear_layout_pro.visibility = View.VISIBLE

                } else {
                    information_form_linear_layout_pro.visibility = View.GONE
                }
            }

        }

        if (intent.getBooleanExtra("edit",false)){
            information_form_linear_layout_statut.visibility = View.GONE
            loadDatabase()

            information_form_save_button.setOnClickListener{
                if(validateForm()){
                    val fullAdress:String = information_form_adress.text.toString() + " " +
                            information_form_city.text.toString() + " "  +
                            information_postal_code.text.toString() + " " +
                            information_form_country.toString()

                    if (isGoodAdress(fullAdress)){
                        editDatabase()
                    }else{
                        alertDialogWrongAdress()
                    }
                }
            }
        }else{
            information_form_save_button.setOnClickListener{
                if(validateForm()){
                    val fullAdress:String = information_form_adress.text.toString() + " " +
                            information_form_city.text.toString() + " "  +
                            information_postal_code.text.toString() + " " +
                            information_form_country.toString()

                    if (isGoodAdress(fullAdress)){
                        addInDatabase()
                    }else{
                        alertDialogWrongAdress()
                    }

                }
            }
        }




    }

    private fun validateForm():Boolean{
        if(information_form_adress.text.toString().trim() != "" &&
                information_form_city.text.toString().trim() != "" &&
                    information_form_full_name.text.toString().trim() != "" &&
                        information_form_phone_number.text.toString().trim() != "" &&
                            information_postal_code.text.toString().trim() != "" &&
                                information_form_country.text.toString().trim() != ""
        ){
            return true
        }else{
            if(information_form_adress.text.toString().trim() == ""){
                information_form_adress.error= getString(R.string.field_cannot_be_blank)
            }
            if(information_form_city.text.toString().trim() == ""){
                information_form_city.error= getString(R.string.field_cannot_be_blank)
            }
            if(information_form_full_name.text.toString().trim() == ""){
                information_form_full_name.error= getString(R.string.field_cannot_be_blank)
            }
            if(information_postal_code.text.toString().trim() == ""){
                information_postal_code.error= getString(R.string.field_cannot_be_blank)
            }
            if(information_form_phone_number.text.toString().trim() == ""){
                information_form_phone_number.error= getString(R.string.field_cannot_be_blank)
            }
            if(information_form_country.text.toString().trim() == ""){
                information_form_country.error = getString(R.string.field_cannot_be_blank)
            }
            return false
        }
    }


    private fun addInDatabase(){
        val db = FirebaseFirestore.getInstance()
        val rating:Float? = null
        val ratingNb: Int? = null

        if(spinner_status.selectedItemPosition == 1){

            val sharedPref: SharedPreferences = getSharedPreferences("isPro", 0)
            val editor = sharedPref.edit()
            editor.putBoolean("isPro",true)
            editor.apply()



            val user = hashMapOf(
                "full name" to information_form_full_name.text.toString(),
                "adress" to information_form_adress.text.toString(),
                "postal code" to information_postal_code.text.toString(),
                "city" to information_form_city.text.toString(),
                "num" to information_form_phone_number.text.toString(),
                "country" to information_form_country.text.toString(),
                "job" to spinner_job.selectedItem.toString(),
                "latitude" to latitude,
                "longitude" to longitude,
                "rating" to rating,
                "ratingNb" to ratingNb,
                "photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                "token" to token

            )

            db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("addDB", "DocumentSnapshot added ")
                }
                .addOnFailureListener { e ->
                    Log.w("addDB", "Error adding document", e)
                }
        }else{

            val sharedPref: SharedPreferences = getSharedPreferences("isPro", 0)
            val editor = sharedPref.edit()
            editor.putBoolean("isPro",true)
            editor.apply()

            val user = hashMapOf(
                "full name" to information_form_full_name.text.toString(),
                "adress" to information_form_adress.text.toString(),
                "postal code" to information_postal_code.text.toString(),
                "city" to information_form_city.text.toString(),
                "country" to information_form_country.text.toString(),
                "num" to information_form_phone_number.text.toString(),
                "latitude" to latitude,
                "longitude" to longitude,
                "rating" to rating,
                "ratingNb" to ratingNb,
                "photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                "token" to token

            )

            db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("addDB", "DocumentSnapshot added ")
                }
                .addOnFailureListener { e ->
                    Log.w("addDB", "Error adding document", e)
                }
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)


    }

    private fun loadDatabase(){
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->
           if (document.data != null){
               information_form_full_name.setText(document["full name"].toString())
               information_postal_code.setText(document["postal code"].toString())
               information_form_phone_number.setText(document["num"].toString())
               information_form_city.setText(document["city"].toString())
               information_form_country.setText(document["country"].toString())
               information_form_adress.setText(document["adress"].toString())
           }else{
               db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->
                   if (document.data != null){
                       information_form_full_name.setText(document["full name"].toString())
                       information_postal_code.setText(document["postal code"].toString())
                       information_form_phone_number.setText(document["num"].toString())
                       information_form_city.setText(document["city"].toString())
                       information_form_country.setText(document["country"].toString())
                       information_form_adress.setText(document["adress"].toString())
                   }else{
                       Log.e("db", "no document")
                   }
               }.addOnFailureListener{exeption ->
                   Log.e("db","get fail with",exeption)
               }
           }
        }.addOnFailureListener{exeption ->
            Log.e("db","get fail with",exeption)
        }



    }

    private fun editDatabase(){
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->
            if (document.data != null){
                val user = hashMapOf(
                    "full name" to information_form_full_name.text.toString(),
                    "adress" to information_form_adress.text.toString(),
                    "postal code" to information_postal_code.text.toString(),
                    "city" to information_form_city.text.toString(),
                    "country" to information_form_country.text.toString(),
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "num" to information_form_phone_number.text.toString(),
                    "rating" to document["rating"],
                    "ratingNb" to document["ratingNb"],
                    "photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                    "token" to token


                )

                db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .set(user)
                    .addOnSuccessListener { documentReference ->
                        finish()
                        Log.d("addDB", "DocumentSnapshot added ")
                    }
                    .addOnFailureListener { e ->
                        Log.w("addDB", "Error adding document", e)
                    }
            }else{
                db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->
                    if (document.data != null){
                        val user = hashMapOf(
                            "full name" to information_form_full_name.text.toString(),
                            "adress" to information_form_adress.text.toString(),
                            "postal code" to information_postal_code.text.toString(),
                            "city" to information_form_city.text.toString(),
                            "num" to information_form_phone_number.text.toString(),
                            "country" to information_form_country.text.toString(),
                            "latitude" to latitude,
                            "longitude" to longitude,
                            "rating" to document["rating"],
                            "ratingNb" to document["ratingNb"],
                            "photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
                            "job" to spinner_job.selectedItem.toString(),
                            "token" to token

                        )

                        db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                            .set(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d("addDB", "DocumentSnapshot added ")
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w("addDB", "Error adding document", e)
                            }

                    }else{
                        Log.e("db", "no document")
                    }
                }.addOnFailureListener{exeption ->
                    Log.e("db","get fail with",exeption)
                }
            }
        }.addOnFailureListener{exeption ->
            Log.e("db","get fail with",exeption)
        }

    }

    private fun isGoodAdress(fullAdress:String):Boolean{

        var geocoder = Geocoder(this)
        var listAdress: List<Address> = geocoder.getFromLocationName(fullAdress, 1)

        if (listAdress.size > 0) {
            latitude = listAdress[0].latitude
            longitude = listAdress[0].longitude
            return true

            //activity?.findViewById<Stepper>(R.id.Stepper)?.forward()

        } else {
            latitude = 0.0
            longitude = 0.0
            return false

        }
    }


    fun alertDialogWrongAdress() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("L'adresse n'est pas valide")

        builder.setMessage("Vous devez entrer une adresse valide")

        builder.setPositiveButton("OK") { dialog, which ->

        }



        val dialog: AlertDialog = builder.create()

        dialog.show()
    }
}
