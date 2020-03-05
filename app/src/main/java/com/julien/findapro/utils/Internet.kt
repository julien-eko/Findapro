package com.julien.findapro.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


//check internet connexion
class Internet {

    companion object {


        fun isInternetAvailable(context: Context?): Boolean {
            var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            result = 2
                        } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            result = 1
                        }
                    }
                }
            } else {
                // < 23 5Android M)
                result = checkInternetConexionAndroidMOrLess(cm)

            }
            return result != 0
        }

        @Suppress("DEPRECATION")
        fun checkInternetConexionAndroidMOrLess(cm:ConnectivityManager?):Int{
            var result = 0
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = 2
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = 1
                    }
                }
            }
            return  result
        }
    }


}