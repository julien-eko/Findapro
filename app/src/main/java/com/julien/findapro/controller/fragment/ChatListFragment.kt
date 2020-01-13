package com.julien.findapro.controller.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.julien.findapro.R
import com.julien.findapro.controller.activity.ChatActivity
import com.julien.findapro.controller.activity.ProfilActivity
import com.julien.findapro.view.ChatListAdapter
import kotlinx.android.synthetic.main.fragment_chat_list.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class ChatListFragment : Fragment() {

    val chatList:ArrayList<HashMap<String,String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()


    }

    private fun loadData(){
        val db = FirebaseFirestore.getInstance()
        val user:String = if(tag!! == "proUserId") "users" else "pro users"
        val userId:String = if(tag!! == "proUserId") "userId" else "proUserId"
        db.collection("assignments")
            .whereEqualTo(tag!!, FirebaseAuth.getInstance().currentUser?.uid!!)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("assignments").document(document.id).collection("chat")
                        .get()
                        .addOnSuccessListener {chatDocument ->

                           if (chatDocument.size()>0){
                               db.collection(user).document(document[userId].toString()).get()
                                   .addOnSuccessListener { documentUser ->
                                       if (documentUser != null) {
                                           val chat = hashMapOf(
                                               "full name" to documentUser["full name"].toString(),
                                               "photo" to documentUser["photo"].toString(),
                                               "idUser" to documentUser.id,
                                               "id" to document.id
                                           )

                                           chatList.add(chat)

                                           recycler_view_chat_list_fragment.layoutManager = LinearLayoutManager(context)
                                           recycler_view_chat_list_fragment.adapter = ChatListAdapter(chatList,context!!,{ chatItem : HashMap<String,String>,isProfil:Boolean -> chatItemClicked(chatItem,isProfil) })

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


    }

    //refresh recycler view with last message
    override fun onResume() {
        super.onResume()
        recycler_view_chat_list_fragment.adapter?.notifyDataSetChanged()
    }
    private fun chatItemClicked(chatItem : HashMap<String,String>,isProfil:Boolean) {

        if (isProfil){
            val intent = Intent(context,
                ProfilActivity::class.java)
            intent.putExtra("id",chatItem["idUser"])
            startActivity(intent)
        }else{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("assignment",chatItem["id"])
            startActivity(intent)
        }

    }
}
