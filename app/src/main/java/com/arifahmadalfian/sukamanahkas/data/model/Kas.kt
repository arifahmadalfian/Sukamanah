package com.arifahmadalfian.sukamanahkas.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kas(
    val createAt: String?,
    val createBy: String?,
    val inclusion: String?,
    val id: String?,
    val name: String?,
    val profile: String?,
): Parcelable
