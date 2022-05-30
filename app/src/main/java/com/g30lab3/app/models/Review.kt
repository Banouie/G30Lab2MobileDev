package com.g30lab3.app.models

import android.content.BroadcastReceiver

class Review{
    var id: String =""
    var senderUserId: String = ""
    var receiverUserId: String = ""
    var ratingReview: Float = 0F
    var commentReview: String = ""

    constructor(
        reviewId:String,
        senderUserId: String,
        receiverUserId: String,
        ratingReview: Float,
        commentReview: String
    ) {
        this.id = reviewId
        this.senderUserId = senderUserId
        this.receiverUserId = receiverUserId
        this.ratingReview = ratingReview
        this.commentReview = commentReview
    }
}


