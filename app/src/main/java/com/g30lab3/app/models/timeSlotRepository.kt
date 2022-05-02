package com.g30lab3.app.models

import android.app.Application
import androidx.lifecycle.LiveData

class timeSlotRepository(application: Application) {
    private val Dao = timeSlotDatabase.getDatabase(application).timeSlotDao()

    fun add() {
        Dao.insert_timeSLot(timeSlot())
    }

    fun getAll():LiveData<List<timeSlot>>{
        return Dao.findAll()
    }

    fun count(): LiveData<Int> = Dao.count()

}