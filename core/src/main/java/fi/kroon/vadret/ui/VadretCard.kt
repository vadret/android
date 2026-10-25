package fi.kroon.vadret.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val cardBorderWidth = 1.dp

@Composable
fun VadretCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = VadretTheme.shapes.large,
        color = VadretTheme.colorScheme.surface,
        border = BorderStroke(
            width = cardBorderWidth,
            color = VadretTheme.colors.cardBorder
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}