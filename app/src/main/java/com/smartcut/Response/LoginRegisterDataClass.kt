package com.smartcut.Response

data class LoginRegisterDataClass(
    val name: String? = null,
    val username: String,
    val password: String,
    val email: String? = null,
    val phone: String? = null,
)