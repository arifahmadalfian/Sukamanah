package com.arifahmadalfian.sukamanahkas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.arifahmadalfian.sukamanahkas.databinding.ActivityMainBinding
import com.arifahmadalfian.sukamanahkas.utils.TOPIC
import com.google.firebase.messaging.FirebaseMessaging
import android.content.Intent




class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

//        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
//            FirebaseService.token = it.token
//            etToken.setText(it.token)
//        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController

        Navigation.findNavController(this, R.id.container)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // This is important, otherwise the result will not be passed to the fragment
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        doExitApp()
    }

    private var exitTime: Long = 0

    private fun doExitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}