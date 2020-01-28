package com.julien.findapro.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


class Internet {

    companion object{
        fun isInternetAvailable(context: Context): Boolean? {
            var connected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connected =
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state == NetworkInfo.State.CONNECTED
                ) {
                    true
                } else false
            return connected
        }
    }


}