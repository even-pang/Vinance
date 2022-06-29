package com.project.vinance.view.sub

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.tabs.TabLayout

class MyTabLayout(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int? = null): TabLayout(context,attributeSet) {
    companion object {
        private val TAG: String = MyTabLayout::class.java.simpleName
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        Log.d(TAG, "$l, $t, $oldl, $oldt")
    }
}