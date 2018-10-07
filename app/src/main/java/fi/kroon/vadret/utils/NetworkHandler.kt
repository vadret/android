package fi.kroon.vadret.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import javax.inject.Inject

class NetworkHandler @Inject constructor(private val context: Context) {
    val isConnected: Boolean get() = context.networkInfo?.isConnected == true
}

private val Context.networkInfo: NetworkInfo?
    get() = (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo