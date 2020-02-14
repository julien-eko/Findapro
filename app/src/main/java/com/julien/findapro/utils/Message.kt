package com.julien.findapro.utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

//message in chat
class Message(
    var message: String? = null,
    @get:ServerTimestamp
    var dateCreated: Date? = null,
    var userSender: String? = null,
    var urlImageSender: String? = null,
    var urlImageMessage: String? = null
)