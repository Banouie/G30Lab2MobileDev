package com.g30lab3.app

import android.app.Application
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.models.timeSlotRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.thread

class timeSlotVM(application: Application) : AndroidViewModel(application) {

    val db = FirebaseFirestore.getInstance()

    //obtain timeSlot repository instance
    val repo = timeSlotRepository(application)

    // get a LiveData representation of all timeSlot in the DB to be observed from Views in application UI
    val all: LiveData<List<timeSlot>> = repo.getAll()

    /*** FUNCTIONS TO INTERACT WITH DB FROM THE APPLICATION ***/

    //Add a timeSlot to firebase DB, return the task in order to perform actions where it's called with callback (addOnFailureListener, ecc)
    fun add(timeSlot: timeSlot): Task<Void> {
        return db.collection("TimeSlotAdvCollection").document().set(timeSlot)
    }

    //delete all the timeSlots in the DB
    fun clear() {
        thread {
            repo.clear()
        }
    }

}