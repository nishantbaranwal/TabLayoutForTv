package com.example.customtablayout

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),TabLayoutClickEventListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val jsonString = assets.open("jsonfiles/menu.json").bufferedReader().use {it.readText()}
        val menuItems:List<MenuItem> = Gson().fromJson(jsonString, object : TypeToken<List<MenuItem>>(){}.type)
        tabs.setSelectedTabIndicatorColor(Color.GREEN)
        tabs.addTab("Main Menu","Tab 0", 5)
        tabs.addTab("Search",R.drawable.circle, "Tab 1",12)
        tabs.addTab(R.drawable.ic_search_black_24dp, "Tab 2",0)
        tabs.addTab(R.drawable.ic_search_black_24dp, "Tab 3",2)
//      custom Tab
//      tabs.addTab(R.layout.custom_tab,"3")

//Request focus at element
        tabs.requestFocusAtTab(1)

        tabs.setNotificationCountAtTab(2,2)

//Auto CreateTabs
        tabs.createTabs(menuItems)
        tabs.getTabAt(0)!!.removeBadge()
//        tabs.getTabAt(0)!!.view.requestFocus()
    }

    override fun replace(clickEvent: Any) {
//        Toast.makeText(applicationContext,clickEvent as String,Toast.LENGTH_SHORT).show()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout,SuperAwesomeCardFragment(),clickEvent as String).commit()
    }

}
