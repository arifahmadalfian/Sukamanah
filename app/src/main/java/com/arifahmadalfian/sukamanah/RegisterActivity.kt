package com.arifahmadalfian.sukamanah

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import android.content.Intent
import android.view.View
import com.arifahmadalfian.sukamanah.data.model.User
import com.arifahmadalfian.sukamanah.databinding.ActivityRegisterBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    private var mNamaUser: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()
        binding?.btRegister?.setOnClickListener(View.OnClickListener {
            val namalengkap = binding?.etNamaRegis?.text.toString().lowercase(Locale.getDefault())
            val password = binding?.etPasswordRegis?.text?.toString()
            val email = binding?.etEmailRegis?.text?.toString()?.lowercase(Locale.getDefault())
            if (TextUtils.isEmpty(namalengkap) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                    email
                )
            ) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Semua kolom harus terisi",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (email != null) {
                    if (password != null) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this@RegisterActivity) { task ->
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Berhasil Mendaftar",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (!task.isSuccessful) {
                                    Toast.makeText(
                                        this@RegisterActivity, "Gagal Mendaftar" + task.exception,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    if (FirebaseAuth.getInstance().currentUser != null) {
                                        mNamaUser = FirebaseAuth.getInstance().currentUser!!.uid
                                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                                    } else {
                                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                                    }
                                    databaseReference = FirebaseDatabase.getInstance().getReference(
                                        mNamaUser!!
                                    )
                                    val value = User(namalengkap, password, email, "0", "0", "0")
                                    databaseReference.setValue(value).addOnCompleteListener {
                                        val intent =
                                            Intent(this@RegisterActivity, LoginActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                                }
                            }
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}