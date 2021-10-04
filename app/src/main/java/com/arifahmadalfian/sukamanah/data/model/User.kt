package com.arifahmadalfian.sukamanah.data.model

data class User(
    var namaUser: String? = null,
    val passUser: String? = null,
    val emailUser: String? = null,
    val saldoPemasukan: String? = null,
    val saldoPengeluaran: String? = null,
    val saldoWishlist: String? = null
)
