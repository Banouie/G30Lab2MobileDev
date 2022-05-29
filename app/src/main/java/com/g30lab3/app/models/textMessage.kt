package com.g30lab3.app.models

import java.util.*

class textMessage{
    var text: String = ""
    var time: Date = Date(0)
    var senderId: String = ""
    var request: Boolean = false

    constructor(text: String, time: Date, senderId: String, request: Boolean) {
        this.text = text
        this.time = time
        this.senderId = senderId
        this.request = request
    }


}