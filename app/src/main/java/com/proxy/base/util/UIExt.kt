package com.proxy.base.util

import android.content.res.ColorStateList
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.proxy.base.R
import java.util.concurrent.atomic.AtomicBoolean

fun EditText.passwordStyle(toggleView: View, defShow: Boolean = false) {
    inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    val showPwd = AtomicBoolean(defShow)
    transformationMethod = if (showPwd.get()) {
        HideReturnsTransformationMethod.getInstance()
    } else {
        PasswordTransformationMethod.getInstance()
    }
    toggleView.setOnClickListener {
        transformationMethod = if (showPwd.get()) {
            PasswordTransformationMethod.getInstance()
        } else {
            HideReturnsTransformationMethod.getInstance()
        }
        showPwd.set(!showPwd.get())
        toggleView.isSelected = showPwd.get()
        setSelection(this.length())
    }
}

fun TextView.tintColor(@ColorInt color: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(color))
}

fun TextView.tint(@ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(context, colorRes)
    tintColor(color)
}