package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.models.Review
import com.g30lab3.app.toUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)

class ReviewsAdapter(val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text: TextView = v.findViewById(R.id.text_of_review)
        val info: TextView = v.findViewById(R.id.info_of_review)
        val stars: RatingBar = v.findViewById(R.id.ratingBar_reviewsLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context).inflate(R.layout.review_layout, parent, false)
        return ReviewsAdapter.ItemViewHolder(vl)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val review = reviews[position]
        holder.text.text = if (review.commentReview != "") "\"${review.commentReview}\"" else "No review message"
        holder.stars.rating = review.ratingReview
        FirebaseFirestore.getInstance().collection("Users").document(review.writerUser).get().addOnSuccessListener {
            val writerUser = it.toUser()
            holder.info.text = "- ${writerUser.full_name} on ${dateFormat.format(review.date)}" //todo insert date in reviews
        }

    }

    override fun getItemCount(): Int = reviews.size
}