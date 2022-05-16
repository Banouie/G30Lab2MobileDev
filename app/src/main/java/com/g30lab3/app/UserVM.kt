package com.g30lab3.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.user
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class UserVM(application: Application) : AndroidViewModel(application) {

    val db = FirebaseFirestore.getInstance()

    /*** FUNCTIONS TO INTERACT WITH DB FROM THE APPLICATION ***/


    fun upload(user: user): Task<Void> {
        return db.collection("Users").document(user.id).set(user)
    }



}