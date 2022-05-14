package com.g30lab3.app.ui.showProfile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.ui.editProfile.createTagChip
import com.google.android.material.chip.ChipGroup

import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException


class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {


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


        //check if exist a profile configuration:
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