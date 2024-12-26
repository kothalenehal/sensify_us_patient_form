package com.sensifyawareapp.utils

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(private val typeface: Typeface) : TypefaceSpan("") {

    override fun updateDrawState(ds: TextPaint) {
        ds.typeface = typeface
        // You can also customize other text properties like color, size, etc. here
    }

    override fun updateMeasureState(paint: TextPaint) {
        paint.typeface = typeface
        // You can also customize other text properties like color, size, etc. here
    }
}