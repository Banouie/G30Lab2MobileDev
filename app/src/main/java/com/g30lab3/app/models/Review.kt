package com.g30lab3.app.models


class Review{
    var writerUser: String = ""
    var valuedUser: String = ""
    var forRequest : String = "" //id of the request associated with this review
    var valuedUserIsOfferer : Boolean = false
    var ratingReview: Float = 0F
    var commentReview: String = ""


    constructor(
        writerUser: String,
        valuedUser: String,
        forRequest : String,
        valuedUserIsOfferer: Boolean,
        ratingReview: Float,
        commentReview: String
    ) {
        this.writerUser = writerUser
        this.valuedUser = valuedUser
        this.forRequest = forRequest
        this.valuedUserIsOfferer = valuedUserIsOfferer
        this.ratingReview = ratingReview
        this.commentReview = commentReview

    }
}