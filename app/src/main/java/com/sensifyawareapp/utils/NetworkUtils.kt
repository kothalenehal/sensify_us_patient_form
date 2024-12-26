package com.sensifyawareapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * <br></br> Description: 网络状态工具类
 *
 *
 * <br></br> Created by shixinzhang on 17/4/26.
 *
 *
 * <br></br> Email: shixinzhang2016@gmail.com
 *
 *
 * [About me](https://about.me/shixinzhang)
 */
@Singleton
class NetworkUtils //no direct instances allowed. use di instead.
@Inject constructor(private val mContext: Context) {
    //region public methods
    /**
     * Checks if device is connected on network
     */
    val isConnected: Boolean
        get() {
            val info: NetworkInfo? = networkInfo
            return info != null && info.isConnected()
        }

    /**
     * Checks if there is slow connectivity
     * returns true in cases when there is 2g connection < 100kbPS
     */
    val isConnectedOnSlowNetwork: Boolean
        get() {
            val info: NetworkInfo? = networkInfo
            if (info == null || !info.isConnected()) {
                Log.w("--", "isConnectedOnSlowNetwork : device not connected to network. Check with isConnected() first!")
                return true
            }
            return !hasFastConnection(info.getType(), info.getSubtype())
        }
    //endregion
    //region private internal methods
    /**
     * Get the network info
     */
    private val networkInfo: NetworkInfo?
        private get() {
            val connectivityManager: ConnectivityManager =
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.getActiveNetworkInfo()
        }

    /**
     * Check if the connection is fast
     * returns true if connected on wifi
     * for mobile, it returns false for 2G connections (< 100 kbps)
     */
    private fun hasFastConnection(type: Int, subType: Int): Boolean {
        return if (type == ConnectivityManager.TYPE_WIFI) {
            true
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            when (subType) {
                TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> {
                    Log.w("---", "hasFastConnection : Unrecognized sub type of network. Returning false")
                    false
                }
                else -> {
                    Log.w("---","hasFastConnection : Unrecognized sub type of network. Returning false")
                    false
                }
            }
        } else {
            false
        }
    } //endregion
}