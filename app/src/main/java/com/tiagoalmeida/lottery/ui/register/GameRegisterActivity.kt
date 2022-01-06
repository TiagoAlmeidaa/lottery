package com.tiagoalmeida.lottery.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tiagoalmeida.lottery.R

class GameRegisterActivity : AppCompatActivity(R.layout.activity_register) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackgroundDefault)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay, R.anim.slide_top_to_bottom)
    }

}
