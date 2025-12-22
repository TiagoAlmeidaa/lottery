package com.tiagoalmeida.lottery.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.databinding.ActivityRegisterBinding
import com.tiagoalmeida.lottery.util.applyWindowInsets

class GameRegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        binding.root.applyWindowInsets()
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackgroundDefault)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay, R.anim.slide_top_to_bottom)
    }

}
