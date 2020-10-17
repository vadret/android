package fi.kroon.vadret.presentation.aboutapp.extension

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