package com.g30lab3.app.ui.showProfile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide


import com.g30lab3.app.R
import com.g30lab3.app.UserVM

import com.g30lab3.app.ui.editProfile.createTagChip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


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
        val fab: View = view.findViewById(R.id.floating_action_button)

        // if the arguments IS NOT NULL means that we want to show info of a timeSlot author
        arguments?.let {
            userVM.getUserInfo(arguments?.get("uid") as String).observe(requireActivity()) {
                if (it != null) {
                    Log.d("PPP", "Profile info downloaded")
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
                    //disable the editProfileButton in this case
                    fab.isEnabled = false
                    fab.visibility = View.GONE
                }
            }
        }
        // OTHERWISE if arguments IS NULL we want to show the current logged user info
        if (arguments == null) {
            userVM.getUserInfo(Firebase.auth.currentUser?.uid!!).observe(requireActivity()) {
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
                }
            }

        }

        fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
        }

        //[START] set Profile picture
        Glide.with(requireContext()).load(imageRef).into(profilePicImageView);
        /*
        val localFile = File.createTempFile("profilePic", "jpg")//we store the profile image in this temp file
        imageRef.getFile(localFile).addOnSuccessListener {
            profilePicImageView.setImageBitmap(BitmapFactory.decodeFile(context?.filesDir.toString() + "/profilePic.jpg"))
        }.addOnFailureListener {
            //Show the default image
        }

         */
        //[END]


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



