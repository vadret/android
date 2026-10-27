package fi.kroon.vadret.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.cancel
import fi.kroon.vadret.core.resources.select_a_theme
import fi.kroon.vadret.settings.extension.labelResource
import fi.kroon.vadret.theme.model.Theme
import fi.kroon.vadret.ui.VadretTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ThemeDialog(
    selectedTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(resource = Res.string.select_a_theme)) },
        text = {
            Column {
                Theme.entries.forEach { theme: Theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = theme == selectedTheme,
                                role = Role.RadioButton,
                                onClick = { onThemeSelected(theme) }
                            )
                            .padding(vertical = VadretTheme.spacing.small)
                    ) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = null
                        )
                        Text(
                            text = stringResource(resource = theme.labelResource()),
                            style = VadretTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = VadretTheme.spacing.medium)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(resource = Res.string.cancel))
            }
        },
        modifier = modifier
    )
}