package com.g30lab3.app.models


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface timeSlotDao {

    @Query("SELECT * from timeSlots")
    fun findAll() : LiveData<List<timeSlot>>

    @Query("SELECT count() from timeSlots")
    fun count(): LiveData<Int>

    @Insert
    fun insert_timeSLot(timeSlot: timeSlot)

    @Query("DELETE FROM timeSlots")
    fun deleteAll()

    @Query("SELECT * FROM timeSlots WHERE id=:id")
    fun getUsingId(id:Int): LiveData<timeSlot>
}