package com.g30lab3.app.models

class user {
    var id:String = ""
    var full_name:String = ""
    var nickname: String = ""
    var description:String = ""
    var skills: MutableList<String> = mutableListOf()
    var location: String = ""
    var mail:String = ""

    constructor(
        id: String,
        full_name: String,
        nickname: String,
        description: String,
        skills: MutableList<String>,
        location: String,
        mail: String
    ) {
        this.id = id
        this.full_name = full_name
        this.nickname = nickname
        this.description = description
        this.skills = skills
        this.location = location
        this.mail = mail
    }

}