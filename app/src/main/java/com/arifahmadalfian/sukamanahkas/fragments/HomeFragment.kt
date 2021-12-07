package com.arifahmadalfian.sukamanahkas.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.arifahmadalfian.sukamanahkas.*
import com.arifahmadalfian.sukamanahkas.data.model.Kas
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.FragmentHomeBinding
import com.arifahmadalfian.sukamanahkas.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener, IOnKasItemsClickListener {

    private var startDate: Long? = null
    private var endDate: Long? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var userData: User
    private var admin = "false"
    private var createBy = ""
    private lateinit var homeAdapter: HomeAdapter
    private val listKas: ArrayList<Kas> = ArrayList()
    private val totalKas: MutableList<Int> = mutableListOf()

    private lateinit var session: Session
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    private var isPrint = true

    companion object{
        const val SCAN_MEMBER = 100
        const val RESULT_SCAN = "result_scan"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = Session(requireContext())
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        userData = User()
        binding?.btnLogout?.setOnClickListener {
            showPopupMenu(it)
        }
        initView()
    }

    private fun initView() {
        // Menunggu semua data sudah terambil
        val builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setView(layoutInflater.inflate(R.layout.custom_loading, null))
        builder.setCancelable(false)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val user = mAuth.currentUser?.uid
        var imageUser = "null"
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = mutableListOf<Any>()
                for (ds in dataSnapshot.children) {
                    ds.getValue(Any::class.java)?.let { data.add(it) }
                }
                imageUser = "${data[5]}"
                binding?.ivProfileHome?.loadImage(imageUser, getProgressDrawable(requireContext()))
                createBy = "${data[3]}"
                admin = "${data[0]}"
                /**
                 * hak akses admin untuk menambahkan data
                 */
                if (admin == "false") {
                    binding?.fabAdd?.visibility = View.GONE
                    binding?.btnPrint?.visibility = View.GONE
                    binding?.btnShowQr?.visibility = View.VISIBLE
                } else {
                    binding?.fabAdd?.visibility = View.VISIBLE
                    binding?.btnPrint?.visibility = View.VISIBLE
                    binding?.btnShowQr?.visibility = View.GONE
                }

                /**
                 * pengambilan data firestore setelah pengambilan datastore beres
                 */
                listKas.clear()
                totalKas.clear()
                getDataKas()
                /**
                 * setting name profile & isAdmin
                 */
                binding?.tvName?.text = data[3].toString().toCapitalize()
                dialog.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        val imageProfile = mDatabase.reference.child("Users").child("$user")
        imageProfile.addListenerForSingleValueEvent(eventListener)

        binding?.fabAdd?.setOnClickListener {
            showBottomSheetAdd()
        }

        binding?.fabAdd?.setOnLongClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this@HomeFragment)
            integrator.captureActivity = ScanViewActivity::class.java
            integrator.setOrientationLocked(false)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt("Scanning...")
            integrator.initiateScan()
            true
        }

        binding?.btnShowQr?.setOnClickListener {
            showToast(requireContext(), "Coming Soon")
        }

        binding?.btnPrint?.setOnClickListener {
            showToast(requireContext(), "Coming Soon")
        }

        homeAdapter = HomeAdapter(this)
        binding?.rvItemKas?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = homeAdapter
            setHasFixedSize(true)
        }

        binding?.swipeRefresh?.setColorSchemeColors(resources.getColor(R.color.design_default_color_error))
        binding?.swipeRefresh?.setOnRefreshListener {
            listKas.clear()
            totalKas.clear()
            getDataKas()
        }

        binding?.btnShowQr?.setOnClickListener {
            showBottomSheetQr()
        }

        binding?.btnPrint?.setOnClickListener {
            getLaporanKas()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result != null) {
                if (result.contents == null) {
                    Log.d("ScanFrag", "Cancelled scan")
                    Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Log.d("ScanFrag", "Scanned | " + result.contents)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataKas() {
        FirebaseFirestore.getInstance().collection("Kas")
            .orderBy("createAt", Query.Direction.DESCENDING)
            .limit(100L)
            .addSnapshotListener {value, error ->
            if (error != null) {
                showToast(requireContext(), "Error")
                return@addSnapshotListener
            }
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    var kas: Kas? = null
                    //filter untuk data berdasarkan datetime/epoch
                    if (startDate != null && endDate != null) {
                        if (dc.document.get("createAt").toString().toLong() >= startDate!! &&
                            dc.document.get("createAt").toString().toLong() <= endDate!!) {
                            kas = Kas(
                                dc.document.get("createAt").toString(),
                                dc.document.get("createBy").toString(),
                                dc.document.get("inclusion").toString(),
                                dc.document.get("id").toString(),
                                dc.document.get("name").toString(),
                                dc.document.get("profile").toString(),
                            )
                            listKas.add(kas)
                            totalKas.add(dc.document.get("inclusion").toString().replace(".","").toInt())
                        }
                    } else {
                        kas = Kas(
                            dc.document.get("createAt").toString(),
                            dc.document.get("createBy").toString(),
                            dc.document.get("inclusion").toString(),
                            dc.document.get("id").toString(),
                            dc.document.get("name").toString(),
                            dc.document.get("profile").toString(),
                        )
                        listKas.add(kas)
                        totalKas.add(dc.document.get("inclusion").toString().replace(".","").toInt())
                    }

                }
            }
            // cek ada data atau tidak
            if (listKas.isEmpty()) {
                binding?.swipeRefresh?.isRefreshing = false
                binding?.emptyLayout?.root?.visibility = View.VISIBLE
                isPrint = false
            } else {
                binding?.emptyLayout?.root?.visibility = View.GONE
                isPrint = true
            }
            homeAdapter.setUser(listKas)
            homeAdapter.notifyDataSetChanged()
            lifecycleScope.launch {
                delay(1500)
                binding?.swipeRefresh?.isRefreshing = false
            }
        }
    }

    private fun showPopupMenu(v: View) {
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener(this@HomeFragment)
            inflate(R.menu.popup_menu)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSemua -> {
                startDate = null
                endDate = null
                binding?.swipeRefresh?.isRefreshing = true
                listKas.clear()
                totalKas.clear()
                getDataKas()
                return true
            }
            R.id.actionFilter -> {
                showDateDialogPicker()
                return true
            }
            R.id.actionProfile -> {
                Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.actionLogout -> {
                AlertDialog.Builder(requireContext())
                    .setIcon(R.drawable.rw)
                    .setTitle("Keluar")
                    .setMessage("Apakah anda yakin ingin keluar!")
                    .setPositiveButton("OK") { _, _ ->
                        session.setLoggedin(false)
                        FirebaseAuth.getInstance().signOut()
                        HomeFragmentDirections.actionHomeFragmentToLoginActivity().also {
                            findNavController().navigate(it)
                        }

                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
                return true
            }
            else -> {
                return false
            }
        }
    }

    private fun showBottomSheetAdd() {
        val bottomSheet= BottomSheetDialog(createBy)
        fragmentManager?.let {
            setupFullHeight(requireView())
            bottomSheet.show(it, "bottomsheet")
        }
    }

    private fun showBottomSheetQr() {
        val bottomSheet= BottomSheetQr( mAuth.currentUser?.uid, createBy)
        fragmentManager?.let {
            setupFullHeight(requireView())
            bottomSheet.show(it, "bottomsheetqr")
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onKasItemClickListener(kas: Kas, position: Int) {
        Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun getLaporanKas() {
        if (isPrint) {
            showToast(requireContext(), "Laporan Kas PDF")
            PdfUtils(requireContext(), listKas.size, listKas, totalKas.sum()).also {
                it.printThermal58()
            }
        } else {
            showToast(requireContext(), "Kosong Bray")
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showDateDialogPicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())
        picker.addOnCancelListener {
            picker.dismiss()
        }
        picker.addOnNegativeButtonClickListener {
            picker.dismiss()
        }
        // 25200_000 timemillis = 7 jam -> 00:01
        // 61200_000 timemillis = 17 jam -> 23:58
        picker.addOnPositiveButtonClickListener { time ->
            if (time.first != null && time.second != null) {
                startDate = time.first - 25100000
                endDate = time.second + 61100000
                //loading
                binding?.swipeRefresh?.isRefreshing = true
                listKas.clear()
                totalKas.clear()
                getDataKas()
            }
            picker.dismiss()
        }
    }

}