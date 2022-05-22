package com.g30lab3.app.ui.Login

import android.app.ActionBar
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.g30lab3.app.MainActivity
import com.g30lab3.app.R
import com.g30lab3.app.UserVM
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class LoginFragment : Fragment(R.layout.fragment_login) {

    /**Function that properly set the image in the drawer and the name of the user after login success*/
    private fun updateDrawer(){
        val userVM by viewModels<UserVM>()
        val navigationView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val drawerProfileImage: ImageView = headerView.findViewById(R.id.drawer_profile_img)
        val drawerUserName: TextView = headerView.findViewById(R.id.drawer_name)
        //*** UPDATE THE USER PHOTO AND NAME IN THE DRAWER
        //set the image of the user and the name in the drawer
        userVM.loggedUser.observe(requireActivity()){
            val imageRef = FirebaseStorage.getInstance().reference.child("ProfileImages/" + it.id)
            Glide
                .with(requireActivity())
                .load(imageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(drawerProfileImage)
            drawerUserName.text = it.full_name
        }
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    private val REQ_ONE_TAP = 2


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize Firebase Auth
        auth = Firebase.auth

        oneTapClient = Identity.getSignInClient(requireActivity())
        var signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId("337309619753-jb3dupcelec6ps33ssjb4f5ns7rr6aj1.apps.googleusercontent.com")
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()


        var loginBtn: MaterialButton = view.findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity()) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("LOGIN", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("LOGINFAILURE", e.localizedMessage)
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            Log.d("IDTOKEN", "Got ID token.")
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("FIREBASESIGNIN", "signInWithCredential:success")
                                        val user = auth.currentUser
                                        if(user!=null){
                                            updateDrawer()
                                            findNavController().navigate(R.id.action_loginFragment_to_skillsListFragment)

                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //updateUI(null)
                                    }

                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("IDTOKEN", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                }
            }

        }

    }


}


