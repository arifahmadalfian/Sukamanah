package com.arifahmadalfian.sukamanahkas.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.data.model.NotificationData
import com.arifahmadalfian.sukamanahkas.data.model.PushNotification
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.data.retrofit.RetrofitInstance
import com.arifahmadalfian.sukamanahkas.databinding.LayoutTambahKasBinding
import com.arifahmadalfian.sukamanahkas.utils.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout

import android.view.ViewGroup

class BottomSheetDialog(val createBy: String) : BottomSheetDialogFragment() {

    private var _binding: LayoutTambahKasBinding? = null
    private val binding get() = _binding!!

    private var currentEditTextAmount: String = "0"
    private lateinit var database: DatabaseReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var search: AutoCompleteTextView
    private val users = ArrayList<User>()

    val TAG = "MainActivity"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutTambahKasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = bottomSheet.height
        }

        database = FirebaseDatabase.getInstance().getReference("Users")
        firestore = FirebaseFirestore.getInstance()
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        search = binding.atCari

        val event = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                getSearch(snapshot)
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        database.addListenerForSingleValueEvent(event)
        initListener()
    }

    private fun getSearch(snapshot: DataSnapshot) {
        val names = ArrayList<String>()
        if (snapshot.exists()) {
            for (data in snapshot.children) {
                val name = data.child("namaUser").getValue(String::class.java)
                if (name != null) {
                    names.add(name)
                }
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, names)
            search.setAdapter(adapter)
            search.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                searchUser(binding.atCari.text.toString())
                Log.d("Search user", "Erorr")
            }
        } else {
            Log.d("Search user", "Erorr")
        }
    }

    private fun searchUser(dataUser: String) {
        val query = database.orderByChild("namaUser").equalTo(dataUser)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    users.clear()
                    for (data in snapshot.children) {
                        val user = User(
                            data.child("admin").getValue(String::class.java)!!,
                            data.child("emailUser").getValue(String::class.java),
                            data.child("id").getValue(String::class.java),
                            data.child("namaUser").getValue(String::class.java),
                            data.child("passUser").getValue(String::class.java),
                            data.child("profileUser").getValue(String::class.java),
                            data.child("profileUserUid").getValue(String::class.java),
                            data.child("saldoPemasukan").getValue(String::class.java),
                            data.child("saldoTotal").getValue(String::class.java)
                        )
                        users.add(user)
                        binding.btnSave.isEnabled = true
                        binding.btnSave.setBackgroundColor(resources.getColor(R.color.biru))
                    }
                } else {
                    Log.d("Search user", "Erorr")
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun initListener() {
        binding.btnSave.setOnClickListener {
            when {
                binding.atCari.text.isEmpty() -> {
                    binding.atCari.error = "Tidak boleh kosong"
                }
                binding.etJumlah.text.isEmpty() -> {
                    binding.etJumlah.error = "Input Pemasukan"
                }
                else -> {
                    lifecycleScope.launch {
                        delay(1000)
                        binding.lottieAnimationViewLoading.visibility = View.VISIBLE
                        binding.lottieAnimationView.visibility = View.GONE
                        addFirestore()
                    }
                }
            }
        }

        binding.ivClose.setOnClickListener {
            this.dismiss()
        }

        binding.etJumlah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (users.isNotEmpty()) {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.setBackgroundColor(resources.getColor(R.color.biru))
                    Log.d("Search user", "$users")
                }
                if (p0 == null) return
                if (p0.isEmpty()) return
                if (p0.toString() != currentEditTextAmount || p0.toString() != "") {
                    binding.etJumlah.removeTextChangedListener(this)
                    val cleanString: String = p0.toString().replace("""[,.]""".toRegex(), "")
                    val parsed = cleanString.toLong().numberToCurrency()

                    currentEditTextAmount = parsed
                    binding.etJumlah.setText(parsed)
                    binding.etJumlah.setSelection(parsed.length)
                    binding.etJumlah.addTextChangedListener(this)
                } else {
                    binding.etJumlah.setText("0")
                }
            }
        })
    }

    private fun addFirestore() {
        val saldo = "${users[0].saldoTotal}"
        val saldoAhir = saldo.toInt() + binding.etJumlah.text.toString().replace(".", "").toInt()
        val id = "${users[0].id}"

        /**Add Firestore*/
        val kas: MutableMap<String, String> = mutableMapOf()
        kas["id"] = id
        kas["name"] = "${users[0].namaUser?.toCapitalize()}"
        kas["profile"] = "${users[0].profileUser}"
        kas["inclusion"] = binding.etJumlah.text.toString()
        kas["createAt"] = todayTimeInMillis
        kas["createBy"] = createBy
        firestore.collection("Kas").add(kas).addOnSuccessListener {
            /**Update database*/
            database.child(id).child("saldoTotal").setValue("$saldoAhir")
            PushNotification(
                NotificationData("${users[0].namaUser?.toCapitalize()}", "Pembayaran kas ${binding.etJumlah.text}" ),
                TOPIC
            ).also {
                sendNotification(it)
            }
            showToast(requireContext(), "Berhasil")
            this.dismiss()
        }.addOnFailureListener {
            showToast(requireContext(), "Gagal")
            this.dismiss()
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val bottomSheet: View = dialog.findViewById(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val view = view
        view!!.post {
            val parent = view.parent as View
            val params =
                parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.peekHeight = view.measuredHeight
            parent.setBackgroundColor(Color.TRANSPARENT)
        }
    }


}