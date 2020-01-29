package com.julien.findapro.Utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

//message
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

    //message without image
    constructor(message: String?, urlImageSender: String?, userSender: String) {
        this.message = message
        this.urlImageSender = urlImageSender
        this.userSender = userSender
    }

    //message with image
    constructor(message: String?, urlImageSender: String?, userSender: String,urlImageMessage:String) {
        this.message = message
        this.urlImageSender = urlImageSender
        this.userSender = userSender
        this.urlImageMessage=urlImageMessage
    }

}