package com.g30lab3.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.models.timeSlotRepository
import kotlin.concurrent.thread

class timeSlotVM(application: Application): AndroidViewModel(application) {
    val repo = timeSlotRepository(application)


    val items: LiveData<List<timeSlot>> = repo.getAll()

    fun add() {
        thread {
            repo.add()
        }
    }

    fun count() {
        thread {
            repo.count()
        }
    }

    fun getAll(){
        thread{
            repo.getAll()
        }
    }
}