package fi.kroon.vadret.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import javax.inject.Inject

class NetworkHandler @Inject constructor(private val context: Context) {

    // networkInfo?.isConnected guarantees network conn. is fully usable
    val isConnected get() = context.networkInfo?.isConnected
}

val Context.networkInfo: NetworkInfo? get() =
    (
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ).activeNetworkInfo