package fi.kroon.vadret.utils.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import timber.log.Timber

/**
 *  Given color attribute returns the color ref
 */
@ColorInt
fun Context.getThemeColorAttr(@AttrRes attr: Int): Int = TypedValue().let { typedValue: TypedValue ->
    theme.resolveAttribute(attr, typedValue, true)
    Timber.d("Attr: $attr, Data: ${typedValue.data}")
    Timber.d("resourceId: ${typedValue.resourceId}")
    typedValue.resourceId
}