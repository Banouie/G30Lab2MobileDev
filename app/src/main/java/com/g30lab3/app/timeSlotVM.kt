package com.g30lab3.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.models.timeSlotRepository
import kotlin.concurrent.thread

class timeSlotVM(application: Application) : AndroidViewModel(application) {

    //obtain timeSlot repository instance
    val repo = timeSlotRepository(application)

    // get a LiveData representation of all timeSlot in the DB to be observed from Views in application UI
    val all: LiveData<List<timeSlot>> = repo.getAll()

    /*** FUNCTIONS TO INTERACT WITH DB FROM THE APPLICATION ***/

    //Add a timeSlot to DB if doesn't exist already, update it otherwise
    fun add(timeSlot: timeSlot) {
        thread {
            if (all.value?.contains(timeSlot)==true) {
                repo.update(timeSlot)
            } else {
                repo.add(timeSlot)
            }
        }
    }

    //delete all the timeSlots in the DB
    fun clear() {
        thread {
            repo.clear()
        }
    }

}