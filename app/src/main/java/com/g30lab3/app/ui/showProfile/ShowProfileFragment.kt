package com.g30lab3.app.ui.showProfile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


import com.g30lab3.app.R
import com.g30lab3.app.ReviewVM
import com.g30lab3.app.UserVM
import com.g30lab3.app.toReview

import com.g30lab3.app.ui.editProfile.createTagChip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text

//todo tap on stars bring to fragment with list of reviews

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

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
    lateinit var ratingConsumerLayout: LinearLayout
    lateinit var ratingOffererLayout: LinearLayout

    //Firebase storage to manage images
    var storageRef = FirebaseStorage.getInstance().reference

    //set the image Reference
    var imageRef =
        storageRef.child("ProfileImages/" + Firebase.auth.uid) //each user, if has edited its profile, has a profile image named as its UID in the folder "ProfileImages" on Firebase Storage

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
        ratingConsumerLayout = view.findViewById(R.id.ratingConsumerLayout)
        ratingOffererLayout = view.findViewById(R.id.ratingOffererLayout)
        val fab: View = view.findViewById(R.id.floating_action_button)


        // we want to show the current logged user info
        userVM.getUserInfo(Firebase.auth.currentUser?.uid!!)
        userVM.retrievedUser.observe(requireActivity()) {
            if (it != null) {
                fullNameTextView.text = it.full_name
                nickNameTextView.text = it.nickname
                mailTextView.text = it.mail
                locationTextView.text = it.location
                descriptionTextView.text = it.description
                skills = it.skills.toMutableSet()
                //show the skills in chipGroup
                if (context != null) { //this check on context avoid errors
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
                    .whereEqualTo("valuedUser", Firebase.auth.currentUser?.uid)
                    .get()
                    .addOnSuccessListener { rev ->
                        if (rev.isEmpty) {
                            //the user has no reviews as both offerer or consumer
                            ratingConsumerLayout.visibility = View.GONE
                            ratingConsumerText.text = "Consumer Rating: no rating"
                            ratingOffererLayout.visibility = View.GONE
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
                                ratingConsumerLayout.visibility = View.GONE
                                ratingConsumerText.text = "Consumer Rating: no rating"
                            }
                            if (x == 0) {
                                //no review as offerer
                                ratingOffererLayout.visibility = View.GONE
                                ratingOffererText.text = "Offerer Rating: no rating"
                            }
                            ratingConsumer.rating = if (y != 0) (consumerValue / y) else 0f
                            ratingOfferer.rating = if (x != 0) (offererValue / x) else 0f
                        }
                    }
            }
        }

        //add navigation on pressing on ratings to the list of reviews
        ratingConsumerLayout.setOnClickListener {
            val bundle = bundleOf("valuedUser" to Firebase.auth.currentUser?.uid,"valuedUserIsOfferer" to false)
            findNavController().navigate(R.id.action_showProfileFragment_to_reviewsListFragment,bundle)
        }
        ratingOffererLayout.setOnClickListener {
            val bundle = bundleOf("valuedUser" to Firebase.auth.currentUser?.uid,"valuedUserIsOfferer" to true)
            findNavController().navigate(R.id.action_showProfileFragment_to_reviewsListFragment,bundle)
        }



        fab.setOnClickListener {
            findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
        }

        //set Profile picture from firebase
        Glide
            .with(this)
            .load(imageRef)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.ic_download)
            .circleCrop()
            .into(profilePicImageView)


        //Handle back button pressed, go to Home Screen
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_showProfileFragment_to_skillsListFragment)
                }
            })


    }
}



