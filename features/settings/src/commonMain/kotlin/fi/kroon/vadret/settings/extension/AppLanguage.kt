package fi.kroon.vadret.settings.extension

import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.language_danish
import fi.kroon.vadret.core.resources.language_english
import fi.kroon.vadret.core.resources.language_finnish
import fi.kroon.vadret.core.resources.language_french
import fi.kroon.vadret.core.resources.language_german
import fi.kroon.vadret.core.resources.language_norwegian
import fi.kroon.vadret.core.resources.language_polish
import fi.kroon.vadret.core.resources.language_swedish
import fi.kroon.vadret.core.resources.system_language
import fi.kroon.vadret.language.model.AppLanguage
import org.jetbrains.compose.resources.StringResource

internal fun AppLanguage.labelResource(): StringResource =
    when (this) {
        AppLanguage.SYSTEM -> Res.string.system_language
        AppLanguage.ENGLISH -> Res.string.language_english
        AppLanguage.SWEDISH -> Res.string.language_swedish
        AppLanguage.DANISH -> Res.string.language_danish
        AppLanguage.GERMAN -> Res.string.language_german
        AppLanguage.FINNISH -> Res.string.language_finnish
        AppLanguage.FRENCH -> Res.string.language_french
        AppLanguage.NORWEGIAN -> Res.string.language_norwegian
        AppLanguage.POLISH -> Res.string.language_polish
    }