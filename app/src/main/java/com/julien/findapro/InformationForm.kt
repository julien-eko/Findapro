package com.julien.findapro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
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

        information_form_save_button.setOnClickListener{
            if(validateForm()){
                addInDatabase()
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
                "nom complet" to information_form_full_name.text.toString(),
                "adresse" to information_form_adress.text.toString(),
                "code postal" to information_postal_code.text.toString(),
                "ville" to information_form_city.text.toString(),
                "num" to information_form_phone_number.text.toString(),
                "profession" to spinner_job.selectedItem.toString()

            )

            db.collection("Utilisateurs").document("Professionnel").collection("Utilisateurs").document(
                FirebaseAuth.getInstance().currentUser?.uid!!
            )
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("addDB", "DocumentSnapshot added ")
                }
                .addOnFailureListener { e ->
                    Log.w("addDB", "Error adding document", e)
                }
        }else{
            val user = hashMapOf(
                "nom complet" to information_form_full_name.text.toString(),
                "adresse" to information_form_adress.text.toString(),
                "code postal" to information_postal_code.text.toString(),
                "ville" to information_form_city.text.toString(),
                "num" to information_form_phone_number.text.toString()

            )

            db.collection("Utilisateurs").document("Particulier").collection("Utilisateurs").document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("addDB", "DocumentSnapshot added ")
                }
                .addOnFailureListener { e ->
                    Log.w("addDB", "Error adding document", e)
                }
        }

        finish()


    }
}
