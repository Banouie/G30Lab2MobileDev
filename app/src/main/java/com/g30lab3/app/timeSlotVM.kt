package com.g30lab3.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.models.timeSlotRepository
import kotlin.concurrent.thread

class timeSlotVM(application: Application): AndroidViewModel(application) {

    //obtain timeSlot repository instance
    val repo = timeSlotRepository(application)

    // get a LiveData representation of all timeSlot in the DB to be observed from Views in application UI
    val all: LiveData<List<timeSlot>> = repo.getAll()

    /*** FUNCTIONS TO INTERACT WITH DB FROM THE APPLICATION ***/

    //Add a timeSlot to DB
    fun add(timeSlot: timeSlot) {
        thread {
            repo.add(timeSlot)
        }
    }

    //delete all the timeSlots in the DB
    fun clear(){
        thread {
            repo.clear()
        }
    }

}