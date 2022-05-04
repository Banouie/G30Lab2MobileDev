package com.g30lab3.app.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [timeSlot::class], version = 1)
abstract class timeSlotDatabase: RoomDatabase() {
    abstract fun timeSlotDao(): timeSlotDao

    companion object {
        @Volatile
        private var INSTANCE: timeSlotDatabase? = null

        fun getDatabase(context:Context): timeSlotDatabase =
            (
                    INSTANCE?:
                    synchronized(this) {
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            timeSlotDatabase::class.java,
                            "timeSlots"
                        ).build()
                        INSTANCE = i
                        INSTANCE
                    }
                    )!!


    }

}