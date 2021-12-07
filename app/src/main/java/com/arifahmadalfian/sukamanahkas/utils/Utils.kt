package com.arifahmadalfian.sukamanahkas.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.DateFormat
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.arifahmadalfian.sukamanahkas.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import org.apache.commons.text.WordUtils

const val DAY = "EEEE"
const val HOURS = "HH:mm"
const val DMY = "d MM yyyy"
const val HHDMY = "EEEE, HH:mm dd MMM yy"
const val HDMY = "HH:mm dd MMM yy"

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

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable) {
    val option = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.mipmap.ic_launcher)

    Glide.with(context)
        .setDefaultRequestOptions(option)
        .load(url)
        .circleCrop()
        .into(this)
}

fun getBitmaps(drawableRes: Int, context: Context): Bitmap? {
    val drawable: Drawable = ContextCompat.getDrawable(context, drawableRes)!!
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return bitmap
}

fun hasPermissions(
    context: Context?,
    vararg permissions: String?
): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
    }
    return true
}



            

