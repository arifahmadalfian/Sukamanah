package com.arifahmadalfian.sukamanahkas

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var namalengkap: String? = null
    private var password: String? = null
    private var email: String? = null
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var urlImage: String? = null
    private var uuid: String? = null

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    companion object {
        //REQUEST CODE
        const val REQUEST = 1
        const val FUCK_UP = 2
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        initView()

    }

    private fun sendEmailVerification() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Check your Email for verification",
                    Toast.LENGTH_SHORT
                ).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }
    }

    private fun initView() {
        binding?.btRegister?.setOnClickListener {
            namalengkap = binding?.etNamaRegis?.text.toString().trim()
            password = binding?.etPasswordRegis?.text?.toString()?.trim()
            email = binding?.etEmailRegis?.text?.toString()?.trim()
            if (TextUtils.isEmpty(namalengkap) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                    email
                )
            ) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Semua kolom harus terisi",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password!!.length < 6) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password kurang dari 6 digit",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setView(layoutInflater.inflate(R.layout.custom_loading, null))
                builder.setCancelable(false)
                val dialog: AlertDialog = builder.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                lifecycleScope.launch {
                    dialog.show()
                    delay(1000)
                    email?.let { email ->
                        password?.let { password ->
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        sendEmailVerification()
                                        task.result?.user?.let { onAuth(it) }
                                        auth.signOut()
                                    } else {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Gagal Mendaftar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    dialog.dismiss()
                                }
                        }

                    }
                }

            }
        }

        binding?.imgChose?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this@RegisterActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this@RegisterActivity, "Permision denied", Toast.LENGTH_SHORT)
                        .show()
                    ActivityCompat.requestPermissions(
                        this@RegisterActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    chooseFoto()
                }
            } else {
                chooseFoto()
            }

        }
    }

    private fun onAuth(user: FirebaseUser) {
        createAnewUser(user.uid)
    }

    private fun createAnewUser(uid: String) {
        val image: String? = urlImage
        val user = User(
            admin = "false",
            emailUser = email,
            id = uid,
            namaUser = namalengkap,
            passUser = password,
            profileUser= "$image",
            profileUserUid = "$uuid",
            saldoPemasukan= "0",
            saldoTotal = "0"
        )
        databaseReference.child(uid).setValue(user)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK) {
                uuid = UUID.randomUUID().toString()
                val imagePath = storageReference.child("images/$uuid")
                data?.data?.let {
                    binding?.pbLoading?.visibility = View.VISIBLE
                    imagePath.putFile(it).continueWithTask { task ->
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }
                        imagePath.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            urlImage = task.result.toString()
                            binding?.imgProfile?.setImageURI(it)
                        } else {
                            val error = task.exception?.message
                            Toast.makeText(this@RegisterActivity, error, Toast.LENGTH_SHORT).show()
                        }
                        binding?.pbLoading?.visibility = View.GONE
                    }
                }

            } else if (resultCode == FUCK_UP) {
                Toast.makeText(this, "Gagal Memilih Profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun chooseFoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pilih Profile"), REQUEST)
    }

}