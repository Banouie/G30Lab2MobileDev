package com.g30lab3.app.models

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class timeSlotRepository(application: Application) {

    //get the DAO object of timeSlot Database
    private val Dao = timeSlotDatabase.getDatabase(application).timeSlotDao()

    //Methods used from the app to interact with the DB throughout the Repository:

    /*Add a timeSlot to the local DB*/
    fun add(timeSlot: timeSlot) {
        Dao.insert_timeSLot(timeSlot)
    }

    /*Retrive all timeSlots in the DB*/
    fun getAll(): MutableLiveData<List<timeSlot>> {
        return Dao.findAll()
    }


    /*Delete all from DB*/
    fun clear(){
        Dao.deleteAll()
    }



}