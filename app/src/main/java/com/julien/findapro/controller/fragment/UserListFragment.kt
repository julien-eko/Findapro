package com.julien.findapro.controller.fragment


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.Utils.Internet
import com.julien.findapro.controller.activity.AssignmentsActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.UserListAdapater
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_users_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
class UserListFragment : Fragment() {

    val userList: ArrayList<HashMap<String, String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)




        return inflater.inflate(R.layout.fragment_users_list, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragment_user_list_cancel_search_button.setOnClickListener {
            if(Internet.isInternetAvailable(context)){
                fragment_user_list_cancel_search_button.visibility = View.GONE
                userList.clear()
                nearUserList(30000f)
            }else{
                Toast.makeText(context,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activity_toolbar, menu)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                //Toast.makeText(context,"test",Toast.LENGTH_SHORT).show()
                val searchUserFragment = SearchUserFragment()
                val transaction = fragmentManager!!.beginTransaction()
                searchUserFragment.show(transaction, "")

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*
    private fun loadData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { resullt ->
                db.collection("pro users").whereEqualTo("postal code", resullt["postal code"]).get()
                    .addOnSuccessListener { documents ->

                        for (document in documents) {

                            val user = hashMapOf(
                                "full name" to document["full name"].toString(),
                                "job" to document["job"].toString(),
                                "photo" to document["photo"].toString(),
                                "city" to document["city"].toString(),
                                "rating" to document["rating"].toString(),
                                "uid" to document.id
                            )

                            userList.add(user)
                        }
                        recycler_view_users_list_fragment.layoutManager =
                            LinearLayoutManager(context)
                        recycler_view_users_list_fragment.adapter = UserListAdapater(
                            userList,
                            context!!,
                            { userItem: HashMap<String, String>, isProfil: Boolean ->
                                userItemClicked(
                                    userItem,
                                    isProfil
                                )
                            })
                    }
                    .addOnFailureListener { exception ->
                        Log.w("access db", "Error getting data", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }

    }


     */
    //click on item
    private fun userItemClicked(userItem: HashMap<String, String>, isProfil: Boolean) {
        if(Internet.isInternetAvailable(context)){
            if (isProfil) {
                //open profil
                val intent = Intent(
                    context,
                    ProfilActivity::class.java
                )
                intent.putExtra("id", userItem["uid"])
                startActivity(intent)
            } else {
                //open assignment activity
                val intent = Intent(
                    context,
                    AssignmentsActivity::class.java
                )
                intent.putExtra("proId", userItem["uid"])
                startActivity(intent)
            }
        }else{
            Toast.makeText(context,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
        }


    }

    //add in list users which are within a predefined radius
    private fun nearUserList(radiusInMetters: Float) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { resullt ->
                db.collection("pro users").limit(20).get()
                    .addOnSuccessListener { documents ->
                        var myLocation = Location("")
                        myLocation.latitude = resullt["latitude"].toString().toDouble()
                        myLocation.longitude = resullt["longitude"].toString().toDouble()

                        for (document in documents) {

                            var locationUser = Location("")
                            locationUser.latitude = document["latitude"].toString().toDouble()
                            locationUser.longitude = document["longitude"].toString().toDouble()

                            val distanceInMeters: Float = myLocation.distanceTo(locationUser)

                            if (distanceInMeters < radiusInMetters) {
                                val user = hashMapOf(
                                    "full name" to document["full name"].toString(),
                                    "job" to document["job"].toString(),
                                    "photo" to document["photo"].toString(),
                                    "city" to document["city"].toString(),
                                    "rating" to document["rating"].toString(),
                                    "uid" to document.id
                                )

                                userList.add(user)
                            }

                        }


                        recycler_view_users_list_fragment.layoutManager =
                            LinearLayoutManager(context)
                        recycler_view_users_list_fragment.adapter = UserListAdapater(
                            userList,
                            context!!,
                            { userItem: HashMap<String, String>, isProfil: Boolean ->
                                userItemClicked(
                                    userItem,
                                    isProfil
                                )
                            })
                    }
                    .addOnFailureListener { exception ->
                        Log.w("access db", "Error getting data", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }

    }

    //search in db user with job and minimum rating
    private fun searchUserList(radiusInMetters: Float, job: String, minRating: Double) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { resullt ->
                val userRef = db.collection("pro users")
                userRef.whereEqualTo("job",job).whereGreaterThan("rating", minRating).get()
                    .addOnSuccessListener { documents ->
                        var myLocation = Location("")
                        myLocation.latitude = resullt["latitude"].toString().toDouble()
                        myLocation.longitude = resullt["longitude"].toString().toDouble()

                        for (document in documents) {

                            var locationUser = Location("")
                            locationUser.latitude = document["latitude"].toString().toDouble()
                            locationUser.longitude = document["longitude"].toString().toDouble()

                            val distanceInMeters: Float = myLocation.distanceTo(locationUser)

                            if (distanceInMeters < radiusInMetters* 1000) {
                                val user = hashMapOf(
                                    "full name" to document["full name"].toString(),
                                    "job" to document["job"].toString(),
                                    "photo" to document["photo"].toString(),
                                    "city" to document["city"].toString(),
                                    "rating" to document["rating"].toString(),
                                    "uid" to document.id
                                )

                                userList.add(user)
                            }

                        }


                        recycler_view_users_list_fragment.layoutManager =
                            LinearLayoutManager(context)
                        recycler_view_users_list_fragment.adapter = UserListAdapater(
                            userList,
                            context!!,
                            { userItem: HashMap<String, String>, isProfil: Boolean ->
                                userItemClicked(
                                    userItem,
                                    isProfil
                                )
                            })
                    }
                    .addOnFailureListener { exception ->
                        Log.w("access db", "Error getting data", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.w("access db", "Error getting data", exception)
            }

    }


    override fun onResume() {
        if(Internet.isInternetAvailable(context)){
            if (arguments != null) {
                fragment_user_list_cancel_search_button.visibility = View.VISIBLE
                userList.clear()
                val rating: Double =
                    if (arguments?.getDouble("rating") == null) 0.0 else arguments?.getDouble("rating")!!
                searchUserList(
                    arguments?.getFloat("maxDistance")!!,
                    arguments?.getString("job")!!,
                    rating
                )
            } else {
                fragment_user_list_cancel_search_button.visibility = View.GONE
                userList.clear()
                nearUserList(30000f)
                GlobalScope.launch {
                    delay(2000)
                    if (userList.isEmpty()){
                        activity?.runOnUiThread(java.lang.Runnable {
                            fragment_user_list_no_item.visibility = View.VISIBLE
                        })

                    }

                }
            }
        }else{
            Toast.makeText(context,getString(R.string.no_connexion),Toast.LENGTH_SHORT).show()
        }





        super.onResume()
    }

}
