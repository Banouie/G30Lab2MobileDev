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
import com.g30lab3.app.models.user
import com.g30lab3.app.ui.editProfile.createTagChip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

import java.io.FileNotFoundException


class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

    //val userVM by viewModels<UserVM>()
    var currentUser = Firebase.auth.currentUser
    lateinit var toShowUser: user
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


        FirebaseFirestore.getInstance().collection("Users").document(currentUser?.uid!!).get()
            .addOnSuccessListener {
                Log.d("ProfileDownload", "Profile info downloaded")
                toShowUser = it.toUser()
                fullNameTextView.text = toShowUser.full_name
                nickNameTextView.text = toShowUser.nickname
                mailTextView.text = toShowUser.mail
                locationTextView.text = toShowUser.location
                descriptionTextView.text = toShowUser.description
                skills = toShowUser.skills.toMutableSet()
                //show the skills
                for (skill in skills) {
                    skillsChipGroup.addView(createTagChip(requireContext(), skill, null, null))
                }
                Log.d("Skills", skills.toString())
            }.addOnFailureListener {
                Log.d("ProfileDownload", "Error")
            }


        //USING SHARED PREFS
        /*
        val prefs = requireContext().getSharedPreferences("Profile", MODE_PRIVATE)
        fullNameTextView.setText(prefs.getString("FULL_NAME", "Full Name"))
        nickNameTextView.setText(prefs.getString("NICKNAME", "nickname"))
        mailTextView.setText(prefs.getString("EMAIL", "email@address"))
        locationTextView.setText(prefs.getString("LOCATION", "location"))
        descriptionTextView.setText(prefs.getString("DESCRIPTION", "description"))



        //show the skills
        var skills:MutableSet<String>? = prefs.getStringSet("SKILLS", mutableSetOf())
        Log.d("Skills from SharedPref:",skills.toString())
        if (skills != null) {
            for (skill in skills){
                skillsChipGroup.addView(createTagChip(requireContext(),skill,null,null))
            }
        }
        //end of show the skills


         */


        val fab: View = view.findViewById(R.id.floating_action_button)
        fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_nav_showProfileFragment_to_nav_editProfileFragment)
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
                    findNavController().navigate(R.id.action_nav_showProfileFragment_to_nav_home)
                }

            })


    }
}

//convert the retrived data from Firebase to a user object class
fun DocumentSnapshot.toUser(): user {
    return user(
        id = get("id") as String,
        full_name = get("full_name") as String,
        nickname = get("nickname") as String,
        description = get("description") as String,
        skills = get("skills") as MutableList<String>,
        location = get("location") as String,
        mail = get("mail") as String
    )
}
