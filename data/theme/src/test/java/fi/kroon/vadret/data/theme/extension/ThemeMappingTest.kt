package fi.kroon.vadret.data.theme.extension

import fi.kroon.vadret.data.theme.model.Theme
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeMappingTest {

    @Test
    fun `every theme survives a round trip through preferences`() {
        Theme.entries.forEach { theme: Theme ->
            assertEquals(
                expected = theme,
                actual = theme.toPreferenceValue().toTheme()
            )
        }
    }

    @Test
    fun `an unknown stored value falls back to light`() {
        assertEquals(
            expected = Theme.LIGHT,
            actual = "None".toTheme()
        )
    }
}