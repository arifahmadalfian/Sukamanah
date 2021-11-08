package com.arifahmadalfian.sukamanahkas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil.transform.RoundedCornersTransformation
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class HomeViewModel: ViewModel(){


    private var _isLoading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _isLoading

    private var _profile = MutableLiveData<String>()
    val profile: LiveData<String> get() = _profile

    private var _userData = MutableLiveData<MutableList<Any>>()
    val userData: LiveData<MutableList<Any>> get() = _userData

    fun getDatabase(mAuth: FirebaseAuth, mStorage: FirebaseStorage, mDatabase: FirebaseDatabase) {
        _isLoading.value = true
        val user = mAuth.currentUser?.uid
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = mutableListOf<Any>()
                for (ds in dataSnapshot.children) {
                    ds.getValue(Any::class.java).let {
                        if (it != null) {
                            data.add(it)
                        }
                    }
                }
                Log.d("userDatav", "$data")
               _userData.postValue(data)
                _isLoading.value = false
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        val imageProfile = mDatabase.reference.child("Users").child("$user")
        imageProfile.addListenerForSingleValueEvent(eventListener)
    }



}