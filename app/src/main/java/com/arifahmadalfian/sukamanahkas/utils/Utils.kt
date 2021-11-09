package com.arifahmadalfian.sukamanahkas.utils

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import org.apache.commons.text.WordUtils

@SuppressLint("SimpleDateFormat")
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

fun Int.currencyIdr(): String? {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    return format.format(this.toLong())
}

fun Double.toCurrencyIdr(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    return formatter.format(this)
}

fun String.toCapitalize(): String {
    return WordUtils.capitalizeFully(this).replace("-", " ")
}

fun Long.numberToCurrency(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    return formatter.format(this)
}

