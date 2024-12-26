package com.sensifyawareapp.ui.scentaware

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

class KeyboardObserver(private val activity: Activity, private val onKeyboardStateChangeListener: OnKeyboardStateChangeListener) {

    private val rootView: View = activity.window.decorView.rootView
    private var isKeyboardOpen = false

    init {
        observeKeyboard()
    }

    private fun observeKeyboard() {
        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            private val visibleThreshold = 100

            override fun onPreDraw(): Boolean {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)

                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom

                // Check if the keyboard is open
                if (keypadHeight > visibleThreshold) {
                    if (!isKeyboardOpen) {
                        isKeyboardOpen = true
                        onKeyboardStateChangeListener.onKeyboardOpened()
                    }
                } else {
                    if (isKeyboardOpen) {
                        isKeyboardOpen = false
                        onKeyboardStateChangeListener.onKeyboardClosed()
                    }
                }

                return true
            }
        })
    }

    interface OnKeyboardStateChangeListener {
        fun onKeyboardOpened()
        fun onKeyboardClosed()
    }
}
