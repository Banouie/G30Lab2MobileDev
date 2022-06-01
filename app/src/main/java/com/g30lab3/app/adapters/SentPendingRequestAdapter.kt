package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.Status
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.toUser
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SentPendingRequestAdapter(
    val timeSlots: List<timeSlot>,
    val requests: List<PendingRequestInfo>,
    val activity: FragmentActivity
) :
    RecyclerView.Adapter<SentPendingRequestAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context)
            .inflate(R.layout.sent_pending_request_layout, parent, false)
        return SentPendingRequestAdapter.ItemViewHolder(vl)
    }

    override fun onBindViewHolder(holder: SentPendingRequestAdapter.ItemViewHolder, position: Int) {

        val item = requests[position]
        val leadingTimeSlot =
            timeSlots.firstOrNull { t -> t.id == item.leadingTimeSlot } //get the timeslot of the request

        val title: TextView = holder.view.findViewById(R.id.request_title)
        title.text = leadingTimeSlot?.title
        val date: TextView = holder.view.findViewById(R.id.request_date)
        date.text = leadingTimeSlot?.date
        val time: TextView = holder.view.findViewById(R.id.request_time)
        time.text = leadingTimeSlot?.title
        val location: TextView = holder.view.findViewById(R.id.request_location)
        location.text = leadingTimeSlot?.location
        val openChatBtn: MaterialButton = holder.view.findViewById(R.id.sent_request_chatBtn)
        val detailsBtn: MaterialButton = holder.view.findViewById(R.id.sent_request_detailsBtn)

        openChatBtn.setOnClickListener {
            val bundle = bundleOf(
                "requestUser" to item.requestingUser,
                "timeSlotId" to item.leadingTimeSlot,
                "authorUser" to item.authorOfTimeSlot
            )
            holder.view.findNavController()
                .navigate(R.id.action_showRequestsFragment_to_chatFragment, bundle)
        }

        detailsBtn.setOnClickListener {
            val bundle = bundleOf("time_slot_ID" to item.leadingTimeSlot)
            holder.view.findNavController()
                .navigate(R.id.action_showRequestsFragment_to_timeSlotDetailsFragment, bundle)
        }

        if (item.status == Status.ACCEPTED) {
            //this adapter is used in Accepted layout, show more info:
            val acceptedRequestLayout: LinearLayout =
                holder.view.findViewById(R.id.accepted_request_info)
            acceptedRequestLayout.visibility = View.VISIBLE
            val acceptedText: TextView = holder.view.findViewById(R.id.request_accepted_text)
            val acceptedImg: ImageView = holder.view.findViewById(R.id.request_accepted_Img)
            val rateButton : MaterialButton = holder.view.findViewById(R.id.accepted_request_rateBtn)
            FirebaseFirestore.getInstance().collection("Users").document(item.requestingUser)
                .get()
                .addOnSuccessListener {
                    val user = it.toUser()
                    acceptedText.text = user.full_name
                    val imageRef =
                        FirebaseStorage.getInstance().reference.child("ProfileImages/" + user.id)
                    Glide
                        .with(activity)
                        .load(imageRef)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop()
                        .into(acceptedImg)
                }
            rateButton.visibility = View.VISIBLE
            rateButton.setOnClickListener {
                //todo open rate format
            }


        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}