package com.arifahmadalfian.sukamanahkas.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.arifahmadalfian.sukamanahkas.R
import com.arifahmadalfian.sukamanahkas.data.model.Kas
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class PdfUtils(
    val context: Context,
    val size: Int,
    val kas: ArrayList<Kas>,
    val totalPemasukan: Int
) {
    private var pageSizeWidth58: Float = 168f
    private val PADDING_DEFAULT = 10f

    private val FONT_SIZE_DEFAULT = 12f
    private val FONT_SIZE_BIG = 37f
    private val FONT_SIZE_SMALL = 17f
    private val FONT_SIZE_REGULAR = 20f
    private val FONT_SMALL = 12f


    private var basefontRegular: BaseFont =
        BaseFont.createFont("res/font/product_sans_regular.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontRegular = Font(basefontRegular, FONT_SIZE_DEFAULT)

    private var basefontRegularBold: BaseFont =
        BaseFont.createFont("res/font/product_sans_regular.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontRegularBold = Font(basefontRegularBold, FONT_SIZE_REGULAR)

    private var basefontSmall: BaseFont =
        BaseFont.createFont("res/font/product_sans_regular.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontSmall = Font(basefontSmall, FONT_SIZE_SMALL)

    private var basefontSmalls: BaseFont =
        BaseFont.createFont("res/font/product_sans_regular.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontSmalls = Font(basefontSmall, FONT_SMALL)

    private var basefontBold: BaseFont =
        BaseFont.createFont("res/font/product_sans_bold.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontBold = Font(basefontBold, FONT_SIZE_DEFAULT)

    private var basefontBoldBig: BaseFont =
        BaseFont.createFont("res/font/product_sans_bold.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontBoldBig = Font(basefontBoldBig, FONT_SIZE_BIG)



    fun printThermal58() {
        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        // 297 width A6
//                        val height: Float = (150 + (size * 50)).toFloat()
//                        val width: Rectangle = RectangleReadOnly(297f, height)

                        val doc = Document(PageSize.A6, 5f, 5f, 0f, 0f)
                        val outPath = context.applicationContext.getExternalFilesDir(null)
                            .toString() + "/laporanKas.pdf"
                        val outPutStream = FileOutputStream(outPath)
                        val writer = PdfWriter.getInstance(doc, outPutStream) // init doc
                        val file = File(outPath)

                        doc.open()
                        doc.setMargins(0f, 0f, PADDING_DEFAULT, PADDING_DEFAULT)

                        initHeader(doc)
                        initLineFooter(doc)
                        initAlamat(doc)
                        initLine(doc)
                        initContent(doc)
                        initLineFooter(doc)
                        initTotal(doc)
                        doc.close()

                        val path: Uri = FileProvider.getUriForFile(
                            context,
                            "com.arifahmadalfian.sukamanahkas.provider",
                            file
                        )
                        Log.d( "LocationUri",path.toString())
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(path, "application/pdf")
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.toast("There is no PDF Viewer ")
                        }

                    } else {
                        context.toast("permissions missing :(")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initHeader(doc: Document) {
        val d = context.resources.getDrawable(R.drawable.rw)
        val bitDw = d as BitmapDrawable
        val bitmap = bitDw.bitmap

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = Image.getInstance(stream.toByteArray())
        image.scaleToFit(70f, 70f)

        val headerTable = PdfPTable(2)
        headerTable.setWidths(
            floatArrayOf(
                1f,
                4f
            )
        )
        headerTable.isLockedWidth = true
        headerTable.totalWidth = 290f

        val icon = PdfPCell(Image.getInstance(image)).apply {
            paddingTop = 10f
            border = Rectangle.NO_BORDER
            horizontalAlignment = Element.ALIGN_LEFT
            verticalAlignment = Element.ALIGN_CENTER
        }

        val title = PdfPCell(
            Paragraph(
                "Laporan Kas",
                appFontBoldBig
            )
        ).apply {
            border = PdfPCell.NO_BORDER
            paddingTop = 19f
            horizontalAlignment = Element.ALIGN_CENTER
            verticalAlignment = Element.ALIGN_CENTER
        }

        headerTable.addCell(icon)
        headerTable.addCell(title)

        doc.add(headerTable)

    }

    private fun initAlamat(doc: Document) {
        val alamatAlamat = PdfPTable(1).apply {
            setWidths(floatArrayOf(1f))
            isLockedWidth = true
            horizontalAlignment = Element.ALIGN_CENTER
            totalWidth = 290f
        }
        val cellAlamat = PdfPCell(
            Paragraph(
                "Kp. Sukamanah RW 12 Desa Nengkelan",
                appFontSmall
            )
        ).apply {
            border = PdfPCell.NO_BORDER
            paddingBottom = 8f
            verticalAlignment = Element.ALIGN_CENTER
        }
        alamatAlamat.addCell(cellAlamat)
        doc.add(alamatAlamat)
    }

    private fun initLine(doc: Document) {
        val lineTable = PdfPTable(1).apply {
            setWidths(floatArrayOf(1f))
            isLockedWidth = true
            paddingTop = 8f
            horizontalAlignment = Element.ALIGN_CENTER
            totalWidth = 290f
        }
        val cellLine = PdfPCell(
            Paragraph(
                "***************************************************",
                appFontBold
            )
        ).apply {
            border = PdfPCell.NO_BORDER
        }
        lineTable.addCell(cellLine)
        doc.add(lineTable)
    }

    private fun initContent(doc: Document) {
        val kasTableBase = PdfPTable(1).apply {
            setWidths(floatArrayOf(1f))
            isLockedWidth = true
            horizontalAlignment = Element.ALIGN_CENTER
            totalWidth = 290f
        }

        val kasTable1= PdfPTable(2).apply {
            setWidths(floatArrayOf(2f, 1f))
            isLockedWidth = true
            totalWidth = 290f
            horizontalAlignment = Element.ALIGN_CENTER
        }

        val kasTable2= PdfPTable(2).apply {
            setWidths(floatArrayOf(2f, 1f))
            isLockedWidth = true
            totalWidth = 290f
            horizontalAlignment = Element.ALIGN_CENTER
        }

        for (item in kas) {
            kasTable1.deleteBodyRows()
            kasTable2.deleteBodyRows()

            val namaCell = PdfPCell(Phrase(item.name?.toCapitalize(), appFontRegularBold))
            namaCell.border = Rectangle.NO_BORDER
            namaCell.horizontalAlignment = Rectangle.ALIGN_LEFT
            kasTable1.addCell(namaCell)

            val pemasukanCell = PdfPCell(Phrase(item.inclusion, appFontRegularBold))
            pemasukanCell.border = Rectangle.NO_BORDER
            pemasukanCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
            kasTable1.addCell(pemasukanCell)

            val createAtCell = PdfPCell(Phrase(item.createAt?.toLong()?.epochToDateTime(HHDMY), appFontSmalls))
            createAtCell.border = Rectangle.NO_BORDER
            createAtCell.horizontalAlignment = Rectangle.ALIGN_LEFT
            kasTable2.addCell(createAtCell)

            val createByCell = PdfPCell(Phrase("by ${item.createBy?.lowercase()}",appFontSmalls))
            createByCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
            createByCell.border = Rectangle.NO_BORDER
            kasTable2.addCell(createByCell)

            val items1 = PdfPCell(kasTable1).apply {
                border = Rectangle.NO_BORDER
            }

            val items2 = PdfPCell(kasTable2).apply {
                border = Rectangle.NO_BORDER
                paddingBottom = 4f
            }

            kasTableBase.addCell(items1)
            kasTableBase.addCell(items2)
        }

        doc.add(kasTableBase)

    }

    private fun initLineFooter(doc: Document) {
        val lineTable = PdfPTable(1).apply {
            setWidths(floatArrayOf(1f))
            isLockedWidth = true
            horizontalAlignment = Element.ALIGN_CENTER
            totalWidth = 290f
        }
        val cellLine = PdfPCell(
            Paragraph(
                "***************************************************",
                appFontBold
            )
        ).apply {
            border = PdfPCell.NO_BORDER
        }
        lineTable.addCell(cellLine)
        doc.add(lineTable)
    }

    private fun initTotal(doc: Document) {
        val totalpemasukan = PdfPTable(1).apply {
            setWidths(floatArrayOf(1f))
            isLockedWidth = true
            horizontalAlignment = Element.ALIGN_CENTER
            totalWidth = 290f
        }
        val total= PdfPTable(2).apply {
            setWidths(floatArrayOf(1f, 4f))
            isLockedWidth = true
            totalWidth = 290f
            horizontalAlignment = Element.ALIGN_CENTER
        }

        val totalCell = PdfPCell(Phrase("Total", appFontRegularBold))
        totalCell.border = Rectangle.NO_BORDER
        totalCell.paddingTop = 8f
        totalCell.horizontalAlignment = Rectangle.ALIGN_LEFT
        totalCell.verticalAlignment = Rectangle.ALIGN_CENTER
        total.addCell(totalCell)

        val pemasukanCell = PdfPCell(Phrase("Rp ${totalPemasukan.toLong().numberToCurrency()}", appFontBoldBig))
        pemasukanCell.border = Rectangle.NO_BORDER
        pemasukanCell.bottom = 16f
        pemasukanCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalCell.verticalAlignment = Rectangle.ALIGN_CENTER
        total.addCell(pemasukanCell)

        val items = PdfPCell(total).apply {
            border = Rectangle.NO_BORDER
        }
        totalpemasukan.addCell(items)
        doc.add(totalpemasukan)
    }



}