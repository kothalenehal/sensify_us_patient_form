package com.sensifyawareapp.utils.common

import android.content.Context
import android.graphics.Typeface
import android.util.ArrayMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * cache for generating typeface from assets
 */
@Singleton
class FontCache @Inject constructor(private val mContext: Context) {
    private val mMapOfTypefaces: MutableMap<String, Typeface?>

    /**
     * generates typeface from font file name given - only if it does not exist in cache.
     *
     *
     * font file must be in assets folder. otherwise specify foll path under assets ex. 'fonts/xyz.ttf'
     */
    fun getTypeface(fontName: String): Typeface? {
        //using app context to avoid leaks
        var typeface: Typeface? = mMapOfTypefaces[fontName]
        if (typeface == null) {
            typeface = try {
                Typeface.createFromAsset(mContext.assets, fontName)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
            mMapOfTypefaces[fontName] = typeface
        }
        return typeface
    }

    init {
        //no direct instances allowed. use di instead.
        //array map is efficient than hash map
        mMapOfTypefaces = ArrayMap<String, Typeface?>()
    }
}