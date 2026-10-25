package fi.kroon.vadret.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.kroon.vadret.ui.model.ElapsedTime

@Composable
fun elapsedTimeText(elapsedTime: ElapsedTime): String {
    val text: String = stringResource(id = elapsedTime.resourceId)
    val count: Long? = elapsedTime.count
    return if (count != null) {
        "$count $text"
    } else {
        text
    }
}