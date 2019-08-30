package fi.kroon.vadret.data.aboutinfo.local

import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

@CoreApplicationScope
class AboutInfoLocalDataSource @Inject constructor() {

    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        listOf(
            AboutInfo(
                iconResourceId = R.drawable.ic_info_outline,
                titleResourceId = R.string.version_row_title,
                hintResourceId = R.string.app_version,
                urlResourceId = R.string.app_release_page
            ),
            AboutInfo(
                iconResourceId = R.drawable.ic_copyright,
                titleResourceId = R.string.license_row_title,
                hintResourceId = R.string.license_row_hint,
                urlResourceId = R.string.app_license_url
            ),
            AboutInfo(
                iconResourceId = R.drawable.ic_history,
                titleResourceId = R.string.changelog_row_title,
                urlResourceId = R.string.app_github_changelog_url
            ),
            AboutInfo(
                iconResourceId = R.drawable.ic_code,
                titleResourceId = R.string.source_code_row_title,
                urlResourceId = R.string.app_github_source_url
            )
        ).asRight()
            .asSingle()
}