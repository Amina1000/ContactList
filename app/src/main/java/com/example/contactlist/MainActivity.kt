package com.example.contactlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.contactlist.ui.gps.GpsFragment
import com.example.contactlist.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_content_provider -> {
                addFragment(MainFragment.newInstance())
                true
            }
            R.id.menu_location -> {
                addFragment(GpsFragment.newInstance())
                true
            }
            else -> true
        }
    }

    private fun addFragment(fragment:Fragment) {
        supportFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }
}