package com.g30lab3.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.timeSlot
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class timeSlotVM(application: Application) : AndroidViewModel(application) {

    val db = FirebaseFirestore.getInstance()

    private val _all = MutableLiveData<List<timeSlot>>()
    val all: LiveData<List<timeSlot>> = _all
    private val listner: ListenerRegistration

    init {
        val collectionRef = db.collection("TimeSlotAdvCollection")
        listner = collectionRef.addSnapshotListener { value, error ->
                if (value != null) {
                    _all.value = if (error != null || value.isEmpty) emptyList() else value.mapNotNull { d ->
                        d.toTimeSlot()
                    }
                }
            }
    }

    //convert the retrived data from Firebase to a timeSlot object class
    fun DocumentSnapshot.toTimeSlot(): timeSlot {
        return timeSlot(
            id = id, //the id of the timeSlot kotlin object will be the ID of the document created from Firebase previously
            title = get("title") as String,
            description = get("description") as String,
            date = get("date") as String,
            location = get("location") as String,
            duration = (get("duration") as Long).toInt(),
            author = if (get("author") != null) get("author") as String else "unknown",
            time = get("time") as String

        )
    }

    /*** FUNCTIONS TO INTERACT WITH DB FROM THE APPLICATION ***/

    //Add a timeSlot to firebase DB, return the task in order to perform actions where it's called with callback (addOnFailureListener, ecc)
    fun add(timeSlot: timeSlot): Task<Void> {
        if (all.value?.contains(timeSlot) == true) {
            //we need to update an already existing timeSlot
            return db.collection("TimeSlotAdvCollection").document(timeSlot.id).set(timeSlot)
        } else {
            return db.collection("TimeSlotAdvCollection").document().set(timeSlot)
        }
    }


}