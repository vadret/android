package fi.kroon.vadret.settings.extension

import android.os.Handler
import android.view.View

fun View.onClickThrottled(skipDurationMillis: Long = 750, action: () -> Unit) {
    var isEnabled = true
    this.setOnClickListener {
        if (isEnabled) {
            action()
            isEnabled = false
            Handler().postDelayed({ isEnabled = true }, skipDurationMillis)
        }
    }
}