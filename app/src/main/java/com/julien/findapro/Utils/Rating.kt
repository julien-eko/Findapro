package com.julien.findapro.Utils

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Rating(
    val raterId: String? = null,
    val rating:Float? = null,
    val comment:String? = null,
    val assignmentsId:String? = null
) {


}