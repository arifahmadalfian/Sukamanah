package com.arifahmadalfian.sukamanahkas.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String? = null,
    var namaUser: String? = null,
    val passUser: String? = null,
    val emailUser: String? = null,
    val profileUser: String? = null,
    val profileUserUid: String? = null,
    val saldoPemasukan: String? = null,
    val saldoTotal: String? = null,
    val isAdmin: Boolean = false
): Parcelable
