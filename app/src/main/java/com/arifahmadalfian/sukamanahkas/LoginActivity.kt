package com.arifahmadalfian.sukamanahkas

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.arifahmadalfian.sukamanahkas.databinding.ActivityLoginBinding
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
        binding?.btLogin?.setOnClickListener { login() }
        binding?.tvDaftar?.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        initView()
    }

    private fun initView() {
        binding?.passwordHide?.setOnClickListener {
            it.visibility = View.GONE
            binding?.passwordShow?.visibility = View.VISIBLE
            binding?.etPasswordLogin?.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }

        binding?.passwordShow?.setOnClickListener {
            it.visibility = View.GONE
            binding?.passwordHide?.visibility = View.VISIBLE
            binding?.etPasswordLogin?.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    @SuppressLint("InflateParams")
    private fun login() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.custom_loading, null))
        builder.setCancelable(false)
        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val email = binding?.etEmailLogin?.text.toString().trim { it <= ' ' }
        val password = binding?.etPasswordLogin?.text.toString().trim { it <= ' ' }
        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this@LoginActivity, "Masukan Email Address", Toast.LENGTH_LONG).show()
                return
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this@LoginActivity, "Masukan Password", Toast.LENGTH_LONG).show()
                return
            }
            else -> {
                lifecycleScope.launch {
                    dialog.show()
                    delay(1500)
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Gagal Login", Toast.LENGTH_SHORT).show()
                        } else {
                            session.setLoggedin(true)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        dialog.dismiss()
                    }
                }

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // or finish();
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}