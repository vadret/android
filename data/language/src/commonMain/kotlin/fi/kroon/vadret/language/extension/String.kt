package fi.kroon.vadret.language.extension

import fi.kroon.vadret.language.model.AppLanguage

internal fun String?.toAppLanguage(): AppLanguage = AppLanguage.entries.firstOrNull { language: AppLanguage -> language.name == this } ?: AppLanguage.SYSTEM