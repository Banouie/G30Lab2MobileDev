package com.g30lab3.app.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


class timeSlot {

    var id: String = ""
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var time: String = ""
    var duration: Int = 0
    var location: String = ""

    //secondary constructor
    constructor(
        id: String,
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

    fun copy(origin: timeSlot) {
        this.id = origin.id
        this.title = origin.title
        this.description = origin.description
        this.date = origin.date
        this.time = origin.time
        this.duration = origin.duration
        this.location = origin.location
    }

    override fun equals(other: Any?): Boolean {
        //if they have the same memory location are equal
        if (this === other) return true

        //if they have different classes are not equal
        if(other?.javaClass != javaClass) return false

        //if they both belong to the same class check if they have the same ID, if not they are not equal
        other as timeSlot
        if(this.id != other.id) return false

        return true
    }

    override fun toString() =
        "{TimeSLot $id -> Title: $title, Description: $description, Date: $date, Time: $time, Duration: $duration, Location: $location\n }"
}