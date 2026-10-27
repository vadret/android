package fi.kroon.vadret.ui

import androidx.compose.runtime.Composable
import fi.kroon.vadret.ui.model.ElapsedTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun elapsedTimeText(elapsedTime: ElapsedTime): String {
    val text: String = stringResource(resource = elapsedTime.resource)
    val count: Long? = elapsedTime.count
    return if (count != null) {
        "$count $text"
    } else {
        text
    }
}