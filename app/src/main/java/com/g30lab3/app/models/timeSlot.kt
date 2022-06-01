package com.g30lab3.app.models

enum class TimeSlotStatus{
    AVAILABLE,UNAVAILABLE
}

class timeSlot {

    var id: String = ""
    var title: String = ""
    var description: String = ""
    var date: String = ""
    var time: String = ""
    var duration: Int = 0
    var location: String = ""
    var author: String = ""
    var skill: String = ""
    var status: TimeSlotStatus = TimeSlotStatus.AVAILABLE

    //secondary constructor
    constructor(
        id: String,
        title: String,
        description: String,
        date: String,
        time: String,
        duration: Int,
        location: String,
        author: String,
        skill: String
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.date = date
        this.time = time
        this.duration = duration
        this.location = location
        this.author = author
        this.skill = skill
        this.status = TimeSlotStatus.AVAILABLE
    }

    constructor(
        id: String,
        title: String,
        description: String,
        date: String,
        time: String,
        duration: Int,
        location: String,
        author: String,
        skill: String,
        status: TimeSlotStatus
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.date = date
        this.time = time
        this.duration = duration
        this.location = location
        this.author = author
        this.skill = skill
        this.status = status
    }

    fun copy(origin: timeSlot) {
        this.id = origin.id
        this.title = origin.title
        this.description = origin.description
        this.date = origin.date
        this.time = origin.time
        this.duration = origin.duration
        this.location = origin.location
        this.author = origin.author
        this.skill = origin.skill
        this.status = origin.status
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
        "{TimeSLot $id -> Title: $title, Description: $description, Date: $date, Time: $time, Duration: $duration, Location: $location, Skill: $skill\n }"
}