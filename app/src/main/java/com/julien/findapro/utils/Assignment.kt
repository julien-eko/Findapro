package com.julien.findapro.utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Assignment(@get:ServerTimestamp
                 var dateCreated: Date? = null,
                 var dateEnd: Date? = null,
                 var userId: String? = null,
                 var proUserId: String? = null,
                 var status:String? = null,
                 var describe:String? = null,
                 var dateAssignment: Date? = null)

