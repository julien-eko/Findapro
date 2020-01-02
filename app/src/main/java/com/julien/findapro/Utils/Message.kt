package com.julien.findapro.Utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


class Message {
    var message: String? = null
    @get:ServerTimestamp
    var dateCreated: Date? = null
    var userSender: String? = null
    var urlImageSender: String? = null
    var urlImageMessage:String? = null

    constructor() {}
    constructor(message: String?, userSender: String) {
        this.message = message
        this.userSender = userSender
    }

    constructor(message: String?, urlImageSender: String?, userSender: String) {
        this.message = message
        this.urlImageSender = urlImageSender
        this.userSender = userSender
    }

    constructor(message: String?, urlImageSender: String?, userSender: String,urlImageMessage:String) {
        this.message = message
        this.urlImageSender = urlImageSender
        this.userSender = userSender
        this.urlImageMessage=urlImageMessage
    }

}