package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.core.R
import fi.kroon.vadret.ui.VadretTheme
import fi.kroon.vadret.weatherforecast.WeatherForecastEvent
import fi.kroon.vadret.weatherforecast.model.SearchState

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
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        TextField(
            value = search.query,
            onValueChange = { query: String -> onEvent(WeatherForecastEvent.OnSearchQueryChanged(query = query)) },
            placeholder = { Text(text = stringResource(id = R.string.search)) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { onEvent(WeatherForecastEvent.OnSearchDismissed) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_24dp),
                        contentDescription = stringResource(id = R.string.close_search)
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
                HorizontalDivider()
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
            style = VadretTheme.typography.titleLarge
        )
        Text(
            text = "${autoCompleteItem.municipality}, ${autoCompleteItem.county}",
            style = VadretTheme.typography.bodyMedium,
            color = VadretTheme.colorScheme.onSurfaceVariant
        )
    }
}