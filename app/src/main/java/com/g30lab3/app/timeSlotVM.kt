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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.concurrent.thread

class timeSlotVM(application: Application) : AndroidViewModel(application) {

    val db = FirebaseFirestore.getInstance()

    //obtain timeSlot repository instance
    val repo = timeSlotRepository(application)

    private val _all = MutableLiveData<List<timeSlot>>()
    val all: LiveData<List<timeSlot>> = _all
    private val listner: ListenerRegistration

    init {
        listner = FirebaseFirestore.getInstance().collection("TimeSlotAdvCollection")
            .addSnapshotListener { value, error ->
                if (value != null) {
                    _all.value = if (error != null) emptyList() else value.mapNotNull { d -> d.toTimeSlot()
                    }
                }
            }
    }

    //convert the retrived data from Firebase to a timeSlot object class
    fun DocumentSnapshot.toTimeSlot() : timeSlot?{
        return timeSlot(
            id= 0,
            title = get("title") as String,
            description = get("description") as String,
            date = get("date") as String,
            location = get("location") as String,
            duration = (get("duration") as Long).toInt(),
            time = get("time") as String
        )
    }

    /*** FUNCTIONS TO INTERACT WITH DB FROM THE APPLICATION ***/

    //Add a timeSlot to firebase DB, return the task in order to perform actions where it's called with callback (addOnFailureListener, ecc)
    fun add(timeSlot: timeSlot): Task<Void> {
        return db.collection("TimeSlotAdvCollection").document().set(timeSlot)
    }


}