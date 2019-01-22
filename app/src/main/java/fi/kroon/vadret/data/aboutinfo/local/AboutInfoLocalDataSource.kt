package fi.kroon.vadret.data.aboutinfo.local

import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.di.scope.VadretApplicationScope
import io.reactivex.Single
import javax.inject.Inject

@VadretApplicationScope
class AboutInfoLocalDataSource @Inject constructor() {

    fun get(): Single<Either<Failure, List<AboutInfoEntity>>> = Single.fromCallable {
        Either.Right(
            listOf(
                AboutInfoEntity(
                    iconResourceId = R.drawable.ic_info_outline,
                    titleResourceId = R.string.version_row_title,
                    hintResourceId = R.string.app_version,
                    urlResourceId = R.string.app_release_page
                ),
                AboutInfoEntity(
                    iconResourceId = R.drawable.ic_copyright,
                    titleResourceId = R.string.license_row_title,
                    hintResourceId = R.string.license_row_hint,
                    urlResourceId = R.string.app_license_url
                ),
                AboutInfoEntity(
                    iconResourceId = R.drawable.ic_history,
                    titleResourceId = R.string.changelog_row_title,
                    urlResourceId = R.string.app_github_changelog_url
                ),
                AboutInfoEntity(
                    iconResourceId = R.drawable.ic_code,
                    titleResourceId = R.string.source_code_row_title,
                    urlResourceId = R.string.app_github_source_url
                )
            )
        )
    }
}