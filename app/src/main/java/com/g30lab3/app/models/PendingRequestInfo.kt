package com.g30lab3.app.models

import java.util.*

class PendingRequestInfo{
    var chatId: String =""
    var authorOfTimeSlot: String = ""
    var requestingUser: String = ""
    var leadingTimeSlot: String = ""

    constructor(
        chatId: String,
        authorOfTimeSlot: String,
        requestingUser: String,
        leadingTimeSlot: String
    ) {
        this.chatId = chatId
        this.authorOfTimeSlot = authorOfTimeSlot
        this.requestingUser = requestingUser
        this.leadingTimeSlot = leadingTimeSlot
    }

    override fun toString(): String {
        return "ReqUsr: ${this.requestingUser}, AuthUsr: ${this.authorOfTimeSlot}, TS: ${this.leadingTimeSlot}"
    }
}