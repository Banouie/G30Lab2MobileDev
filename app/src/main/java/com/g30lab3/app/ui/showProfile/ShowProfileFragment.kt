package com.g30lab3.app.ui.showProfile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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


        val sharedPref: SharedPreferences = this.requireActivity().getSharedPreferences("Profile", MODE_PRIVATE)
        //check if exist a profile configuration:

        val prefs = requireContext().getSharedPreferences("Profile", MODE_PRIVATE)
        val fullName = prefs.getString("FULL_NAME", "Full Name")

        fullNameTextView.setText(fullName)
        nickNameTextView.setText(prefs.getString("NICKNAME", "nickname"))
        mailTextView.setText(prefs.getString("EMAIL", "email@address"))
        locationTextView.setText(prefs.getString("LOCATION", "location"))
        descriptionTextView.setText(prefs.getString("DESCRIPTION", "description"))
        skillsTextView.setText(prefs.getString("SKILLS", "Skill1, skill2"))

       /*
        try{


            var savedData: JSONObject= JSONObject(sharedPref.getString("Profile",""))
            //configuration exists, set values to config parameters
            fullNameTextView.setText(savedData.getString("fullname"))
            nickNameTextView.setText(savedData.getString("nickname"))
            mailTextView.setText(savedData.getString("mail"))
            locationTextView.setText(savedData.getString("location"))
            skillsTextView.setText(savedData.getString("skills"))
            descriptionTextView.setText(savedData.getString("description"))
        }catch(e: JSONException){
            //no configuration saved, set values to default parameters
        }

        //set Profile picture
        try {
            //if already exists a profile Image set it
            activity?.openFileInput("profilePic.jpg").use {
                profilePicImageView.setImageBitmap(BitmapFactory.decodeStream(it))
            }
        }catch(e: FileNotFoundException){
            //no profileImage, set default image
        }
*/
    }








    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        //manage Result intent from EditProfile
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                //retrive updated data
                val fullname: String = data.getStringExtra("group30.lab1.FULL_NAME").toString()
                val nickname: String = data.getStringExtra("group30.lab1.NICKNAME").toString()
                val mail: String = data.getStringExtra("group30.lab1.MAIL").toString()
                val location: String = data.getStringExtra("group30.lab1.LOCATION").toString()
                val skills: String = data.getStringExtra("group30.lab1.SKILLS").toString()
                val description: String = data.getStringExtra("group30.lab1.DESCRIPTION").toString()
                //update layout
                fullNameTextView.setText(fullname)
                nickNameTextView.setText(nickname)
                mailTextView.setText(mail)
                locationTextView.setText(location)
                skillsTextView.setText(skills)
                descriptionTextView.setText(description)

            }
        }
    }

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