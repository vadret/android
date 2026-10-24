package fi.kroon.vadret.util.extension

import android.content.Context
import android.content.res.TypedArray

fun Context.getAttribute(attributeId: Int): Int {
    val attr: TypedArray = obtainStyledAttributes(intArrayOf(attributeId))
    val colorResourceId: Int = attr.getResourceId(0, attributeId)
    attr.recycle()
    return colorResourceId
}