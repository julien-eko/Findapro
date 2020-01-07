package com.julien.findapro.Utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Assignment {

    @get:ServerTimestamp
    var dateCreated: Date? = null
    var dateEnd: Date? = null
    var userId: String? = null
    var proUserId: String? = null
    var status:String? = null
    var describe:String? = null
    var dateAssignment: Date? = null

    constructor() {}

    constructor(userId: String?, proUserId: String?, status: String,describe:String,dateEnd: Date?) {
        this.userId = userId
        this.proUserId = proUserId
        this.status = status
        this.describe = describe
        this.dateEnd = dateEnd
        this.dateAssignment = null
    }
}