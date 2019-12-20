package com.julien.findapro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_information_form.*

class InformationForm : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information_form)

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
                    editDatabase()
                }
            }
        }else{
            information_form_save_button.setOnClickListener{
                if(validateForm()){
                    addInDatabase()
                }
            }
        }




    }

    private fun validateForm():Boolean{
        if(information_form_adress.text.toString().trim() != "" &&
                information_form_city.text.toString().trim() != "" &&
                    information_form_full_name.text.toString().trim() != "" &&
                        information_form_phone_number.text.toString().trim() != "" &&
                            information_postal_code.text.toString().trim() != ""
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
            return false
        }
    }


    private fun addInDatabase(){
        val db = FirebaseFirestore.getInstance()


        if(spinner_status.selectedItemPosition == 1){


            val user = hashMapOf(
                "full name" to information_form_full_name.text.toString(),
                "adress" to information_form_adress.text.toString(),
                "postal code" to information_postal_code.text.toString(),
                "city" to information_form_city.text.toString(),
                "num" to information_form_phone_number.text.toString(),
                "job" to spinner_job.selectedItem.toString(),
                "photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString()

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
            val user = hashMapOf(
                "full name" to information_form_full_name.text.toString(),
                "adress" to information_form_adress.text.toString(),
                "postal code" to information_postal_code.text.toString(),
                "city" to information_form_city.text.toString(),
                "num" to information_form_phone_number.text.toString(),
                "photo" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString()

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

        val intent = Intent(this,MainActivity::class.java)
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
               information_form_adress.setText(document["adress"].toString())
           }else{
               db.collection("pro users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { document ->
                   if (document.data != null){
                       information_form_full_name.setText(document["full name"].toString())
                       information_postal_code.setText(document["postal code"].toString())
                       information_form_phone_number.setText(document["num"].toString())
                       information_form_city.setText(document["city"].toString())
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
                    "num" to information_form_phone_number.text.toString()

                )

                db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .set(user)
                    .addOnSuccessListener { documentReference ->
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
                            "job" to spinner_job.selectedItem.toString()

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
                        Log.e("db", "no document")
                    }
                }.addOnFailureListener{exeption ->
                    Log.e("db","get fail with",exeption)
                }
            }
        }.addOnFailureListener{exeption ->
            Log.e("db","get fail with",exeption)
        }

        finish()
    }
}
