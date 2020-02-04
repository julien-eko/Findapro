package com.julien.findapro.controller.fragment


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.julien.findapro.R
import com.julien.findapro.Utils.Internet
import com.julien.findapro.controller.activity.ChatActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.ChatListAdapter
import kotlinx.android.synthetic.main.fragment_assignments_list.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_chat_list_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class ChatListFragment : Fragment() {

    val chatList: ArrayList<HashMap<String, Any?>> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedPreferences = activity!!.getSharedPreferences("isPro", 0)


    }

    //load different chat and diplay in recycler view
    private fun loadData() {
        val db = FirebaseFirestore.getInstance()
        val user: String =
            if (sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"
        val userId: String =
            if (sharedPreferences.getBoolean("isPro", false)) "userId" else "proUserId"
        val userType: String =
            if (sharedPreferences.getBoolean("isPro", false)) "proUserId" else "userId"
        db.collection("assignments")
            .whereEqualTo(userType, FirebaseAuth.getInstance().currentUser?.uid!!)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("assignments").document(document.id).collection("chat")
                        .get()
                        .addOnSuccessListener { chatDocument ->

                            if (chatDocument.size() > 0) {
                                db.collection(user).document(document[userId].toString()).get()
                                    .addOnSuccessListener { documentUser ->


                                        if (documentUser != null) {
                                            lastMessage(
                                                document.id,
                                                documentUser["full name"].toString(),
                                                documentUser["photo"].toString(),
                                                documentUser.id
                                            )

                                        } else {
                                            Log.d("", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("", "get failed with ", exception)
                                    }
                            }


                        }
                        .addOnFailureListener { exception ->
                            Log.w("chatlist", "Error getting documents: ", exception)

                        }
                }


            }
            .addOnFailureListener { exception ->
                Log.w("chatlist", "Error getting documents: ", exception)

            }
            .addOnCompleteListener {

            }


    }

    /*
    private fun updateList() {
        val db = FirebaseFirestore.getInstance()
        val user: String =
            if (sharedPreferences.getBoolean("isPro", false)) "users" else "pro users"
        val userId: String =
            if (sharedPreferences.getBoolean("isPro", false)) "userId" else "proUserId"
        val userType: String =
            if (sharedPreferences.getBoolean("isPro", false)) "proUserId" else "userId"


        db.collection("assignments")
            .whereEqualTo(userType, FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("chat listener", "Listen failed.", e)
                    return@addSnapshotListener
                }


                for (document in value!!) {
                    db.collection("assignments").document(document.id).collection("chat")
                        .get()
                        .addOnSuccessListener { chatDocument ->

                            if (chatDocument.size() > 0) {
                                db.collection(user).document(document[userId].toString()).get()
                                    .addOnSuccessListener { documentUser ->


                                        if (documentUser != null) {
                                            lastMessage(
                                                document.id,
                                                documentUser["full name"].toString(),
                                                documentUser["photo"].toString(),
                                                documentUser.id
                                            )
                                            /*
                                            val chat = hashMapOf(
                                                "full name" to documentUser["full name"].toString(),
                                                "photo" to documentUser["photo"].toString(),
                                                "idUser" to documentUser.id,
                                                "id" to document.id
                                            )

                                            //chatList.add(chat)


                                            //recycler_view_chat_list_fragment.layoutManager = LinearLayoutManager(context)
                                            //recycler_view_chat_list_fragment.adapter = ChatListAdapter(chatList,context!!,{ chatItem : HashMap<String,String>,isProfil:Boolean -> chatItemClicked(chatItem,isProfil) })
 */
                                        } else {
                                            Log.d("", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("", "get failed with ", exception)
                                    }
                            }


                        }
                        .addOnFailureListener { exception ->
                            Log.w("chatlist", "Error getting documents: ", exception)
                        }

                }

            }

    }


     */

    //load last message send in conv
    private fun lastMessage(assignmentId: String, fullName: String, photo: String, userId: String) {
        val db = FirebaseFirestore.getInstance()

        var createdDate: Any? = Timestamp(Date())
        var lastMessage: String = ""


        db.collection("assignments").document(assignmentId).collection("chat")
            .orderBy("dateCreated", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    for (message in documents!!) {
                        createdDate = message["dateCreated"]
                        lastMessage = message["message"].toString()
                    }
                }
                val chat: HashMap<String, Any?> = hashMapOf(
                    "full name" to fullName,
                    "photo" to photo,
                    "idUser" to userId,
                    "id" to assignmentId,
                    "createdDate" to createdDate,
                    "lastMessage" to lastMessage
                )


                chatList.add(chat)


                val sortList: List<HashMap<String, Any?>> =
                    chatList.sortedWith(compareByDescending { it["createdDate"] as Comparable<*>? })


                    if(context != null){
                    recycler_view_chat_list_fragment.layoutManager = LinearLayoutManager(context)
                    val controller = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_fall_down)
                    recycler_view_chat_list_fragment.layoutAnimation = controller
                    recycler_view_chat_list_fragment.adapter = ChatListAdapter(
                        sortList,
                        context!!,
                        { chatItem: HashMap<String, Any?>, isProfil: Boolean ->
                            chatItemClicked(
                                chatItem,
                                isProfil
                            )
                        })
                        recycler_view_chat_list_fragment.scheduleLayoutAnimation()
                }



            }
            .addOnFailureListener { exception ->

                Log.w("", "Error getting documents: ", exception)
            }


    }


    //refresh recycler view with last message
    override fun onResume() {
        super.onResume()
        if (Internet.isInternetAvailable(context)) {
            chatList.clear()
            loadData()
            GlobalScope.launch {
                delay(2000)
                if (chatList.isEmpty()) {
                    activity?.runOnUiThread(java.lang.Runnable {
                        fragment_chat_list_no_item.visibility = View.VISIBLE
                    })

                }

            }
        } else {
            Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }

    private fun chatItemClicked(chatItem: HashMap<String, Any?>, isProfil: Boolean) {
        if (Internet.isInternetAvailable(context)) {
            if (isProfil) {
                val intent = Intent(
                    context,
                    ProfilActivity::class.java
                )
                intent.putExtra("id", chatItem["idUser"].toString())
                startActivity(intent)
            } else {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("assignment", chatItem["id"].toString())
                startActivity(intent)
            }
        } else {
            Toast.makeText(context, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show()
        }


    }
}
