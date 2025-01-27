package com.example.nav

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nav.fragment.CenteredTextFragment
import com.example.nav.menu.DrawerAdapter
import com.example.nav.menu.DrawerItem
import com.example.nav.menu.SimpleItem
import com.example.nav.menu.SpaceItem
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder

class MainActivity : AppCompatActivity(), DrawerAdapter.OnItemSelectedListener {

    private lateinit var slidingRootNav: SlidingRootNav
    private lateinit var screenTitles: Array<String>
    private lateinit var screenIcons: Array<Drawable?>

    companion object {
        private const val POS_DASHBOARD = 0
        private const val POS_ACCOUNT = 1
        private const val POS_MESSAGES = 2
        private const val POS_CART = 3
        private const val POS_LOGOUT = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        slidingRootNav = SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(toolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject()

        screenIcons = loadScreenIcons()
        screenTitles = loadScreenTitles()

        val adapter = DrawerAdapter(
            listOf(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_ACCOUNT),
                createItemFor(POS_MESSAGES),
                createItemFor(POS_CART),
                SpaceItem(48),
                createItemFor(POS_LOGOUT)
            )
        )
        adapter.setListener(this)

        val list: RecyclerView = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        adapter.setSelected(POS_DASHBOARD)
    }

    override fun onItemSelected(position: Int) {
        if (position == POS_LOGOUT) {
            finish()
        } else {
            val selectedScreen = CenteredTextFragment.createFor(screenTitles[position])
            showFragment(selectedScreen)
        }
        slidingRootNav.closeMenu()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun createItemFor(position: Int): DrawerItem<*> {
        return SimpleItem(screenIcons[position], screenTitles[position])
            .withIconTint(color(R.color.textColorSecondary))
            .withTextTint(color(R.color.textColorPrimary))
            .withSelectedIconTint(color(R.color.colorAccent))
            .withSelectedTextTint(color(R.color.colorAccent))
    }

    private fun loadScreenTitles(): Array<String> {
        return resources.getStringArray(R.array.ld_activityScreenTitles)
    }

    private fun loadScreenIcons(): Array<Drawable?> {
        val ta: TypedArray = resources.obtainTypedArray(R.array.ld_activityScreenIcons)
        val icons = arrayOfNulls<Drawable>(ta.length())
        for (i in icons.indices) {
            val id = ta.getResourceId(i, 0)
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id)
            }
        }
        ta.recycle()
        return icons
    }

    @ColorInt
    private fun color(@ColorRes res: Int): Int {
        return ContextCompat.getColor(this, res)
    }
}