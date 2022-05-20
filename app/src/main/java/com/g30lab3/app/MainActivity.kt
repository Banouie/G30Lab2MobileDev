package com.g30lab3.app

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileNotFoundException





class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    val userVm by viewModels<UserVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.skillsListFragment,
                R.id.showProfileFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //*** UPDATE THE USER PHOTO AND NAME IN THE DRAWER
        //set the image of the user and the name in the drawer
        userVm.loggedUser.observe(this){
            val hView = binding.navView.getHeaderView(0)
            var drawerProfileImage: ImageView = hView.findViewById(R.id.drawer_profile_img)
            var drawerUserName: TextView = hView.findViewById(R.id.drawer_name)
            var imageRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + it.id)
            Glide
                .with(this)
                .load(imageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(drawerProfileImage)
            drawerUserName.text = it.full_name
        }


        //[Start] Button for logout
        val btnLogout = findViewById<Button>(R.id.button_logout)

        btnLogout.setOnClickListener{
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Exit App")
            alertDialogBuilder.setMessage("Are you sure you want to log out?")
            alertDialogBuilder.setPositiveButton("Yes") { _: DialogInterface, i: Int ->
                Firebase.auth.signOut()
                Snackbar.make(findViewById<View>(android.R.id.content).rootView,"Logged out",Snackbar.LENGTH_LONG).show()
                //TODO navigate to the login fragment
            }
            alertDialogBuilder.setNegativeButton("Cancel") { _: DialogInterface, i: Int -> }
            var alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
        //[End]

        FirebaseFirestoreSettings.Builder().isPersistenceEnabled = false



    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


}

