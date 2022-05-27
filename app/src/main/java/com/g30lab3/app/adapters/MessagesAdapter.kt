package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.R
import com.g30lab3.app.models.textMessage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.AbstractMap

class MessagesAdapter(val messages: List<textMessage>) :
    RecyclerView.Adapter<MessagesAdapter.ItemViewHolder>() {
    val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT,SimpleDateFormat.SHORT)

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view = v
        val incomeLayout: LinearLayout = v.findViewById(R.id.income_message)
        val sentLayout: LinearLayout = v.findViewById(R.id.sent_message)
        //val leftProfileImage: ImageView = v.findViewById(R.id.income_profile_image)
        //val rightProfileImage: ImageView = v.findViewById(R.id.sender_profile_image)
        val timeRight :TextView = v.findViewById(R.id.time_right)
        val timeLeft :TextView = v.findViewById(R.id.time_left)
        val incomeText: TextView = v.findViewById(R.id.income_text)
        val sentText: TextView = v.findViewById(R.id.sent_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl =
            LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false);
        return MessagesAdapter.ItemViewHolder(vl);
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = messages[position]
        if (item.senderId == Firebase.auth.currentUser?.uid) {
            //it's a message sent from the current user
            holder.incomeLayout.visibility = View.GONE
            holder.sentText.text = item.text
            holder.timeRight.text = dateFormat.format(item.time)
            //TODO update image profile or remove it if it's too complex manage images
            /*val profileImgRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + Firebase.auth.currentUser?.uid)
            Glide
                .with(holder.view.context)
                .load(profileImgRef)
                .circleCrop().into(holder.rightProfileImage)
             */
        } else {
            //it's an incoming message from the other user
            holder.sentLayout.visibility = View.GONE
            holder.incomeText.text = item.text
            holder.timeLeft.text = dateFormat.format(item.time)
            //TODO update image profile or remove it if it's too complex manage images
            /*val profileImgRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + item.senderId)
            Glide
                .with(holder.view.context)
                .load(profileImgRef)
                .circleCrop().into(holder.leftProfileImage)
             */
        }
    }

    override fun getItemCount(): Int = messages.size


}