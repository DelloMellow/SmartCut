package com.smartcut.Model

import android.content.Context

internal class UserPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(value: UserModel) {
        val editor = preferences.edit()
        editor.putString(ID, value.id)
        editor.putString(NAME, value.name)
        editor.putString(USERNAME, value.username)
        editor.putString(EMAIL, value.email)
        editor.putString(PHONE, value.phone)
        editor.putString(PASSWORD, value.password)
        editor.putString(PHOTO_URL, value.photoUrl)
        editor.putString(TOKEN, value.token)
        editor.apply()
    }

    fun getUser(): UserModel {
        val model = UserModel()
        model.id = preferences.getString(ID, "")
        model.name = preferences.getString(NAME, "")
        model.username = preferences.getString(USERNAME, "")
        model.email = preferences.getString(EMAIL, "")
        model.phone = preferences.getString(PHONE, "")
        model.password = preferences.getString(PASSWORD, "")
        model.photoUrl = preferences.getString(PHOTO_URL, "")
        model.token = preferences.getString(TOKEN, "")
        return model
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val ID = "id"
        private const val NAME = "name"
        private const val USERNAME = "username"
        private const val EMAIL = "email"
        private const val PHONE = "phone"
        private const val PASSWORD = "password"
        private const val PHOTO_URL = "photo_url"
        private const val TOKEN = "token"
    }
}