package com.g30lab3.app.ui.showProfile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
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

import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException


class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {


    lateinit var fullNameTextView: TextView
    lateinit var nickNameTextView: TextView
    lateinit var mailTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var profilePicImageView: ImageView
    lateinit var skillsTextView: TextView
    lateinit var descriptionTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullNameTextView = view.findViewById(R.id.show_full_name)
        nickNameTextView = view.findViewById(R.id.show_nickname)
        mailTextView = view.findViewById(R.id.show_mail)
        locationTextView = view.findViewById(R.id.show_location)
        descriptionTextView = view.findViewById(R.id.show_description)
        skillsTextView = view.findViewById(R.id.show_skills)
        profilePicImageView = view.findViewById(R.id.imageView)


        val sharedPref: SharedPreferences =
            this.requireActivity().getSharedPreferences("Profile", MODE_PRIVATE)
        //check if exist a profile configuration:

        val prefs = requireContext().getSharedPreferences("Profile", MODE_PRIVATE)
        val fullName = prefs.getString("FULL_NAME", "Full Name")

        fullNameTextView.setText(fullName)
        nickNameTextView.setText(prefs.getString("NICKNAME", "nickname"))
        mailTextView.setText(prefs.getString("EMAIL", "email@address"))
        locationTextView.setText(prefs.getString("LOCATION", "location"))
        descriptionTextView.setText(prefs.getString("DESCRIPTION", "description"))
        skillsTextView.setText(prefs.getString("SKILLS", "Skill1, skill2"))

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


/*
    private var _binding: FragmentShowProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(ShowProfileViewModel::class.java)

        _binding = FragmentShowProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


/*
        val textView: TextView = binding.showProfileTitle
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

 */
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

 */

    }
}