package com.arifahmadalfian.sukamanahkas.fragments

import android.app.AlertDialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.Session
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.FragmentHomeBinding
import com.arifahmadalfian.sukamanahkas.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var userData: User
    private var admin = "false"
    private var createBy = ""

    private lateinit var session: Session
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mDatabase: FirebaseDatabase

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
        mStorage = FirebaseStorage.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
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
                binding?.ivProfileHome?.load(imageUser) {
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                    crossfade(true)
                    crossfade(400)
                    transformations(RoundedCornersTransformation(10f))
                }
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
                 * setting name profile & isAdmin
                 */
                binding?.tvName?.text = data[3].toString().toCapitalize()
                dialog.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        val imageProfile = mDatabase.reference.child("Users").child("$user")
        imageProfile.addListenerForSingleValueEvent(eventListener)

        /**
         * setting image null
         */
        binding?.ivProfileHome?.load("https://firebasestorage.googleapis.com/v0/b/sukamanah-ccbf0.appspot.com/o/images%2Fblue_image.png?alt=media&token=76aaf1c0-a04a-4365-a7e4-69cbf9986f40") {
            placeholder(R.drawable.ic_placeholder)
            error(R.drawable.ic_placeholder)
            crossfade(true)
            crossfade(400)
            transformations(RoundedCornersTransformation(10f))
        }

        binding?.fabAdd?.setOnClickListener {
            showBottomSheetAdd()
        }

        binding?.fabAdd?.setOnLongClickListener {
            showToast(requireContext(), "Coming Soon")
            true
        }

        binding?.btnShowQr?.setOnClickListener {
            showToast(requireContext(), "Coming Soon")
        }

        binding?.btnPrint?.setOnClickListener {
            showToast(requireContext(), "Coming Soon")
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
            R.id.actionSortHariIni -> {
                Toast.makeText(requireContext(), "Hari ini", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.actionSortMinggu -> {
                Toast.makeText(requireContext(), "1 Minggu", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.actionSortBulan -> {
                Toast.makeText(requireContext(), "1 Bulan", Toast.LENGTH_SHORT).show()
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
        val bottomSheet: com.arifahmadalfian.sukamanahkas.fragments.BottomSheetDialog = BottomSheetDialog(createBy)
        fragmentManager?.let {
            setupFullHeight(requireView())
            bottomSheet.show(it, "bottomsheet")
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}