package com.samoylenko.kt12.activity

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.samoylenko.kt12.R

class MainFragmentActivity : AppCompatActivity(R.layout.activity_main_fragment) {


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}