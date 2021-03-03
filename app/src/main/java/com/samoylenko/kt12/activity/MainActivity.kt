package com.samoylenko.kt12.activity

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.samoylenko.kt12.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}