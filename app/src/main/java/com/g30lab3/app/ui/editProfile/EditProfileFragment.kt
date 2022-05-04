package com.g30lab3.app.ui.editProfile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.google.android.material.snackbar.Snackbar
import java.io.FileNotFoundException


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var editName: EditText
    lateinit var editNickName: EditText
    lateinit var editEmail: EditText
    lateinit var editLocation: EditText
    lateinit var editDescription: EditText
    lateinit var editSkills :  EditText

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_PICK_IMAGE = 2

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
        //handle back button to navigate to ShowProfile with updated profile date
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Please confirm")
                        setMessage("Do you want to exit from editing profile? All changes will be saved")

                        setPositiveButton("Yes") { _, _ ->
                            val editor = requireContext().getSharedPreferences("Profile", MODE_PRIVATE).edit()
                            editor.putString("FULL_NAME", editName.text.toString()).apply()
                            editor.putString("NICKNAME", editNickName.text.toString()).apply()
                            editor.putString("LOCATION", editLocation.text.toString()).apply()
                            editor.putString("EMAIL", editEmail.text.toString()).apply()
                            editor.putString("DESCRIPTION", editDescription.text.toString()).apply()
                            editor.putString("SKILLS", editSkills.text.toString()).apply()
                            Snackbar.make(view,"Profile info saved",Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(requireContext(),R.color.purple_500)).show()
                            findNavController().navigate(R.id.action_nav_editProfileFragment_to_nav_showProfileFragment)
                        }
                        setNegativeButton("No"){_, _ ->
                            //do nothing
                        }
                        setCancelable(true)
                    }.create().show()
                }

            })

        //show the correct profile Image
        val profilePicImageView = view.findViewById<ImageView>(R.id.imageView_edit)
        try {
            //if already exists a profile Image set it
            requireContext().openFileInput("profilePic.jpg").use {
                profilePicImageView.setImageBitmap(BitmapFactory.decodeStream(it))
            }
        }catch(e: FileNotFoundException){
            //no profileImage, set default image
        }

        //create the popup menu for the edit profile image button
        val photoButton: ImageButton = view.findViewById(R.id.cameraButton)
        photoButton.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(requireContext(), photoButton)
            popupMenu.menuInflater.inflate(R.menu.edit_profile_photo_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                onOptionsItemSelected(item)
            })
            popupMenu.show()

        }


    }

    //handle selected option in the popup menu for the profile image
    //Adapted from previous lab
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.takePhotoFromCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
            R.id.takePhotoFromGallery -> {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, REQUEST_PICK_IMAGE)
            }
        }
        return true
    }


    //Handle the photo selected from gallery or from camera and show as profile picture
    //Adapted from previous lab
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView = view?.findViewById<ImageView>(R.id.imageView_edit)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap: Bitmap = data?.extras?.get("data") as Bitmap
            //save bitmap image as jpg
            requireContext().openFileOutput("profilePic.jpg", MODE_PRIVATE).use {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
            }
            //display image in ImageView
            imageView?.setImageBitmap(imageBitmap)
        }
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE) {
            //get the URI of the image from gallery
            var imageUri = data?.data
            //check if is not null since the createSource method above need a non null URI
            if(imageUri!=null) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                val imageBitmap: Bitmap = ImageDecoder.decodeBitmap(source)
                //save the image in app internal storage
                requireContext().openFileOutput("profilePic.jpg", MODE_PRIVATE).use {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                imageView?.setImageURI(imageUri)
            }
        }
    }
}