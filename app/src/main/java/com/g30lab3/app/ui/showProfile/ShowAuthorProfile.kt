package com.g30lab3.app.ui.showProfile


import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.R
import com.g30lab3.app.UserVM
import com.g30lab3.app.toReview
import com.g30lab3.app.ui.editProfile.createTagChip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import java.io.File


class ShowAuthorProfileFragment : Fragment(R.layout.fragment_show_profile) {

    val userVM by viewModels<UserVM>()
    var skills: MutableSet<String> = mutableSetOf()

    lateinit var fullNameTextView: TextView
    lateinit var nickNameTextView: TextView
    lateinit var mailTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var profilePicImageView: ImageView
    lateinit var skillsChipGroup: ChipGroup
    lateinit var descriptionTextView: TextView
    lateinit var ratingConsumer: RatingBar
    lateinit var ratingConsumerText: TextView
    lateinit var ratingOfferer: RatingBar
    lateinit var ratingOffererText: TextView

    //Firebase storage to manage images
    var storageRef = FirebaseStorage.getInstance().reference

    //set the image Reference
    lateinit var imageRef: StorageReference //reference to the author profile pic, initialized below

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fullNameTextView = view.findViewById(R.id.show_full_name)
        nickNameTextView = view.findViewById(R.id.show_nickname)
        mailTextView = view.findViewById(R.id.show_mail)
        locationTextView = view.findViewById(R.id.show_location)
        descriptionTextView = view.findViewById(R.id.show_description)
        skillsChipGroup = view.findViewById(R.id.show_skills)
        profilePicImageView = view.findViewById(R.id.imageView)
        ratingConsumer = view.findViewById(R.id.ratingConsumer)
        ratingOfferer = view.findViewById(R.id.ratingOfferer)
        ratingConsumerText = view.findViewById(R.id.rating_consumer_text)
        ratingOffererText = view.findViewById(R.id.rating_offerer_text)
        val fab: View = view.findViewById(R.id.floating_action_button)

        // if the arguments IS NOT NULL means that we want to show info of a timeSlot author
        arguments?.let {
            var authorID = arguments?.get("uid") as String
            //set correctly the image reference to the author profile picture
            imageRef = storageRef.child("ProfileImages/$authorID")
            // show the author information:
            userVM.getUserInfo(authorID).observe(requireActivity()) {
                if (it != null) {
                    fullNameTextView.text = it.full_name
                    nickNameTextView.text = it.nickname
                    mailTextView.text = it.mail
                    locationTextView.text = it.location
                    descriptionTextView.text = it.description
                    skills = it.skills.toMutableSet()
                    //show the skills in chipGroup
                    if (context != null) { //check that avoid bugs
                        for (skill in skills) {
                            skillsChipGroup.addView(
                                createTagChip(
                                    requireContext(),
                                    skill,
                                    null,
                                    null
                                )
                            )
                        }
                    }
                    //retrieve eventual ratings of the user
                    FirebaseFirestore.getInstance().collection("Reviews")
                        .whereEqualTo("valuedUser", arguments?.get("uid"))
                        .get()
                        .addOnSuccessListener { rev ->
                            if (rev.isEmpty) {
                                //the user has no reviews as both offerer or consumer
                                ratingConsumer.visibility = View.GONE
                                ratingConsumerText.text = "Consumer Rating: no rating"
                                ratingOfferer.visibility = View.GONE
                                ratingOffererText.text = "Offerer Rating: no rating"
                            } else {

                                var consumerValue = 0f
                                var x = 0
                                var offererValue = 0f
                                var y = 0

                                for (review in rev) {
                                    val r = review.toReview()
                                    if (r.valuedUserIsOfferer) {
                                        offererValue += r.ratingReview
                                        x++
                                    } else {
                                        consumerValue += r.ratingReview
                                        y++
                                    }
                                }
                                if (y == 0) {
                                    //no review as consumer
                                    ratingConsumer.visibility = View.GONE
                                    ratingConsumerText.text = "Consumer Rating: no rating"
                                }
                                if (x == 0) {
                                    //no review as offerer
                                    ratingOfferer.visibility = View.GONE
                                    ratingOffererText.text = "Offerer Rating: no rating"
                                }
                                ratingConsumer.rating = if (y != 0) (consumerValue / y) else 0f
                                ratingOfferer.rating = if (x != 0) (offererValue / x) else 0f
                            }
                        }
                    //disable the editProfileButton in this case
                    fab.isEnabled = false
                    fab.visibility = View.GONE
                }
            }
        }

        //set author Profile picture
        Glide
            .with(requireContext())
            .load(imageRef)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .circleCrop()
            .into(profilePicImageView)


    }
}


