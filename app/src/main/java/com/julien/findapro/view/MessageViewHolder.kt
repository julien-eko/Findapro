package com.julien.findapro.view



import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.julien.findapro.R
import com.julien.findapro.Utils.CircleTransform
import com.julien.findapro.Utils.Message
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    //Root view
    val rootView = itemView.activity_chat_item_root_view

    //profile container
    val profileContainer = itemView.activity_chat_item_profile_container
    val imageViewProfile = itemView.activity_chat_item_profile_container_profile_image

    //Message container

    val messageContainer = itemView.activity_chat_item_message_container

    //Image sender container
    val cardViewImageSent = itemView.activity_chat_item_message_container_image_sent_cardview
    val imageSent = itemView.activity_chat_item_message_container_image_sent_cardview_image

    //text message container
    val textMessageContainer = itemView.activity_chat_item_message_container_text_message_container
    val textViewMessage = itemView.activity_chat_item_message_container_text_message_container_text_view

    //Dare text
    val textViewDate = itemView.activity_chat_item_message_container_text_view_date

    val colorCurrentUser =ContextCompat.getColor(itemView.context, R.color.colorChat1)
    val colorRemoteUser = ContextCompat.getColor(itemView.context, R.color.colorChat2)



    fun updateWithMessage(message: Message, currentUserId:String){

        // Check if current user is the sender
        val isCurrentUser:Boolean = message.userSender.equals(currentUserId)

        //Update message text view
        this.textViewMessage.text = message.message
        this.textViewMessage.textAlignment = if (isCurrentUser) View.TEXT_ALIGNMENT_TEXT_END else View.TEXT_ALIGNMENT_TEXT_START



        //update date text view
        if(message.dateCreated != null){
            this.textViewDate.text = this.convertDateToHour(message.dateCreated!!)
        }

        //update profile picture image view

        if(message.urlImageSender != null){
            Picasso.get().load(message.urlImageSender).transform(CircleTransform()).into(imageViewProfile)
        }

        //update image sent texview (not implemented atm
        this.imageSent.visibility=View.GONE

        //Update Message Bubble Color Background
        //Update Message Bubble Color Background
        (textMessageContainer.background as GradientDrawable).setColor(if (isCurrentUser) colorCurrentUser else colorRemoteUser)

        //update all views alignments depending is current user or not

        this.updateDesignDependingUser(isCurrentUser)
    }

    private  fun updateDesignDependingUser(isSender: Boolean){
        // PROFILE CONTAINER
        // PROFILE CONTAINER
        val paramsLayoutHeader = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLayoutHeader.addRule(
            if (isSender) RelativeLayout.ALIGN_PARENT_RIGHT else RelativeLayout.ALIGN_PARENT_LEFT)
        this.profileContainer.layoutParams = paramsLayoutHeader


        // MESSAGE CONTAINER
        // MESSAGE CONTAINER
        val paramsLayoutContent = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLayoutContent.addRule(
            if (isSender) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF,
            R.id.activity_chat_item_profile_container)
        this.messageContainer.layoutParams = paramsLayoutContent

        // CARDVIEW IMAGE SEND
        // CARDVIEW IMAGE SEND
        val paramsImageView = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        paramsImageView.addRule(
            if (isSender) RelativeLayout.ALIGN_LEFT else RelativeLayout.ALIGN_RIGHT,
            R.id.activity_chat_item_message_container_text_message_container
        )
        this.cardViewImageSent.layoutParams = paramsImageView

        this.rootView.requestLayout()
    }

    private fun convertDateToHour(date: Date): String? {
        val dfTime: DateFormat = SimpleDateFormat("HH:mm")
        return dfTime.format(date)
    }


}