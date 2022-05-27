package com.g30lab3.app.models

import java.util.*

class chatInfo{
    var authorOfTimeSlot: String = ""
    var requestingUser: String = ""
    var leadingTimeSlot: String = ""

    constructor(authorOfTimeSlot: String, requestingUser: String, leadingTimeSlot: String) {
        this.authorOfTimeSlot = authorOfTimeSlot
        this.requestingUser = requestingUser
        this.leadingTimeSlot = leadingTimeSlot
    }
}