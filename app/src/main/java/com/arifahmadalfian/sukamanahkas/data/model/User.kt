package com.arifahmadalfian.sukamanahkas.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val admin: String = "false",
    val emailUser: String? = null,
    var id: String? = null,
    var namaUser: String? = null,
    val passUser: String? = null,
    val profileUser: String? = null,
    val profileUserUid: String? = null,
    val saldoPemasukan: String? = null,
    val saldoTotal: String? = null,

): Parcelable
