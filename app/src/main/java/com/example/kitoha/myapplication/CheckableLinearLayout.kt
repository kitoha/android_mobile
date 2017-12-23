package com.example.kitoha.myapplication

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.Checkable
import android.widget.LinearLayout
import java.util.jar.Attributes

/**
 * Created by kitoha on 2017-12-10.
 */
class CheckableLinearLayout : LinearLayout, Checkable {

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP) constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun isChecked(): Boolean {
        val cb:CheckBox=findViewById(R.id.chkbox)
        return cb.isChecked

    }

    override fun toggle() {
        val cb:CheckBox=findViewById(R.id.chkbox)
        isChecked
    }

    override fun setChecked(checked: Boolean) {
        val cb:CheckBox=findViewById(R.id.chkbox)
        if(cb.isChecked!=checked) {
            cb.setChecked(checked)
        }
    }
}