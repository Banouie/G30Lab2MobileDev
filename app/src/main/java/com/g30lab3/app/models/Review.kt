package com.g30lab3.app.models

import java.util.*


class Review{
    var writerUser: String = ""
    var valuedUser: String = ""
    var forRequest : String = "" //id of the request associated with this review
    var valuedUserIsOfferer : Boolean = false
    var ratingReview: Float = 0f
    var commentReview: String = ""
    var date: Date = Date(0)


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
        this.date = Date() //set the creation of review at the moment of creation
    }

    constructor(
        writerUser: String,
        valuedUser: String,
        forRequest: String,
        valuedUserIsOfferer: Boolean,
        ratingReview: Float,
        commentReview: String,
        date: Date
    ) {
        this.writerUser = writerUser
        this.valuedUser = valuedUser
        this.forRequest = forRequest
        this.valuedUserIsOfferer = valuedUserIsOfferer
        this.ratingReview = ratingReview
        this.commentReview = commentReview
        this.date = date
    }


}