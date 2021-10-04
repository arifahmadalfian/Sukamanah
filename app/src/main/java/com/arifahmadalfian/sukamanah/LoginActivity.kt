package com.arifahmadalfian.sukamanah

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import com.arifahmadalfian.sukamanah.R
import android.content.Intent
import com.arifahmadalfian.sukamanah.MainActivity
import com.arifahmadalfian.sukamanah.RegisterActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.arifahmadalfian.sukamanah.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private val mUserID: String? = null
    private lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        session = Session(this)
        if (session.loggedIn()) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
        auth = FirebaseAuth.getInstance()
        binding?.btLogin?.setOnClickListener(View.OnClickListener { login() })
        binding?.tvDaftar?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        })
    }

    private fun login() {
        val email = binding?.etEmailLogin?.text.toString().trim { it <= ' ' }
        val password = binding?.etPasswordLogin?.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@LoginActivity, "Masukan Email Address", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this@LoginActivity, "Masukan Password", Toast.LENGTH_LONG).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this@LoginActivity, "Gagal Login", Toast.LENGTH_LONG).show()
            } else {
                session.setLoggedin(true)
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}