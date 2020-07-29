package com.example.expiredcalculator.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.expiredcalculator.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        customBottomBar.setupWithNavController(findNavController(R.id.fragment))

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this,AddNewItemActivity::class.java))
        }
    }
}