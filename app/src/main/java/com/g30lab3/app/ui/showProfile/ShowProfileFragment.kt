package com.g30lab3.app.ui.showProfile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.UserVM
import com.g30lab3.app.models.User
import com.g30lab3.app.ui.editProfile.createTagChip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import java.io.FileNotFoundException


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fullNameTextView = view.findViewById(R.id.show_full_name)
        nickNameTextView = view.findViewById(R.id.show_nickname)
        mailTextView = view.findViewById(R.id.show_mail)
        locationTextView = view.findViewById(R.id.show_location)
        descriptionTextView = view.findViewById(R.id.show_description)
        skillsChipGroup = view.findViewById(R.id.show_skills)
        profilePicImageView = view.findViewById(R.id.imageView)

        userVM.loggedUser.observe(requireActivity()){
            if(it!=null){
                Log.d("PPP", "Profile info downloaded")
                fullNameTextView.text = it.full_name
                nickNameTextView.text = it.nickname
                mailTextView.text = it.mail
                locationTextView.text = it.location
                descriptionTextView.text = it.description
                skills = it.skills.toMutableSet()
                //show the skills in chipGroup
                for (skill in skills) {
                    skillsChipGroup.addView(createTagChip(requireContext(), skill, null, null))
                }
            }
        }

        val fab: View = view.findViewById(R.id.floating_action_button)
        fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
        }

        //set Profile picture
        try {
            //if already exists a profile Image set it
            requireActivity().openFileInput("profilePic.jpg").use {
                profilePicImageView.setImageBitmap(BitmapFactory.decodeStream(it))
            }
        } catch (e: FileNotFoundException) {
            //no profileImage, set default image
        }

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


