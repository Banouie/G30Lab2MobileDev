package com.g30lab3.app.ui.review

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.ReviewVM
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ReviewFragment : Fragment(R.layout.fragment_review)  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        
        Log.d("Test", "File ok")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewVM by viewModels<ReviewVM>()
        val authorID = arguments?.get("authorID")
        val requestingID = arguments?.get("requestingID")
        val timeSlotID = arguments?.get("timeSlotId")
        val ratingbar: RatingBar = view.findViewById(R.id.ratingBar)
        val showRating: MaterialButton = view.findViewById(R.id.btn_save_rating)
        val reviewComment:EditText = view.findViewById(R.id.edit_review_text)
        //val box :TextInputLayout = view.findViewById(R.id.edit_box_review)
        var senderId: String
        var receiverId : String

        // unique review Id
        val reviewId = authorID.toString() + timeSlotID

        showRating.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Saving review")
            builder.setMessage("Are you sure you want to save this review?")

            builder.setPositiveButton("Yes") { _, _ ->

                if (Firebase.auth.uid == authorID){
                    senderId = authorID as String
                    receiverId =  requestingID as String
                }else {
                    senderId = requestingID as String
                    receiverId = authorID as String
                }
                Firebase.auth.uid?.let { _ ->
                    reviewVM.createNewReview(reviewId, senderId, receiverId, ratingbar.rating.toFloat(), reviewComment.text.toString())
                }

                Toast.makeText(context,
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
                Log.d("OkSaved", "Current is saved!")

            }

            builder.setNegativeButton("No") { _, _ ->
                Toast.makeText(
                    context,
                    android.R.string.no, Toast.LENGTH_SHORT
                ).show()

                Log.d(
                    "Test", "File ok, rating= " + ratingbar.rating +
                            "userID= " + Firebase.auth.uid.toString() + " authorID= " + authorID +
                            "\nText in edit text= " + reviewComment.text.toString()
                )
            }
            builder.show()

        }

    }
}