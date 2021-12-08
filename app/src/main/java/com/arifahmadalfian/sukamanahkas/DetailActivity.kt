package com.arifahmadalfian.sukamanahkas

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arifahmadalfian.sukamanahkas.data.model.Kas
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.ActivityDetailBinding
import com.arifahmadalfian.sukamanahkas.utils.*
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(), IOnKasItemsClickListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var userData: User
    private val listKas: ArrayList<Kas> = ArrayList()
    private lateinit var homeAdapter: HomeAdapter

    companion object {
        const val EXTRA_PERSON = "extra_person"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userData = intent.getParcelableExtra<User>(EXTRA_PERSON) as User
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
            showToast(this, "Coming soon")
        }
    }

    private fun getListData() {
        binding.swipeRefreshDetail.isRefreshing = true
        FirebaseFirestore.getInstance().collection("Kas").whereEqualTo("name", userData.namaUser?.toCapitalize())
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

    override fun onKasItemClickListener(kas: Kas, position: Int) {

    }
}