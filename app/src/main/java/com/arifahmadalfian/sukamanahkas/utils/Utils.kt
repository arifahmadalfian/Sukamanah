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

iv_scan_qr_member.setOnClickListener {
            val intent = Intent(requireContext(), ScanViewActivity::class.java)
            startActivityForResult(intent, SCAN_MEMBER)
        }

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SCAN_MEMBER) {
                    val result = data.getStringExtra(RESULT_SCAN).toString()
                    lyt_input_scan_member_id.editText?.setText(result)
                    viewModel.getMemberIndie(result)
                    isAddNewMemberScreen = false
                    cv_member_result.visibility = View.VISIBLE
                    tv_not_found.visibility = View.GONE
                }
            }
        }
    }


class ScanViewActivity: AppCompatActivity() {

    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA
    )

    private var mDisposable: Disposable? = null

    lateinit var animationScan: Animation

    private var _binding:  ActivityScanQrLoginBinding? = null
    private val binding get() = _binding
    var position :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScanQrLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        position = intent.getStringExtra("position")

        if (!hasPermissions(this, *PERMISSIONS)) ActivityCompat.requestPermissions(
            this, PERMISSIONS, 1
        )

        mDisposable = binding?.barcodeView
            ?.getObservable()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ barcode ->
                val returnIntent = Intent()
                returnIntent.putExtra(RESULT_SCAN, barcode.displayValue.toString())
                if (position != null){
                    returnIntent.putExtra("position",position)
                }
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }, { throwable ->
                Timber.e(throwable.toString())
            })

        binding?.btnFlashLogin?.setOnClickListener {
            if (binding?.ivFlashOnLogin?.visibility == View.VISIBLE) {
                binding?.barcodeView?.setFlash(true)
                binding?.ivFlashOnLogin?.visibility = View.GONE
                binding?.ivFlashOffLogin?.visibility = View.VISIBLE
            } else {
                binding?.barcodeView?.setFlash(false)
                binding?.ivFlashOnLogin?.visibility = View.VISIBLE
                binding?.ivFlashOffLogin?.visibility = View.GONE
            }
        }

        animationScan = AnimationUtils.loadAnimation(this, R.anim.scan_qr_code)
        animationScan.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding?.bar?.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })

        binding?.bar?.startAnimation(animationScan)
    }

    override fun onStop() {
        super.onStop()
        mDisposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/colorPrimary"
    tools:context=".view.checkLicense.scanQrLicense.ScanQrLoginActivity">

    <ImageView
        android:id="@+id/imageView7"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:src="@drawable/indiepos"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <com.bobekos.bobek.scanner.BarcodeView
        android:id="@+id/barcodeView"
        app:setAutoFocus="true"
        android:layout_width="match_parent"
        app:layout_constraintTop_toBottomOf="@id/guideline_start"
        app:layout_constraintBottom_toBottomOf="@id/guideline_bottom"
        android:layout_height="0dp"/>

    <View
        android:id="@+id/bar"
        android:layout_width="411dp"
        android:layout_height="100dp"
        android:background="@drawable/bg_scan"
        android:visibility="visible"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@+id/barcodeView" />

    <RelativeLayout
        android:id="@+id/btn_flash_login"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:layout_margin="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="@+id/barcodeView">

        <ImageView
            android:id="@+id/iv_flash_on_login"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:layout_centerInParent="true"
            android:visibility="visible"
            android:src="@drawable/ic_flash_on_white"
            tools:ignore="VectorDrawableCompat" />

        <ImageView
            android:id="@+id/iv_flash_off_login"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:layout_centerInParent="true"
            android:visibility="gone"
            android:src="@drawable/ic_flash_off_white_24dp"
            tools:ignore="VectorDrawableCompat" />
    </RelativeLayout>

    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline_start"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_percent="0.15" />

    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline_bottom"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_percent="0.95" />
</androidx.constraintlayout.widget.ConstraintLayout>
    
    
    implementation 'com.github.bobekos:SimpleBarcodeScanner:1.0.23'

<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">

    <!--  Gradient Bg for listrow -->
    <gradient
        android:startColor="#9715CB8C"
        android:endColor="#0015cb8c"
        android:angle="270" />
</shape>
    
    <vector android:height="24dp" android:tint="#FFFFFF"
    android:viewportHeight="24.0" android:viewportWidth="24.0"
    android:width="24dp" xmlns:android="http://schemas.android.com/apk/res/android">
    <path android:fillColor="#FF000000" android:pathData="M7,2v11h3v9l7,-12h-4l4,-8z"/>
</vector>
    
    <vector android:height="24dp" android:tint="#FFFFFF"
    android:viewportHeight="24.0" android:viewportWidth="24.0"
    android:width="24dp" xmlns:android="http://schemas.android.com/apk/res/android">
    <path android:fillColor="#FF000000" android:pathData="M3.27,3L2,4.27l5,5V13h3v9l3.58,-6.14L17.73,20 19,18.73 3.27,3zM17,10h-4l4,-8H7v2.18l8.46,8.46L17,10z"/>
</vector>
            

