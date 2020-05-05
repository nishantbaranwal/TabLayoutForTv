package com.example.customtablayout

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.custom_tab.view.*


class CustomTabLayout(context: Context,attributeSet: AttributeSet?=null): TabLayout(context,attributeSet) {
    private var focusedColor: Int
    private var clickedColor: Int
    private var pressedTab: Int? = null
    private var defaultColor: Int
    private var lastFocusedTabPosition:Int? = null
    private var indicatorColor:Int ?= null
    init{
        val ta =
            context.obtainStyledAttributes(attributeSet, R.styleable.CustomTabLayout, 0, 0)
        try {
            focusedColor = ta.getColor(R.styleable.CustomTabLayout_focusedTabColor, Color.TRANSPARENT)
            clickedColor = ta.getColor(R.styleable.CustomTabLayout_clickedTabColor, Color.TRANSPARENT)
            defaultColor = ta.getColor(R.styleable.CustomTabLayout_defaultTabColor, Color.BLACK)
        } finally {
            ta.recycle()
        }
    }

    fun addTab(tabName:String?, tabIconId:Int?, clickEvent:Any, notificationCount:Int){
        var mTab:Tab?=null

        if(tabName!=null && tabIconId!=null){
            val drawable = ContextCompat.getDrawable(context,tabIconId)
            drawable!!.colorFilter = getColorFilter(defaultColor)
            mTab = newTab().setIcon(drawable).setText(tabName)
            (mTab.view.getChildAt(1) as TextView).setTextColor(defaultColor)
        }
        else
            if(tabName!=null){
                mTab = newTab().setText(tabName)
                (mTab.view.getChildAt(1) as TextView).setTextColor(defaultColor)
            }
        else
            if(tabIconId!=null){
                val drawable = ContextCompat.getDrawable(context,tabIconId)
                drawable!!.colorFilter = getColorFilter(defaultColor)
                mTab = newTab().setIcon(drawable)
            }

        mTab!!.view.setOnClickListener {
            ((context as ContextWrapper).baseContext as MainActivity).replace(clickEvent)
            if(pressedTab!=null && getTabAt(pressedTab!!)!!.icon!= null) {
                getTabAt(pressedTab!!)!!.icon!!.colorFilter = getColorFilter(defaultColor)
            }
            if(pressedTab!=null ){
                (getTabAt(pressedTab!!)!!.view.getChildAt(1) as TextView).setTextColor(defaultColor)
            }
            pressedTab = mTab.position

            if(mTab.icon!=null)
                mTab.icon!!.colorFilter = getColorFilter(clickedColor)
            (mTab.view.getChildAt(1) as TextView).setTextColor(clickedColor)
        }

        if(notificationCount>0) {
            val badgeDrawable = mTab.orCreateBadge
            badgeDrawable.backgroundColor = Color.RED
            badgeDrawable.isVisible = true
            badgeDrawable.number = notificationCount
        }

        mTab.view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                    lastFocusedTabPosition = mTab.position
                    setSelectedTabIndicatorColor(Color.TRANSPARENT)
                }
            }
            return@setOnKeyListener false
        }

        mTab.view.setOnFocusChangeListener {
                _, hasFocus ->
                val colorFilter: ColorFilter? =
                    if(!hasFocus) {
                        if (mTab.position == pressedTab) {
                            (mTab.view.getChildAt(1) as TextView).setTextColor(clickedColor)
                            getColorFilter(clickedColor)
                        }
                        else {
                            (mTab.view.getChildAt(1) as TextView).setTextColor(defaultColor)
                            getColorFilter(defaultColor)
                        }
                    }
                    else
                    {
                        setSelectedTabIndicatorColor(indicatorColor!!)
                        selectTab(mTab, true)
                        if (mTab.position == pressedTab) {
                            (mTab.view.getChildAt(1) as TextView).setTextColor(clickedColor)
                            getColorFilter(clickedColor)
                        }
                        else {
                            (mTab.view.getChildAt(1) as TextView).setTextColor(focusedColor)
                            getColorFilter(focusedColor)
                        }
                    }


            if(mTab.icon!=null)
                    mTab.icon!!.colorFilter = colorFilter
            if(hasFocus){
                if(lastFocusedTabPosition!=null) {
                    selectTab(getTabAt(lastFocusedTabPosition!!))
                    getTabAt(lastFocusedTabPosition!!)!!.view.requestFocus()
                    lastFocusedTabPosition = null
                }
            }

        }
        addTab(mTab)
    }

    fun addTab(s: String, clickEvent:Any, notificationCount:Int) {
        addTab(s,null,clickEvent, notificationCount)
    }

    fun addTab(tabIconId: Int, clickEvent:Any, notificationCount:Int) {
        addTab(null,tabIconId,clickEvent, notificationCount)
    }

    private fun getColorFilter(iColor:Int): ColorFilter {
        val red = (iColor and 0xFF0000) / 0xFFFF
        val green = (iColor and 0xFF00) / 0xFF
        val blue = iColor and 0xFF

        val matrix = floatArrayOf(
            0f, 0f, 0f, 0f, red.toFloat(),
            0f, 0f, 0f, 0f, green.toFloat(),
            0f, 0f, 0f, 0f, blue.toFloat(),
            0f, 0f, 0f, 1f, 0f
        )
        return ColorMatrixColorFilter(matrix)
    }

    fun addTab(resId:Int,notificationCount: String){
        val view = LayoutInflater.from(context).inflate(resId,null, false)
        val mTab = newTab().setCustomView(view)
        view.tv_count.text = notificationCount
        addTab(mTab)
        mTab.view.setOnFocusChangeListener {
                _, hasFocus ->
            val colorFilter: ColorFilter? =
                if (hasFocus) {
                    selectTab(mTab, true)
                    getColorFilter(focusedColor)
                } else {
                    getColorFilter(defaultColor)
                }

            if(mTab.icon!=null)
                mTab.icon!!.colorFilter = colorFilter
        }
    }

    override fun setTabTextColors(textColor: ColorStateList?) {

    }

    override fun setTabTextColors(normalColor: Int, selectedColor: Int) {

    }
    fun createTabs(menuItemList:List<MenuItem>){

        for(menuItem in menuItemList) {
            val res = context.resources.getIdentifier(menuItem.icon, "mipmap", context.packageName)
            if(res!=0) addTab(menuItem.title, res, menuItem.clickAction, menuItem.notificationCount)
            else{ addTab(menuItem.title, menuItem.clickAction, menuItem.notificationCount) }
        }


    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        try {
            if (tabCount == 0) return
            val tabLayout = getChildAt(0) as ViewGroup
            val childCount: Int = tabLayout.childCount
            val widths = IntArray(childCount + 1)
            for (i in 0 until childCount) {
                widths[i] = tabLayout.getChildAt(i).measuredWidth
                widths[childCount] += widths[i]
            }
            val measuredWidth: Int = measuredWidth
            for (i in 0 until childCount) {
                if(((tabLayout.getChildAt(i) as TabView).getChildAt(1) as TextView).text == "") {
                    val layoutParams = LinearLayout.LayoutParams(62, 60)
                    layoutParams.setMargins (0, 5, 0, 5)
                    tabLayout.getChildAt(i).layoutParams = layoutParams
                }
                tabLayout.getChildAt(i).minimumWidth = measuredWidth * widths[i] / widths[childCount]
            }
        } catch (e: Exception) {
//            e.ToString()
        }
        finally {
            if(tabCount>0) {
                getTabAt(tabCount - 1)!!.view.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
                            return@setOnKeyListener true
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            lastFocusedTabPosition = tabCount - 1
                            setSelectedTabIndicatorColor(Color.TRANSPARENT)
                        }
                    }
                    return@setOnKeyListener false
                }
            }
        }
    }

    fun setNotificationCountAtTab(position: Int, notificationCount: Int){
        if(notificationCount>0) {
            val mTab = getTabAt(position)
            mTab!!.removeBadge()
            val badgeDrawable = mTab.orCreateBadge
            badgeDrawable.backgroundColor = Color.RED
            badgeDrawable.isVisible = true
            badgeDrawable.number = notificationCount
        }
    }

    fun requestFocusAtTab(position:Int){
        selectTab(getTabAt(position))
        getTabAt(position)!!.view.requestFocus()
    }

    override fun setSelectedTabIndicatorColor(color: Int) {
        super.setSelectedTabIndicatorColor(color)
        if(color != Color.TRANSPARENT)
            indicatorColor = color
    }
}