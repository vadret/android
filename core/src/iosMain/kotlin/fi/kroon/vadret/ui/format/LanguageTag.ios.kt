package fi.kroon.vadret.ui.format

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

private const val IDENTIFIER_SEPARATOR = '_'
private const val TAG_SEPARATOR = '-'

actual fun currentLanguageTag(): String =
    NSLocale.currentLocale.localeIdentifier.replace(
        oldChar = IDENTIFIER_SEPARATOR,
        newChar = TAG_SEPARATOR
    )