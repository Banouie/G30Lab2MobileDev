package com.g30lab3.app.ui.editProfile

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.SkillsVM
import com.g30lab3.app.UserVM
import com.g30lab3.app.models.User
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


/**function used to show a Snackbar after creating a timeSlot */
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
    lateinit var skillsSet: MutableSet<String>
    lateinit var profilePicImageView: ImageView

    lateinit var drawer_img: ImageView
    lateinit var drawer_name: TextView

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_PICK_IMAGE = 2

    //Firebase storage to manage images
    var storageRef = FirebaseStorage.getInstance().reference

    //set the image Reference
    var imageRef = storageRef.child("ProfileImages/" + Firebase.auth.uid)


    val userVM by viewModels<UserVM>()     // -> to show user info
    val skillsVM by viewModels<SkillsVM>() // -> to save inserted skills


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //variables to interact with the drawer from this fragment
        val navigationView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        drawer_img = headerView.findViewById(R.id.drawer_profile_img)
        drawer_name = headerView.findViewById(R.id.drawer_name)
        // ***

        profilePicImageView = view.findViewById<ImageView>(R.id.imageView_edit)
        editName = view.findViewById(R.id.edit_full_name)
        editNickName = view.findViewById(R.id.edit_nickname)
        editLocation = view.findViewById(R.id.edit_location)
        editEmail = view.findViewById(R.id.edit_mail)
        editDescription = view.findViewById(R.id.edit_description)
        editSkills = view.findViewById(R.id.edit_skills)

        userVM.loggedUser.observe(requireActivity()) {
            if (it != null) {
                //show the profile information in the layout:
                editName.setText(it.full_name)
                editNickName.setText(it.nickname)
                editLocation.setText(it.location)
                editEmail.setText(it.mail)
                editDescription.setText(it.description)
                skillsSet = it.skills.toMutableSet()
                val skillsEditor: TextInputLayout = view.findViewById(R.id.skills_input)
                val chipGroup: ChipGroup = view.findViewById(R.id.show_skills)
                if (skillsSet.isNotEmpty()) {
                    //show the skills in a chipGroup
                    for (skill in skillsSet) {
                        chipGroup.addView(
                            createTagChip(
                                requireContext(),
                                skill,
                                skillsSet,
                                chipGroup
                            )
                        )
                    }
                    chipGroup.visibility = View.VISIBLE
                }
                //the "x" icon of each skill-chip in the chipGroup will erase that skill from the set
                skillsEditor.setEndIconOnClickListener {
                    val newSkill: String = editSkills.text.toString()
                    skillsSet.add(newSkill)
                    chipGroup.addView(
                        createTagChip(
                            requireContext(),
                            newSkill,
                            skillsSet,
                            chipGroup
                        )
                    )
                    editSkills.setText("")
                    if (skillsSet.isNotEmpty()) chipGroup.visibility = View.VISIBLE
                }
            }
        }


        // show the correct profile Image if exists on FirebaseStorage
        Glide.with(requireContext()).load(imageRef).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).circleCrop().into(profilePicImageView)



        //[Start] create the popup menu for the edit profile image button
        val photoButton: ImageButton = view.findViewById(R.id.cameraButton)
        photoButton.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(requireContext(), photoButton)
            popupMenu.menuInflater.inflate(R.menu.edit_profile_photo_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                onOptionsItemSelected(item)
            })
            popupMenu.show()

        }
        //[End]


        // Handle back button to navigate to ShowProfile and saving with shared preferences updated profile date
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Save changes?")
                        setMessage("Exiting this screen any changes will be saved")
                        setPositiveButton("Yes") { _, _ ->
                            userVM.upload(
                                User(
                                    Firebase.auth.currentUser?.uid!!,
                                    editName.text.toString(),
                                    editNickName.text.toString(),
                                    editDescription.text.toString(),
                                    skillsSet!!.toMutableList(), //skills need to be a list in order to be serializable with firebase
                                    editLocation.text.toString(),
                                    editEmail.text.toString()
                                )
                            )
                                .addOnSuccessListener {
                                    Log.d("UPLOAD", "OK")
                                    skillsVM.add(skillsSet) //upload the declared new skills to Firebase DB
                                    createSnackBar(
                                        "Profile info saved",
                                        view,
                                        requireContext(),
                                        true
                                    )
                                    drawer_name.text =
                                        editName.text.toString()//set the name of the user also in the lateral navigation drawer
                                    findNavController().navigate(R.id.action_editProfileFragment_to_showProfileFragment)
                                }.addOnFailureListener {
                                    Log.d("UPLOAD", "ERROR")
                                    createSnackBar("Error!", view, requireContext(), false)
                                }
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

    //TODO use a viewmodel.observer for refresh the image in imageview
    //Handle the photo selected from gallery or from camera and show as profile picture
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView = view?.findViewById<ImageView>(R.id.imageView_edit)


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            //get the image captured from the camera
            val imageBitmap: Bitmap = data?.extras?.get("data") as Bitmap
            //save bitmap image as jpg (create the file)
            requireContext().openFileOutput("profilePic.jpg", MODE_PRIVATE).use { f ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, f)
            }
            //retrieve the file (using an Input Stream) and upload it on Firebase
            var inputStream = FileInputStream(context?.filesDir.toString() + "/profilePic.jpg")
            imageRef.putStream(inputStream)
                .addOnSuccessListener {
                    Glide.with(requireContext()).load(imageRef).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.ic_download).circleCrop().into(profilePicImageView)
                    createSnackBar("Image saved", requireView(), requireContext(), true)
                    Glide.with(requireActivity()).load(imageRef).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).circleCrop().into(drawer_img)
                }.addOnFailureListener {
                    createSnackBar("Error saving image", requireView(), requireContext(), false)
                }
        }
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE) {
            //get the URI of the image from gallery
            var imageUri = data?.data
            //check if is not null since the createSource method above need a non null URI
            if (imageUri != null) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                //save the image in Firebase storage
                imageRef.putFile(imageUri)
                    .addOnFailureListener {
                        createSnackBar("Error saving image", requireView(), requireContext(), false)
                    }.addOnSuccessListener {
                        Glide.with(requireContext()).load(imageRef).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.ic_download).circleCrop().into(profilePicImageView)
                        createSnackBar("Image saved", requireView(), requireContext(), true)
                        Glide.with(requireActivity()).load(imageRef).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).circleCrop().into(drawer_img)
                    }

            }
        }
    }

    //Avoid back arrow in appBar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

/**This function create a chip in a chip group, if receives also a chipgroup will enable for the created chip the erase button
 * used with an erase button in EditProfileFragment, without in ShowProfileFragment */
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