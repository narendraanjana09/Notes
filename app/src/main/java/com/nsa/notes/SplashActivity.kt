package com.nsa.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nsa.notes.extra.Analyitcs

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Analyitcs().log("app_open","App Started")
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
}