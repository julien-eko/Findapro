package com.julien.findapro

import com.julien.findapro.controller.activity.AssignmentDetailActivity
import com.julien.findapro.controller.activity.RatingActivity
import com.julien.findapro.controller.fragment.ChatListFragment
import com.julien.findapro.model.Assignment
import com.julien.findapro.model.Message
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {


    @Test
    fun dateFormat() {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = 2020
        cal[Calendar.MONTH] = Calendar.JANUARY
        cal[Calendar.DAY_OF_MONTH] = 29
        cal[Calendar.HOUR_OF_DAY] = 10
        cal[Calendar.MINUTE] = 29
        val dateRepresentation = cal.time
        assertEquals(
            "29/01/2020",
            AssignmentDetailActivity().convertDate(dateRepresentation, false)
        )
        assertEquals(
            "29/01/2020 10:29",
            AssignmentDetailActivity().convertDate(dateRepresentation, true)
        )
    }

    @Test
    fun sortListByDate() {
        val list: ArrayList<HashMap<String, Any?>> = ArrayList()

        val cal1 = Calendar.getInstance()
        cal1[Calendar.YEAR] = 2019
        val date1 = cal1.time

        val cal2 = Calendar.getInstance()
        cal2[Calendar.YEAR] = 2020
        val date2 = cal2.time

        val cal3 = Calendar.getInstance()
        cal3[Calendar.YEAR] = 2018
        val date3 = cal3.time

        val first: HashMap<String, Any?> = hashMapOf(
            "id" to 1,
            "createdDate" to date1
        )

        val second: HashMap<String, Any?> = hashMapOf(
            "id" to 2,
            "createdDate" to date2
        )

        val third: HashMap<String, Any?> = hashMapOf(
            "id" to 3,
            "createdDate" to date3
        )

        list.add(first)
        list.add(second)
        list.add(third)

        //before sort
        assertEquals(list[0]["id"], 1)
        assertEquals(list[1]["id"], 2)
        assertEquals(list[2]["id"], 3)


        val sortList: List<HashMap<String, Any?>> = ChatListFragment().sortListByDate(list)
        //after sort

        //2020
        assertEquals(2, sortList[0]["id"])
        //2019
        assertEquals(1, sortList[1]["id"])
        //2018
        assertEquals(3, sortList[2]["id"])

    }


    @Test
    fun calculateAvrage() {
        val result = RatingActivity().calculateRate(10.0, 2.0, 10.0)
        assertEquals(10.0, result, 0.0)
    }

    @Test
    fun updateStatusAssignment() {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = 2020
        cal[Calendar.MONTH] = Calendar.JANUARY
        cal[Calendar.DAY_OF_MONTH] = 29
        cal[Calendar.HOUR_OF_DAY] = 10
        cal[Calendar.MINUTE] = 29
        val dateRepresentation = cal.time
        val assignment =
            Assignment(dateRepresentation, null, "myId", "proId", "pending", "mission test", null)

        //update satatus
        assignment.status = "refuse"
        assignment.dateEnd = dateRepresentation

        //check update

        assertEquals("refuse", assignment.status)
        assertEquals(dateRepresentation, assignment.dateEnd)
    }

    @Test
    fun createMessage() {
        val messageWithoutImage = Message("Test", null, "me", "myUrlImage", "noImage")
        val messageWithImage = Message("Test", null, "me", "myUrlImage", "urlImage")

        assertEquals("noImage", messageWithoutImage.urlImageMessage)
        assertEquals("urlImage", messageWithImage.urlImageMessage)
    }
}
