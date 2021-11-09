package com.arifahmadalfian.sukamanahkas.fragments

import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.databinding.LayoutTambahKasBinding
import com.arifahmadalfian.sukamanahkas.utils.numberToCurrency
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: LayoutTambahKasBinding? = null
    private val binding get() = _binding!!

    private var currentEditTextAmount: String = "0"

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

        initListener()
    }

    private fun initListener() {
        binding.ivClose.setOnClickListener {
            this.dismiss()
        }
        binding.btnSave.setBackgroundColor(resources.getColor(R.color.biru))
        binding.etJumlah.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
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