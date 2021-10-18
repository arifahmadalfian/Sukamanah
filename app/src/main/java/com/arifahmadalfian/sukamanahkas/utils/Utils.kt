package com.arifahmadalfian.sukamanahkas.utils

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Long.epochToDateTime(): String {
    val date = Date(this * 1000L)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
    return sdf.format(date)
}

@SuppressLint("SimpleDateFormat")
fun Long.epochToDay(): String {
    val date = Date(this * 1000L)
    val sdf = SimpleDateFormat("EEEE, d MMM yyyy HH:mm")
    return sdf.format(date)
}

fun getToday(): String {
    val date = Calendar.getInstance().time
    val hariIni = DateFormat.format("EEEE", date) as String
    val tanggal = DateFormat.format("d MMM yyyy", date) as String
    return "$hariIni, $tanggal"
}
