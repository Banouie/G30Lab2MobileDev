package com.g30lab3.app.ui.showProfile

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBindings
import com.g30lab3.app.databinding.FragmentShowProfileBinding
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.g30lab3.app.R
import com.g30lab3.app.ui.editProfile.EditProfileFragment
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


        val sharedPref: SharedPreferences = this.requireActivity().getSharedPreferences("Profile_info", AppCompatActivity.MODE_PRIVATE)


    }




        //restore previously saved configuration, if doesn't already exist set default configuration



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