package com.arifahmadalfian.sukamanah

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.arifahmadalfian.sukamanah.databinding.ActivitySplashscreenBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {

    private var _binding: ActivitySplashscreenBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.splashScreen?.alpha = 0f

        binding?.splashScreen?.animate()?.setDuration(1500)?.alpha(1f)?.withEndAction {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(800)
                val intent = Intent(this@SplashscreenActivity, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}