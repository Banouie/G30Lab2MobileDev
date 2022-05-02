package com.g30lab3.app.ui.editProfile

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.g30lab3.app.R


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var editName: EditText
    lateinit var editNickName: EditText
    lateinit var editEmail: EditText
    lateinit var editLocation: EditText
    lateinit var editDescription: EditText
    lateinit var editSkills :  EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editName = view.findViewById(R.id.edit_full_name)
        editNickName = view.findViewById(R.id.edit_nickname)
        editLocation= view.findViewById(R.id.edit_location)
        editEmail = view.findViewById(R.id.edit_mail)
        editDescription = view.findViewById(R.id.edit_description)
        editSkills = view.findViewById(R.id.edit_skills)

        // SharedPreference created to fill all the fields in the edit profile fragment
        val showInfo = requireContext().getSharedPreferences("Profile", MODE_PRIVATE)

        editName.setText(showInfo.getString("FULL_NAME", ""))
        editLocation.setText(showInfo.getString("LOCATION", ""))
        editNickName.setText(showInfo.getString("NICKNAME", ""))
        editEmail.setText(showInfo.getString("EMAIL", ""))
        editDescription.setText(showInfo.getString("DESCRIPTION", ""))
        editSkills.setText(showInfo.getString("SKILLS", ""))
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val editor = requireContext().getSharedPreferences("Profile", MODE_PRIVATE).edit()
                    editor.putString("FULL_NAME", editName.text.toString()).apply()
                    editor.putString("NICKNAME", editNickName.text.toString()).apply()
                    editor.putString("LOCATION", editLocation.text.toString()).apply()
                    editor.putString("EMAIL", editEmail.text.toString()).apply()
                    editor.putString("DESCRIPTION", editDescription.text.toString()).apply()
                    editor.putString("SKILLS", editSkills.text.toString()).apply()
                    Toast.makeText(context , "Data should be saved, check it on Show Profile page :)", Toast.LENGTH_SHORT).show()
                    //TODO back button pressed shoul bring user to the ShowProfile
                }


            })


    }
}