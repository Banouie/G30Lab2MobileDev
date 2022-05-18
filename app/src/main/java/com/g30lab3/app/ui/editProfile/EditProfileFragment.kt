package com.g30lab3.app.ui.editProfile

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.SkillsVM
import com.g30lab3.app.UserVM
import com.g30lab3.app.models.user
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException

//function used to show a snackbar after creating a timeSlot
private fun createSnackBar(message: String, view: View, context: Context, goodNews: Boolean) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(
            ContextCompat.getColor(
                context,
                if (goodNews) R.color.purple_500 else R.color.red
            )
        )
        .show()
}

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var editName: EditText
    lateinit var editNickName: EditText
    lateinit var editEmail: EditText
    lateinit var editLocation: EditText
    lateinit var editDescription: EditText
    lateinit var editSkills: EditText

    lateinit var drawer_img: ImageView
    lateinit var drawer_name: TextView

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_PICK_IMAGE = 2

    val db = FirebaseFirestore.getInstance()
    var curr_user = Firebase.auth.currentUser
    val user_vm by viewModels<UserVM>()
    val skillsVM by viewModels<SkillsVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //variables to interact with the drawer from this fragment
        val navigationView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        drawer_img = headerView.findViewById(R.id.drawer_profile_img)
        drawer_name = headerView.findViewById(R.id.drawer_name)
        // ***

        editName = view.findViewById(R.id.edit_full_name)
        editNickName = view.findViewById(R.id.edit_nickname)
        editLocation = view.findViewById(R.id.edit_location)
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


        //show the correct profile Image
        val profilePicImageView = view.findViewById<ImageView>(R.id.imageView_edit)
        try {
            //if already exists a profile Image set it
            requireContext().openFileInput("profilePic.jpg").use {
                val img: Bitmap = BitmapFactory.decodeStream(it)
                profilePicImageView.setImageBitmap(img)
            }
        } catch (e: FileNotFoundException) {
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

        // Start managing the skills field:
        //if there are saved skills retrive them, if not set the skillsSet to empty set
        var oldSkillsSet: MutableSet<String>? =
            showInfo.getStringSet("SKILLS", mutableSetOf()) //skills retrived from sharedPrefs
        var skillsSet: MutableSet<String>? =
            mutableSetOf() //the skill set that will be edited in order to save changes with sharedpref.setStringSet
        if (oldSkillsSet != null) {
            skillsSet?.addAll(oldSkillsSet)//copy the skills retrived from shared prefs in the editable set
        }
        val skillsEditor: TextInputLayout = view.findViewById(R.id.skills_input)
        val chipGroup: ChipGroup = view.findViewById(R.id.show_skills)
        if (skillsSet?.isNotEmpty() == true) {
            //if there are shared skills preferences create a chipset group with retrived skills from shared preferences
            for (skill in skillsSet) {
                chipGroup.addView(createTagChip(requireContext(), skill, skillsSet, chipGroup))
            }
            chipGroup.visibility = View.VISIBLE
        }

        skillsEditor.setEndIconOnClickListener {
            // Respond to end icon presses
            val newSkill: String = editSkills.text.toString()
            skillsSet?.add(newSkill)
            chipGroup.addView(createTagChip(requireContext(), newSkill, skillsSet, chipGroup))
            editSkills.setText("")
            if (skillsSet?.isNotEmpty() == true) chipGroup.visibility = View.VISIBLE
        }
        // End managing skills field


        // Handle back button to navigate to ShowProfile and saving with shared preferences updated profile date
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Save changes?")
                        setMessage("Exiting this screen any changes will be saved")
                        setPositiveButton("Yes") { _, _ ->
                            //TODO save the user profile info on Firebase:
                            //skills need to be a list in order to be serializable with firebase
                            user_vm.upload(
                                user(
                                    curr_user?.uid!!,
                                    editName.text.toString(),
                                    editNickName.text.toString(),
                                    editDescription.text.toString(),
                                    skillsSet!!.toMutableList(),
                                    editLocation.text.toString(),
                                    editEmail.text.toString()
                                )
                            )
                                .addOnSuccessListener {
                                    Log.d("UPLOAD", "OK")
                                    skillsVM.add(skillsSet)
                                    createSnackBar("Profile info saved",view,requireContext(),true)
                                    drawer_name.setText(editName.text.toString())//set the saved name of the user also in the navigation drawer
                                    findNavController().navigate(R.id.action_nav_editProfileFragment_to_nav_showProfileFragment)
                                }.addOnFailureListener {
                                    Log.d("UPLOAD", "ERROR")
                                    createSnackBar("Error!",view,requireContext(),false)
                                }


                            /*
                            val editor =
                                requireContext().getSharedPreferences("Profile", MODE_PRIVATE)
                                    .edit()
                            editor.putString("FULL_NAME", editName.text.toString()).apply()
                            editor.putString("NICKNAME", editNickName.text.toString()).apply()
                            editor.putString("LOCATION", editLocation.text.toString()).apply()
                            editor.putString("EMAIL", editEmail.text.toString()).apply()
                            editor.putString("DESCRIPTION", editDescription.text.toString()).apply()
                            editor.putStringSet("SKILLS", skillsSet).apply()
                            */

                        }
                        setNegativeButton("No") { _, _ ->
                            //do nothing
                        }
                        setCancelable(true)
                    }.create().show()
                }

            })


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
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            //display image in ImageView
            imageView?.setImageBitmap(imageBitmap)
            drawer_img.setImageBitmap(imageBitmap)//update also the image in the navigation drawer
        }
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE) {
            //get the URI of the image from gallery
            var imageUri = data?.data
            //check if is not null since the createSource method above need a non null URI
            if (imageUri != null) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                val imageBitmap: Bitmap = ImageDecoder.decodeBitmap(source)
                //save the image in app internal storage
                requireContext().openFileOutput("profilePic.jpg", MODE_PRIVATE).use {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                imageView?.setImageURI(imageUri)
                drawer_img.setImageURI(imageUri)//update also the image in the navigation drawer
            }
        }
    }
}

//this function create a chip in a chip group, if receives also a chipgroup will enable for the created chip the erase button
// used with an erase button in EditProfileFragment, without in ShowProfileFragment
fun createTagChip(
    context: Context,
    chipName: String,
    set: MutableSet<String>?,
    chipGroup: ChipGroup?
): Chip {
    return Chip(context).apply {
        text = chipName
        if (chipGroup != null) {
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                var a = set?.remove(chipName)
                chipGroup.removeView(it)
            }
        }
    }

}