package com.g30lab3.app.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.R
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.Status
import com.g30lab3.app.toTimeSlot
import com.g30lab3.app.toUser
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class IncomePendingRequestAdapter(
    val incomeRequests: List<PendingRequestInfo>,
    val activity: FragmentActivity
) : RecyclerView.Adapter<IncomePendingRequestAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var text: TextView = v.findViewById(R.id.income_request_text)
        val image: ImageView = v.findViewById(R.id.income_request_image)
        val timeSlotName: TextView = v.findViewById(R.id.income_request_text2)
        val card: MaterialCardView = v.findViewById(R.id.income_request_card)
        val rateBtn: MaterialButton = v.findViewById(R.id.rate_btn)
        val view = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context)
            .inflate(R.layout.income_request_layout, parent, false)
        return IncomePendingRequestAdapter.ItemViewHolder(vl)
    }

    override fun onBindViewHolder(
        holder: IncomePendingRequestAdapter.ItemViewHolder,
        position: Int
    ) {
        val item = incomeRequests[position]

        if (item.status == Status.PENDING) {
            //we are displaying the request in the income field
            FirebaseFirestore.getInstance().collection("Users").document(item.requestingUser).get()
                .addOnSuccessListener {
                    val user = it.toUser()
                    holder.text.text = "From: ${user.full_name}"
                    val imageRef =
                        FirebaseStorage.getInstance().reference.child("ProfileImages/" + user.id)
                    Glide
                        .with(activity)
                        .load(imageRef)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(holder.image)

                    FirebaseFirestore.getInstance().collection("TimeSlotAdvCollection")
                        .document(item.leadingTimeSlot).get().addOnSuccessListener {
                            holder.timeSlotName.text = "For: ${it.toTimeSlot().title}"
                        }
                }
        } else {
            //we are displaying the request assigned to the logged user in assigned Fragment, show information about the offerer
            FirebaseFirestore.getInstance().collection("Users").document(item.authorOfTimeSlot)
                .get()
                .addOnSuccessListener {
                    val user = it.toUser()
                    holder.text.text = "Offerer: ${user.full_name}"
                    val imageRef =
                        FirebaseStorage.getInstance().reference.child("ProfileImages/" + user.id)
                    Glide
                        .with(activity)
                        .load(imageRef)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(holder.image)

                    FirebaseFirestore.getInstance().collection("TimeSlotAdvCollection")
                        .document(item.leadingTimeSlot).get().addOnSuccessListener {
                            holder.timeSlotName.text =
                                "Time Slot assigned:\n${it.toTimeSlot().title}"
                        }

                    //check if exists the review for this accepted request with valuedUser == OffererUser (if the logged user has reviewed the offerer basically)
                    FirebaseFirestore.getInstance().collection("Reviews")
                        .whereEqualTo("forRequest", item.chatId)
                        .whereEqualTo("valuedUser", user.id)
                        .whereEqualTo("writerUser", Firebase.auth.currentUser?.uid)
                        .get()
                        .addOnSuccessListener { q ->
                            if (!q.isEmpty) {
                                //review already exists
                                holder.rateBtn.text = "Review done!"
                                holder.rateBtn.setIconResource(R.drawable.ic_review_done)
                                holder.rateBtn.isCheckable = false
                                holder.rateBtn.isEnabled = false
                            }
                        }
                    holder.rateBtn.visibility = View.VISIBLE //show the button, if the review already exists will be disabled, otherwise will lead to the review layout
                    holder.rateBtn.setOnClickListener {
                        //insert in bundle some useful info for the review using data of this accepted request and send it to rate format, also open rate format
                        val bundle = bundleOf(
                            "writerUser" to Firebase.auth.currentUser?.uid!!,
                            "valuedUser" to user.id,
                            "forRequest" to item.chatId,
                            "valuedUserIsOfferer" to true
                        )
                        holder.view.findNavController()
                            .navigate(R.id.action_showRequestsFragment_to_reviewFragment, bundle)
                    }
                }
        }



        holder.card.setOnClickListener {
            val bundle = bundleOf(
                "timeSlotId" to item.leadingTimeSlot,
                "requestUser" to item.requestingUser,
                "authorUser" to item.authorOfTimeSlot
            )
            holder.view.findNavController()
                .navigate(R.id.action_showRequestsFragment_to_chatFragment, bundle)
        }


    }

    override fun getItemCount(): Int {
        return incomeRequests.size
    }
}
