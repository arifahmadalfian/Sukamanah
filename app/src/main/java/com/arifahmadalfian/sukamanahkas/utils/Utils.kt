package com.arifahmadalfian.sukamanahkas.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.widget.ImageView
import android.widget.Toast
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

// private fun showQrCode(attendanceCode: String) {

//         val view = layoutInflater.inflate(R.layout.bottom_sheet_show_qr, null)
//         val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetRounded)
//         dialog.setContentView(view)
//         dialog.show()

//         val logoBitmap = getBitmap(R.drawable.ic_favicon_pos, requireContext())
//         val bitmap = CuteR.ProductLogo(logoBitmap, attendanceCode, true, Color.BLACK)
//         val ivQrCode = dialog.findViewById<ImageView>(R.id.iv_qr_code)
//         val ivDismiss = dialog.findViewById<ImageView>(R.id.iv_dismiss_bottom_sheet_qr)
//         val btnRefreshQr = dialog.findViewById<Button>(R.id.btn_refresh_qr)

//         ivQrCode?.setImageBitmap(bitmap)

//         timer = object : CountDownTimer(30000, 1000) {
//             @SuppressLint("SetTextI18n")
//             override fun onFinish() {
//                 dialog.tv_timeleft.text = "Qr code expired, please press refresh button"
//             }

//             override fun onTick(untilFinished: Long) {

//                 val s = TimeUnit.MILLISECONDS.toSeconds(untilFinished)

//                 dialog.tv_timeleft.text = String.format(
//                     Locale.getDefault(),
//                     "%02d Seconds left", s
//                 )
//             }
//         }.start()

//         btnRefreshQr?.setOnClickListener {
//             dialog.dismiss()
//             timer?.cancel()
//             timer?.onFinish()
//             viewModel.getRequestCode()
//         }

//         ivDismiss?.setOnClickListener {
//             dialog.dismiss()
//             timer?.onFinish()
//             timer?.cancel()
//         }

//     }

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

// import android.graphics.Bitmap;
// import android.graphics.Canvas;
// import android.graphics.Color;
// import android.graphics.ColorMatrix;
// import android.graphics.ColorMatrixColorFilter;
// import android.graphics.Matrix;
// import android.graphics.Paint;
// import android.util.Log;

// import com.google.zxing.BinaryBitmap;
// import com.google.zxing.DecodeHintType;
// import com.google.zxing.EncodeHintType;
// import com.google.zxing.MultiFormatReader;
// import com.google.zxing.NotFoundException;
// import com.google.zxing.RGBLuminanceSource;
// import com.google.zxing.Result;
// import com.google.zxing.WriterException;
// import com.google.zxing.common.BitMatrix;
// import com.google.zxing.common.HybridBinarizer;
// import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
// import com.google.zxing.qrcode.encoder.ByteMatrix;
// import com.google.zxing.qrcode.encoder.Encoder;
// import com.google.zxing.qrcode.encoder.QRCode;

// import java.util.EnumMap;
// import java.util.HashMap;
// import java.util.Map;

// /**
//  * Created by shaozheng on 2016/8/12.
//  */

// public class CuteR {
//     private static final String TAG = "CuteR";

//     private static final int WHITE = 0xFFFFFFFF;
//     private static final int BLACK = 0xFF000000;

//     private static int[] patternCenters;
//     private static int scaleQR;
//     private static final int MAX_INPUT_GIF_SIZE = 480;
//     private static final int SCALE_NORMAL_QR = 10;

//     private static final float FULL_LOGO_QR = 507.1f;
//     private static final float LOGO_BACKGROUND = 140.7f;
//     private static final float LOGO_SIZE = 126.7f;

//     private static final int MAX_LOGO_SIZE = 1080;

//     public static Bitmap Product(String txt, Bitmap input, boolean colorful, int color){
//         Log.d(TAG, "Products start input input.getWidth(): " + input.getWidth() + " input.getHeight(): " + input.getHeight());
//         Bitmap QRImage = null;
//         try {
//             QRImage = encodeAsBitmap(txt);
//         } catch (WriterException e) {
//             Log.e(TAG, "encodeAsBitmap: " + e);
//         }

//         if (colorful && color != Color.BLACK) {
//             QRImage = replaceColor(QRImage, color);
//         }

//         int inputSize = Math.max(input.getWidth(), input.getHeight());
//         int scale = (int) Math.ceil(1.0 * inputSize / QRImage.getWidth());
//         if (scale % 3 != 0) {
//             scale += (3 - scale % 3);
//         }

//         scaleQR = scale;
//         Bitmap scaledQRImage = Bitmap.createScaledBitmap(QRImage, QRImage.getWidth() * scale, QRImage.getHeight() * scale, false);

//         int imageSize = 0;
//         Bitmap resizedImage = null;
//         if (input.getWidth() < input.getHeight()) {
//             resizedImage = Bitmap.createScaledBitmap(input, scaledQRImage.getWidth() - scale  * 4 * 2, (int)((scaledQRImage.getHeight() - scale  * 4 * 2) * (1.0 * input.getHeight() / input.getWidth())), false);
//             imageSize = resizedImage.getWidth();
//         } else {
//             resizedImage = Bitmap.createScaledBitmap(input, (int)((scaledQRImage.getWidth() - scale  * 4 * 2) * (1.0 * input.getWidth() / input.getHeight())), scaledQRImage.getHeight() - scale  * 4 * 2, false);
//             imageSize = resizedImage.getHeight();
//         }
// //
// //        if (patternCenters == null || patternCenters.length == 0) {
// //            Log.e(TAG, "patternCenters == null || patternCenters.length == 0");
// //            return null;
// //        }

//         int[][] pattern = new int[scaledQRImage.getWidth() - scale  * 4 * 2][scaledQRImage.getWidth() - scale  * 4 * 2];

//         for (int i = 0; i < patternCenters.length; i++) {
//             for (int j = 0; j < patternCenters.length; j++) {
//                 if (patternCenters[i] == 6 && patternCenters[j] == patternCenters[patternCenters.length - 1] ||
//                         (patternCenters[j] == 6 && patternCenters[i] == patternCenters[patternCenters.length - 1]) ||
//                         (patternCenters[i] == 6 && patternCenters[j] == 6)) {
//                     continue;
//                 } else {
//                     int initx = scale * (patternCenters[i] - 2);
//                     int inity = scale * (patternCenters[j] - 2);
//                     for (int x = initx; x < initx + scale * 5; x++) {
//                         for (int y = inity; y < inity + scale * 5; y++) {
//                             pattern[x][y] = 1;
//                         }
//                     }
//                 }
//             }
//         }

//         Bitmap blackWhite = resizedImage;
//         if (colorful == false) {
//             blackWhite = convertBlackWhiteFull(blackWhite);
//         }


// @SuppressLint("SetTextI18n")
//     private fun showDateDialogPicker() {
//         val builder = MaterialDatePicker.Builder.dateRangePicker()
//         builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
//         val picker = builder.build()
//         picker.show(childFragmentManager, picker.toString())
//         binding?.rvProductInventories?.pbLoading?.visibility = View.GONE
//         picker.addOnCancelListener {
//             picker.dismiss()
//         }

//         picker.addOnNegativeButtonClickListener {
//             picker.dismiss()
//         }

//         picker.addOnPositiveButtonClickListener {
//             if (it.first != null && it.second != null) {

//                 currentStartDate = DateTimeUtils.epochToYMDDate(it.first!! / 1000)
//                 currentEndDate = DateTimeUtils.epochToYMDDate(it.second!! / 1000)
//                 getProductListAndRefresh()

//                 val startDate = DateTimeUtils.epochToDMY(it.first!! / 1000)
//                 val endDate = DateTimeUtils.epochToDMY(it.second!! / 1000)
//                 binding?.tvtextDateTimeAndMonth?.text = "$startDate - $endDate"
//                 binding?.rltextDateTimeAndMonth?.visibility = View.VISIBLE
//             }
//             picker.dismiss()
//         }
//     }


            

