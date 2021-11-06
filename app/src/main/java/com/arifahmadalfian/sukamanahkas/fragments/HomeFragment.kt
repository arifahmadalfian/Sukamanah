package com.arifahmadalfian.sukamanahkas.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.Session
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.arifahmadalfian.sukamanahkas.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var userData: User

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
        val user = mAuth.currentUser?.uid
        var imageUser = ""
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = mutableListOf<Any>()
                for (ds in dataSnapshot.children) {
                    ds.getValue(Any::class.java)?.let { data.add(it) }
                }
                imageUser = "${data[4]}"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        val imageProfile = mDatabase.reference.child("Users").child("$user")
        imageProfile.addListenerForSingleValueEvent(eventListener)
        Log.d("userData","$imageUser")




//
//        if (user != null) {
//            binding?.ivProfileHome?.load("$profile") {
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_placeholder)
//                crossfade(true)
//                crossfade(400)
//                transformations(RoundedCornersTransformation(100f))
//            }
//
//        } else {
//            binding?.ivProfileHome?.load("https://firebasestorage.googleapis.com/v0/b/sukamanah-ccbf0.appspot.com/o/images%2Fblue_image.png?alt=media&token=c820dc0a-b77c-4dd9-a315-8ef8b9053c12") {
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_placeholder)
//                crossfade(true)
//                crossfade(400)
//                transformations(RoundedCornersTransformation(100f))
//            }
//        }
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

}