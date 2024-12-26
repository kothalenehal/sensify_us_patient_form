package com.sensifyawareapp.ui

import android.app.Application
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.api.ApiManager
import com.sensifyawareapp.utils.NetworkUtils
import com.sensifyawareapp.utils.PrefUtils

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var throwable: MutableLiveData<Throwable> = MutableLiveData()
    var tempClick: MutableLiveData<Boolean> = MutableLiveData()

    var apiManager: ApiManager = SensifyAwareApplication.getAppComponent().provideApiManager()
    var prefUtils: PrefUtils = SensifyAwareApplication.getAppComponent().providePrefUtil()
}


@BindingAdapter("app:tint")
fun setAppTint(view: ImageView, tintColor: Int) {
    ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(tintColor));
}


@BindingAdapter("android:layout_margin")
fun setMargin(view: View, margin: Float) {
    val layoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        Math.round(margin), Math.round(margin),
        Math.round(margin), Math.round(margin)
    )
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("android:layout_marginRight")
fun setMarginEnd(view: View, margin: Float) {
    val layoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.leftMargin, layoutParams.leftMargin,
        Math.round(margin), layoutParams.leftMargin
    )
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("android:layout_marginTop")
fun setMarginTop(view: View, margin: Float) {
    val layoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.leftMargin, Math.round(margin),
        layoutParams.leftMargin, layoutParams.leftMargin
    )
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("android:layout_marginEnd")
fun ViewGroup.setMarginEndValue(marginValue: Float) =
    (layoutParams as ViewGroup.MarginLayoutParams).apply { rightMargin = marginValue.toInt() }

@BindingAdapter("android:layout_marginBottom")
fun setMarginBottom(view: View, margin: Float) {
    val layoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.leftMargin, layoutParams.leftMargin,
        layoutParams.leftMargin, Math.round(margin)
    )
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("android:layout_marginVertical")
fun setMarginVertical(view: View, margin: Float) {
    val layoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.leftMargin, Math.round(margin),
        layoutParams.rightMargin, Math.round(margin)
    )
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("android:layout_marginHorizontal")
fun setMarginHorizontal(view: View, margin: Float) {
    val layoutParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        Math.round(margin), layoutParams.topMargin,
        Math.round(margin), layoutParams.bottomMargin
    )
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("app:indicatorColor")
fun setIndicatorColor(view: LinearProgressIndicator, color: Int) {
    view.setIndicatorColor(color)

}