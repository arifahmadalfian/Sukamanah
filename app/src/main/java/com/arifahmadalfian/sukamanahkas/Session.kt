package com.arifahmadalfian.sukamanahkas

import android.content.Context
import android.content.SharedPreferences

class Session(var context: Context) {
    var preferences: SharedPreferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = preferences.edit()

    fun setLoggedin(loggedin: Boolean) {
        editor.putBoolean("loggedInmode", loggedin)
        editor.apply()
    }

    fun loggedIn(): Boolean {
        return preferences.getBoolean("loggedInmode", false)
    }

}