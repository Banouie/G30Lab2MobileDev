package com.g30lab3.app.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "timeSlots")
class timeSlot {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var time: String = ""
    var duration: Int = 0
    var location: String = ""

    //secondary constructor
    constructor(
        id: Int,
        title: String,
        description: String,
        date: String,
        time: String,
        duration: Int,
        location: String
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.date = date
        this.time = time
        this.duration = duration
        this.location = location
    }

    override fun toString() = "{TimeSLot $id -> Title: $title, Description: $description, Date: $date, Time: $time, Duration: $duration, Location: $location\n }"
}