package com.julien.findapro


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.julien.findapro.utils.Internet
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {



    @Test
    fun checkInternetConnexion() {
        val context = InstrumentationRegistry.getInstrumentation().context

        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1
                }
            }
        }
        val internetConnexion: Boolean
        internetConnexion = result != 0

        assertEquals(Internet.isInternetAvailable(context), internetConnexion)

    }


}
