package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.models.textMessage
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

internal enum class VIEW_TYPE {
    MESSAGE_VIEW, REQUEST_VIEW
}

class MessagesAdapter(val messages: List<textMessage>) :
    RecyclerView.Adapter<MessagesAdapter.ItemViewHolder>() {
    val dateFormat =
        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view = v
    }

    //inflate different layout for request and text message in chat
    override fun getItemViewType(position: Int): Int {
        val item = messages[position]
        return if (item.request) {
            VIEW_TYPE.REQUEST_VIEW.ordinal
        } else VIEW_TYPE.MESSAGE_VIEW.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        if (viewType == VIEW_TYPE.MESSAGE_VIEW.ordinal) {
            val vl = LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false);
            return MessagesAdapter.ItemViewHolder(vl);
        } else {
            val vl = LayoutInflater.from(parent.context).inflate(R.layout.request_layout, parent, false);
            return MessagesAdapter.ItemViewHolder(vl);
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = messages[position]

        if (!item.request) {
            //The message is not a request !!!
            // variables for interacting with the message layout
            val incomeLayout: LinearLayout = holder.view.findViewById(R.id.income_message)
            val sentLayout: LinearLayout = holder.view.findViewById(R.id.sent_message)
            //val leftProfileImage: ImageView = v.findViewById(R.id.income_profile_image)
            //val rightProfileImage: ImageView = v.findViewById(R.id.sender_profile_image)
            val timeRight: TextView = holder.view.findViewById(R.id.time_right)
            val timeLeft: TextView = holder.view.findViewById(R.id.time_left)
            val incomeText: TextView = holder.view.findViewById(R.id.income_text)
            val sentText: TextView = holder.view.findViewById(R.id.sent_text)

            if (item.senderId == Firebase.auth.currentUser?.uid) {
                //it's a message sent from the current user
                incomeLayout.visibility = View.GONE
                sentText.text = item.text
                timeRight.text = dateFormat.format(item.time)
                //TODO update image profile or remove it if it's too complex manage images
                /*val profileImgRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + Firebase.auth.currentUser?.uid)
            Glide
                .with(holder.view.context)
                .load(profileImgRef)
                .circleCrop().into(holder.rightProfileImage)
             */
            } else {
                //it's an incoming message from the other user
                sentLayout.visibility = View.GONE
                incomeText.text = item.text
                timeLeft.text = dateFormat.format(item.time)
                //TODO update image profile or remove it if it's too complex manage images
                /*val profileImgRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + item.senderId)
            Glide
                .with(holder.view.context)
                .load(profileImgRef)
                .circleCrop().into(holder.leftProfileImage)
             */
            }
        } else {
            //the message is a request!!!
            //variables to interact with request layout
            val incomeRequest : CardView = holder.view.findViewById(R.id.income_request)
            val sentRequest : CardView = holder.view.findViewById(R.id.request_sent)
            val incomeRequestTime : TextView = holder.view.findViewById(R.id.time_income_request)
            val sentRequestTime : TextView = holder.view.findViewById(R.id.time_request_sent)
            if(item.senderId== Firebase.auth.currentUser?.uid){
                //the request has been sent from the current user
                incomeRequest.visibility = View.GONE
                sentRequestTime.text = dateFormat.format(item.time)
            }else{
                sentRequest.visibility = View.GONE
                incomeRequestTime.text = dateFormat.format(item.time)
            }
        }
    }



    override fun getItemCount(): Int = messages.size


}