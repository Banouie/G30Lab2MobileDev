package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.R
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.toTimeSlot
import com.g30lab3.app.toUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class IncomePendingRequestAdapter (val incomeRequests: List<PendingRequestInfo>, val activity: FragmentActivity) : RecyclerView.Adapter<IncomePendingRequestAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var text : TextView = v.findViewById(R.id.income_request_text)
        val image : ImageView = v.findViewById(R.id.income_request_image)
        val timeSlotName : TextView = v.findViewById(R.id.income_request_text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ItemViewHolder {
        val vl = LayoutInflater.from(parent.context).inflate(R.layout.income_request_layout,parent,false)
        return  IncomePendingRequestAdapter.ItemViewHolder(vl)
    }

    override fun onBindViewHolder(holder: IncomePendingRequestAdapter.ItemViewHolder, position: Int) {
        val item = incomeRequests[position]

        FirebaseFirestore.getInstance().collection("Users").document(item.requestingUser).get().addOnSuccessListener {
                val user = it.toUser()
                holder.text.text = "From: ${user.full_name}"
            val imageRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + user.id)
            Glide
                .with(activity)
                .load(imageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(holder.image)
        }

        FirebaseFirestore.getInstance().collection("TimeSlotAdvCollection").document(item.leadingTimeSlot).get().addOnSuccessListener {
            holder.timeSlotName.text = "For: ${it.toTimeSlot().title}"
        }




    }

    override fun getItemCount(): Int {
        return incomeRequests.size
    }
}