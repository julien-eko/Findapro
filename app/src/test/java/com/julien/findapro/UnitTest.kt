package com.julien.findapro

import com.julien.findapro.controller.activity.AssignmentDetailActivity
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
        assertEquals("29/01/2020", AssignmentDetailActivity().convertDate(dateRepresentation,false))
        assertEquals("29/01/2020 10:29", AssignmentDetailActivity().convertDate(dateRepresentation,true))
    }
}
