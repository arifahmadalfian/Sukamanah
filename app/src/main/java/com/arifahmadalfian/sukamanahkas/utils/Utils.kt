package com.arifahmadalfian.sukamanahkas.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import org.apache.commons.text.WordUtils

const val DAY = "EEEE"
const val HOURS = "HH:mm"
const val DMY = "d MM yyyy"
const val HDMY = "EEEE, HH:mm dd MMM yy"

const val BASE_URL = "https://fcm.googleapis.com"
const val SERVER_KEY = "AAAAivpj5C4:APA91bHufEMq3lWrqhsfVyZ6reDqUi64OEyb3-SASBy_WQ5MUtyEw0aP8xkBBnMAI_tMKZnO0u1Km2LJgnoDUdClaECZo7-xJxl-Pa0U88txl-DIztC1Apd6Iv6AucprQ8KxFTu9jqYq"
const val CONTENT_TYPE = "application/json"
const val TOPIC = "/topics/myTopic2"
const val CHANNEL_ID = "my_channel"

val todayTimeInMillis : String
    get() = Calendar.getInstance().timeInMillis.toString()

@SuppressLint("SimpleDateFormat")
fun Long.epochToDateTime(format: String = "dd MM yyyy"): String {
    val date = Date( this)
    val sdf = SimpleDateFormat(format)
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

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

