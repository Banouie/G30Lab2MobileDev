package com.g30lab3.app.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "timeSlots")
class timeSlot {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String = "Empty title"
    var description: String = "Description"
    var date:String = "1/1/2000"
    var time:String = "12:00"
    var duration: Int = 0
    var location: String = "Nowhere"

    override fun toString() = "{TimeSLot $id }"
}