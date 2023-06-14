package com.smartcut.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var id: String? = null,
    var name: String? = null,
    var username: String? = null,
    var email: String? = null,
    var password: String? = null,
    var phone: String? = null,
    var photoUrl: String? = null,
    var token: String? = null
): Parcelable