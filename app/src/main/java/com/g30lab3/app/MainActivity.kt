package com.g30lab3.app

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.g30lab3.app.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //test
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
        val hView = binding.navView.getHeaderView(0)
        var ProfileImage: ImageView = hView.findViewById(R.id.drawer_profile_img)
        var UserName: TextView = hView.findViewById(R.id.drawer_name)
        //set User name in drawer
        val prefs = getSharedPreferences("Profile", AppCompatActivity.MODE_PRIVATE)
        UserName.setText(prefs.getString("FULL_NAME", ""))
        //set Profile picture in drawer
        try {
            //if already exists a profile Image set it
            openFileInput("profilePic.jpg").use {
                ProfileImage.setImageBitmap(BitmapFactory.decodeStream(it))
            }
        } catch (e: FileNotFoundException) {
            //no profile image, set default one
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



    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }


}