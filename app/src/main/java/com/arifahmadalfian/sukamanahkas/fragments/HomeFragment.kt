package com.arifahmadalfian.sukamanahkas.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.arifahmadalfian.sukamanahkas.LoginActivity
import com.arifahmadalfian.sukamanahkas.MainActivity
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.Session
import com.arifahmadalfian.sukamanahkas.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var session: Session

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
        binding?.btnLogout?.setOnClickListener {
            showPopupMenu(it)
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

}