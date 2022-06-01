package com.g30lab3.app.models

import java.util.*

enum class Status {
    ACCEPTED,PENDING,DECLINED
}

class PendingRequestInfo{
    var chatId: String =""
    var authorOfTimeSlot: String = ""
    var requestingUser: String = ""
    var leadingTimeSlot: String = ""
    var status : Status = Status.PENDING


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
        this.status = Status.PENDING //when a request is created is always pending
    }

    //this is used to build a PendingRequestInfo retrieved from Firebase
    constructor(
        chatId: String,
        authorOfTimeSlot: String,
        requestingUser: String,
        leadingTimeSlot: String,
        status: Status
    ) {
        this.chatId = chatId
        this.authorOfTimeSlot = authorOfTimeSlot
        this.requestingUser = requestingUser
        this.leadingTimeSlot = leadingTimeSlot
        this.status = status
    }


    override fun toString(): String {
        return "ReqUsr: ${this.requestingUser}, AuthUsr: ${this.authorOfTimeSlot}, TS: ${this.leadingTimeSlot}, Status: ${this.status}"
    }
}