package com.julien.findapro.controller.fragment


import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.utils.Internet
import com.julien.findapro.controller.activity.AssignmentsActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.UserListAdapater
import kotlinx.android.synthetic.main.fragment_users_list.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class UserListFragment : Fragment() {

    private val userList: ArrayList<HashMap<String, String>> = ArrayList()

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
            if (Internet.isInternetAvailable(context)) {
                fragment_user_list_cancel_search_button.visibility = View.GONE
                userList.clear()
                nearUserList()
            } else {
                Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activity_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val searchUserFragment = SearchUserFragment()
                val transaction = fragmentManager!!.beginTransaction()
                searchUserFragment.show(transaction, "")

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    //click on item
    private fun userItemClicked(userItem: HashMap<String, String>, isProfil: Boolean) {
        if (Internet.isInternetAvailable(context)) {
            if (isProfil) {
                //open profil
                val intent = Intent(
                    context,
                    ProfilActivity::class.java
                )
                intent.putExtra(getString(R.string.id), userItem["uid"])
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
        } else {
            Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }

    //add in list users which are within a predefined radius
    private fun nearUserList() {
        val db = FirebaseFirestore.getInstance()

        db.collection(getString(R.string.users)).document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { resullt ->
                db.collection(getString(R.string.pro_users)).limit(20).get()
                    .addOnSuccessListener { documents ->
                        val myLocation = Location("")
                        myLocation.latitude = resullt["latitude"].toString().toDouble()
                        myLocation.longitude = resullt["longitude"].toString().toDouble()

                        for (document in documents) {

                            val locationUser = Location("")
                            locationUser.latitude = document["latitude"].toString().toDouble()
                            locationUser.longitude = document["longitude"].toString().toDouble()

                            val distanceInMeters: Float = myLocation.distanceTo(locationUser)

                            if (distanceInMeters < 30000f) {
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

                        if (context != null) {
                            recycler_view_users_list_fragment.layoutManager =
                                LinearLayoutManager(context)
                            val controller = AnimationUtils.loadLayoutAnimation(
                                context,
                                R.anim.layout_animation_fall_down
                            )
                            recycler_view_users_list_fragment.layoutAnimation = controller
                            recycler_view_users_list_fragment.adapter = UserListAdapater(
                                userList,
                                context!!
                            ) { userItem: HashMap<String, String>, isProfil: Boolean ->
                                userItemClicked(
                                    userItem,
                                    isProfil
                                )
                            }
                            recycler_view_users_list_fragment.scheduleLayoutAnimation()
                        }


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

        db.collection(getString(R.string.users)).document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnSuccessListener { resullt ->
                val userRef = db.collection(getString(R.string.pro_users))
                userRef.whereEqualTo(getString(R.string.job), job).whereGreaterThan(getString(R.string.rating), minRating).get()
                    .addOnSuccessListener { documents ->
                        val myLocation = Location("")
                        myLocation.latitude = resullt["latitude"].toString().toDouble()
                        myLocation.longitude = resullt["longitude"].toString().toDouble()

                        for (document in documents) {

                            val locationUser = Location("")
                            locationUser.latitude = document["latitude"].toString().toDouble()
                            locationUser.longitude = document["longitude"].toString().toDouble()

                            val distanceInMeters: Float = myLocation.distanceTo(locationUser)

                            if (distanceInMeters < radiusInMetters * 1000) {
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
                        val controller = AnimationUtils.loadLayoutAnimation(
                            context,
                            R.anim.layout_animation_fall_down
                        )
                        recycler_view_users_list_fragment.layoutAnimation = controller
                        recycler_view_users_list_fragment.adapter = UserListAdapater(
                            userList,
                            context!!
                        ) { userItem: HashMap<String, String>, isProfil: Boolean ->
                            userItemClicked(
                                userItem,
                                isProfil
                            )
                        }
                        recycler_view_users_list_fragment.scheduleLayoutAnimation()
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
        if (Internet.isInternetAvailable(context)) {
            if (arguments != null) {
                fragment_user_list_cancel_search_button.visibility = View.VISIBLE
                userList.clear()
                val rating: Double =
                    if (arguments?.getDouble(getString(R.string.rating)) == null) 0.0 else arguments?.getDouble(getString(R.string.rating))!!
                searchUserList(
                    arguments?.getFloat(getString(R.string.maxDistance))!!,
                    arguments?.getString(getString(R.string.job))!!,
                    rating
                )
            } else {
                fragment_user_list_cancel_search_button.visibility = View.GONE
                userList.clear()
                nearUserList()
                GlobalScope.launch {
                    delay(2000)
                    if (userList.isEmpty()) {
                        activity?.runOnUiThread {
                            fragment_user_list_no_item.visibility = View.VISIBLE
                        }

                    }

                }
            }
        } else {
            Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }





        super.onResume()
    }

}
