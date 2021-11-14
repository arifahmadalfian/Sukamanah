package com.arifahmadalfian.sukamanahkas.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.LayoutTambahKasBinding
import com.arifahmadalfian.sukamanahkas.utils.numberToCurrency
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class BottomSheetDialog(createBy: String) : BottomSheetDialogFragment() {

    private var _binding: LayoutTambahKasBinding? = null
    private val binding get() = _binding!!

    private var currentEditTextAmount: String = "0"
    private lateinit var database: DatabaseReference
    private lateinit var search: AutoCompleteTextView
    private val users = ArrayList<User>()

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
        database = FirebaseDatabase.getInstance().getReference("Users")
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
                    Toast.makeText(requireContext(),"$users", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.ivClose.setOnClickListener {
            this.dismiss()
        }
        //binding.btnSave.setBackgroundColor(resources.getColor(R.color.biru))
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.arifahmadalfian.sukamanahkas.R.style.CustomBottomSheetDialogTheme)
    }

}