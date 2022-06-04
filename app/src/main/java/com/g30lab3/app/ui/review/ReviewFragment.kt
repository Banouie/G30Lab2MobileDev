package com.g30lab3.app.ui.review

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.R
import com.g30lab3.app.ReviewVM
import com.g30lab3.app.models.Review
import com.g30lab3.app.ui.timeSlotEdit.createSnackBar
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ReviewFragment : Fragment(R.layout.fragment_review)  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewVM by viewModels<ReviewVM>()


        val writerUser = arguments?.get("writerUser") as String
        val valuedUser = arguments?.get("valuedUser") as String
        val forRequest = arguments?.get("forRequest") as String
        val valuedUserIsOfferer =arguments?.get("valuedUserIsOfferer") as Boolean

        val ratingbar: RatingBar = view.findViewById(R.id.ratingBar)
        val saveReview: MaterialButton = view.findViewById(R.id.btn_save_rating)
        val reviewComment:EditText = view.findViewById(R.id.edit_review_text)
        val reviewTitleImg : ImageView = view.findViewById(R.id.review_title_img)
        val reviewTitle: TextView = view.findViewById(R.id.review_title)

        // this makes 1 the minimum number of stars for the rating
        ratingbar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, rating, _ ->
                if (rating < 1.0f) ratingBar.rating = 1.0f
            }

        //get the user to evaluate name and  profile image and set those in the title of this review
        FirebaseFirestore.getInstance().collection("Users").document(valuedUser).get().addOnSuccessListener {
            reviewTitle.text = it.get("full_name") as String
        }
        val imageRef = FirebaseStorage.getInstance().reference.child("ProfileImages/$valuedUser")
        Glide
            .with(requireActivity())
            .load(imageRef)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .circleCrop()
            .into(reviewTitleImg)



        saveReview.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Publish review")
            builder.setMessage("Are you sure you want to publish this review?")
            builder.setPositiveButton("Yes") { _, _ ->
                val newReview = Review(writerUser,valuedUser,forRequest,valuedUserIsOfferer,ratingbar.rating,reviewComment.text.toString())
                reviewVM.createNewReview(newReview)
                createSnackBar("Thank you!",requireView(),requireContext(),true)
                findNavController().popBackStack()
            }
            builder.setNegativeButton("Continue editing") { _, _ ->
                //continue editing
            }
            builder.show()
        }

    }
}