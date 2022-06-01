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
import com.google.firebase.firestore.FirebaseFirestore
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
            //we are displaying the request assigned to the logged user in assigned Fragment, show rating button and information
            holder.rateBtn.visibility = View.VISIBLE
            FirebaseFirestore.getInstance().collection("Users").document(item.authorOfTimeSlot)
                .get()
                .addOnSuccessListener {
                    val user = it.toUser()
                    holder.text.text = "Offerer user: ${user.full_name}"
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

                    holder.rateBtn.setOnClickListener {
                        //todo add rating to the owner of the timeslot assigned
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
