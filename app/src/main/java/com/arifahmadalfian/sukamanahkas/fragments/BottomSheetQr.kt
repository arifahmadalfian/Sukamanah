package com.arifahmadalfian.sukamanahkas.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.databinding.LayoutShowQrBinding
import com.arifahmadalfian.sukamanahkas.utils.CuteR
import com.arifahmadalfian.sukamanahkas.utils.getBitmaps
import com.arifahmadalfian.sukamanahkas.utils.toCapitalize
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetQr(
    private val uid: String?,
    private val name: String?
) : BottomSheetDialogFragment() {

    private var _binding: LayoutShowQrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutShowQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoBitmap = getBitmaps(R.drawable.rw, requireContext())
        val bitmap = CuteR.ProductLogo(logoBitmap, uid.toString(), true, Color.BLACK)
        binding.ivQrCode.setImageBitmap(bitmap)
        binding.tvQrName.text = name?.toCapitalize()
        binding.ivClose.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
}