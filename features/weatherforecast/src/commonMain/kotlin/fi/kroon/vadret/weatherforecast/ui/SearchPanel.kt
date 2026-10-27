package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.add_place
import fi.kroon.vadret.core.resources.close_search
import fi.kroon.vadret.core.resources.ic_close
import fi.kroon.vadret.ui.VadretCard
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.WeatherForecastEvent
import fi.kroon.vadret.weatherforecast.model.SearchState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchPanel(
    search: SearchState,
    onEvent: (WeatherForecastEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
    VadretCard(modifier = modifier) {
        TextField(
            value = search.query,
            onValueChange = { query: String -> onEvent(WeatherForecastEvent.OnSearchQueryChanged(query = query)) },
            placeholder = { Text(text = stringResource(resource = Res.string.add_place)) },
            textStyle = VadretTheme.textStyles.body,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { onEvent(WeatherForecastEvent.OnSearchDismissed) }) {
                    Icon(
                        painter = painterResource(resource = Res.drawable.ic_close),
                        contentDescription = stringResource(resource = Res.string.close_search)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onEvent(WeatherForecastEvent.OnSearchSubmitted) }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester)
        )
        search.suggestions.forEachIndexed { index: Int, autoCompleteItem: AutoCompleteItem ->
            if (index > 0) {
                HorizontalDivider(color = VadretTheme.colors.divider)
            }
            SuggestionRow(
                autoCompleteItem = autoCompleteItem,
                onClick = { onEvent(WeatherForecastEvent.OnSuggestionClicked(autoCompleteItem = autoCompleteItem)) }
            )
        }
    }
}

@Composable
private fun SuggestionRow(
    autoCompleteItem: AutoCompleteItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = VadretTheme.spacing.large,
                vertical = VadretTheme.spacing.medium
            )
    ) {
        Text(
            text = autoCompleteItem.locality,
            style = VadretTheme.textStyles.value,
            color = VadretTheme.colorScheme.onSurface
        )
        Text(
            text = "${autoCompleteItem.municipality}, ${autoCompleteItem.county}",
            style = VadretTheme.textStyles.detail,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}