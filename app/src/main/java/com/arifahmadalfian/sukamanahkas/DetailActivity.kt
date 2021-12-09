package com.arifahmadalfian.sukamanahkas

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arifahmadalfian.sukamanahkas.data.model.Kas
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.ActivityDetailBinding
import com.arifahmadalfian.sukamanahkas.fragments.HomeFragmentDirections
import com.arifahmadalfian.sukamanahkas.utils.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity : AppCompatActivity(), IOnKasItemsClickListener,
    PopupMenu.OnMenuItemClickListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var userData: User
    private val listKas: ArrayList<Kas> = ArrayList()
    private lateinit var homeAdapter: HomeAdapter

    private var urlImage: String? = null

    private val ref = Firebase.firestore.collection("Kas")

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    companion object {
        const val EXTRA_PERSON = "extra_person"
        //REQUEST CODE
        const val REQUEST = 1
        const val FUCK_UP = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userData = intent.getParcelableExtra<User>(EXTRA_PERSON) as User
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        initView()
        getListData()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.btnBackDetail.setOnClickListener {
            startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            finish()
        }
        binding.tvNameDetail.text = userData.namaUser?.toCapitalize()
        binding.tvInclusion.text = "Rp ${userData.saldoTotal?.toLong()?.numberToCurrency()}"
        binding.ivProfileDetail.loadImage(userData.profileUser, getProgressDrawable(this@DetailActivity))
        Glide.with(this@DetailActivity).load(userData.profileUser)
            .apply(bitmapTransform(BlurTransformation(25, 2)))
            .error(R.mipmap.ic_launcher)
            .into(binding.ivBackgroundProfile)
        homeAdapter = HomeAdapter(this)
        binding.rvItemKasDetail.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            adapter = homeAdapter
            setHasFixedSize(true)
        }
        binding.swipeRefreshDetail.setColorSchemeColors(resources.getColor(R.color.design_default_color_error))
        binding.swipeRefreshDetail.setOnRefreshListener {
            listKas.clear()
            getListData()
        }
        binding.btnSettingDetail.setOnClickListener {
            showPopupMenuDetail(it)
        }
    }

    private fun showPopupMenuDetail(v: View) {
        PopupMenu(this@DetailActivity, v).apply {
            setOnMenuItemClickListener(this@DetailActivity)
            inflate(R.menu.popup_menu_detail)
            show()
        }
    }

    private fun getListData() {
        binding.swipeRefreshDetail.isRefreshing = true
        FirebaseFirestore.getInstance().collection("Kas").whereEqualTo("id", userData.id)
            .get()
            .addOnSuccessListener { value ->
                getQuerySnapshot(value)
            }
            .addOnFailureListener{
                showToast(this, "Gagal Mencari Data")
            }
    }

    private fun getQuerySnapshot(value: QuerySnapshot?) {
        for (dc: DocumentChange in value?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var kas: Kas? = null
                kas = Kas(
                    dc.document.get("createAt").toString(),
                    dc.document.get("createBy").toString(),
                    dc.document.get("inclusion").toString(),
                    dc.document.get("id").toString(),
                    dc.document.get("name").toString(),
                    dc.document.get("profile").toString(),
                )
                listKas.add(kas)

            }
        }
        // cek ada data atau tidak
        binding.swipeRefreshDetail.isRefreshing = false
        homeAdapter.setUser(listKas)
        homeAdapter.notifyDataSetChanged()
        lifecycleScope.launch {
            delay(1500)
            binding.swipeRefreshDetail.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
        finish()
    }

    override fun onKasItemClickListener(kas: Kas, position: Int) {

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
       when (item?.itemId) {
           R.id.actionSetting -> {
               AlertDialog.Builder(this@DetailActivity)
                   .setTitle("Ganti Photo Profile")
                   .setMessage("Yakin ingin mengganti profile!")
                   .setPositiveButton("OK") { _, _ ->
                       updateProfile()
                   }
                   .setNegativeButton("Cancel") { dialog, _ ->
                       dialog.cancel()
                   }
                   .show()
               return true
           }
           else ->{
               return false
           }
       }
    }

    private fun updateProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@DetailActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this@DetailActivity, "Permision denied", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.requestPermissions(
                    this@DetailActivity,
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

    private fun chooseFoto() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Pilih Profile"),
            RegisterActivity.REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RegisterActivity.REQUEST) {
            if (resultCode == RESULT_OK) {
                //hapusdata storage
                val desertRef = storageReference.child("images/${userData.profileUserUid}.jpeg")
                desertRef.delete().addOnSuccessListener {
                    //upload image
                    val imagePath = storageReference.child("images/${userData.profileUserUid}.jpeg")
                    data?.data?.let {
                        binding.pbLoading.visibility = View.VISIBLE
                        imagePath.putFile(it).continueWithTask { task ->
                            if (!task.isSuccessful) {
                                throw task.exception!!
                            }
                            imagePath.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                urlImage = task.result.toString()
                                //updatedatabase
                                urlImage?.let { pr->
                                    updateProfileDatabase(pr)
                                }
                                urlImage?.let { prl ->
                                    updateProfileFirestore(prl)
                                }
                                binding.ivProfileDetail.loadImage(urlImage, getProgressDrawable(this@DetailActivity))
                                Glide.with(this@DetailActivity).load(urlImage)
                                    .apply(bitmapTransform(BlurTransformation(25, 2)))
                                    .error(R.mipmap.ic_launcher)
                                    .into(binding.ivBackgroundProfile)
                            } else {
                                val error = task.exception?.message
                                Toast.makeText(this@DetailActivity, error, Toast.LENGTH_SHORT).show()
                            }
                            binding.pbLoading.visibility = View.GONE
                        }
                    }
                }


            } else if (resultCode == RegisterActivity.FUCK_UP) {
                Toast.makeText(this, "Gagal Memilih Profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfileDatabase(url: String) {
        FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(userData.id.toString())
            .child("profileUser")
            .setValue(url)
    }

    private fun updateProfileFirestore(url: String) {
        val query = ref
            .whereEqualTo("id", userData.id)
            .get()
            .addOnSuccessListener { value ->
                getQuerySnapshotUpdate(value, url)
            }
            .addOnFailureListener{
                showToast(this, "Gagal Mencari Data")
            }

    }

    private fun getQuerySnapshotUpdate(value: QuerySnapshot?, url: String) {
        for (dc: DocumentChange in value?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                if (dc.document.get("id").toString() == userData.id) {
                    //mengabil id document firestore
                    // lalu menggati field nya
                    val doc = ref.document(dc.document.id).id
                    val refUpdate = ref.document(doc.toString())
                    val map: MutableMap<String, Any> = mutableMapOf()
                    map["createAt"] = dc.document.get("createAt").toString()
                    map["createBy"] = dc.document.get("createBy").toString()
                    map["inclusion"] = dc.document.get("inclusion").toString()
                    map["id"] = dc.document.get("id").toString()
                    map["name"] = dc.document.get("name").toString()
                    map["profile"] = url
                    refUpdate.update(map)
                        .addOnSuccessListener {
                            Log.d("DetailActivity", "Success")
                        }.addOnFailureListener {
                            Log.d("DetailActivity", "Error")
                        }

                }
            }
        }
    }

}