package com.julien.findapro.Utils

interface Communicator {

    fun passDataUserList(job:String,maxDistance:Float,rating:Double)

    fun passDataAssignmentList(maxDistance:Float,rating:Double)

    fun passDataAssignmentInProgressList(status:String)
}