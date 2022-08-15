package com.subkingofmobile.skpos_android

import android.content.Context
import kotlin.random.Random
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager

class SettingsManager(applicationContext : Context) {
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val sharedPreferencesHandle : SharedPreferences
    private val applicationContext : Context

    init {
        sharedPreferencesHandle = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        this.applicationContext = applicationContext
    }

    fun getAccessKey() : String {
        if (! keyExists(applicationContext.getString(R.string.access_key_pref_key))) {
            val randomString = generateRandomString(256)
            writeKeyValue(applicationContext.getString(R.string.access_key_pref_key), randomString)
        }
        return getValFromKey(applicationContext.getString(R.string.access_key_pref_key))
    }

    fun getServerIP() : String {
        return getValFromKey(applicationContext.getString(R.string.IP_pref_key))
    }

    fun getServerPort() : Int {
        return getValFromKey(applicationContext.getString(R.string.port_pref_key)).toInt()
    }

    fun getValFromKey(key : String) : String {
        return sharedPreferencesHandle.getString(key, "")!!
    }

    fun getNumFromKey(key : String) : Int {
        return sharedPreferencesHandle.getInt(key, -1)
    }

    private fun writeKeyValue(key : String, value : String) {
        val prefEditor = sharedPreferencesHandle.edit()
        prefEditor.putString(key, value)
        prefEditor.apply()
    }

    private fun writeKeyValue(key :String, value : Int) {
        val prefEditor = sharedPreferencesHandle.edit()
        prefEditor.putInt(key, value)
        prefEditor.apply()
    }

    private fun keyExists(key : String) : Boolean {
        return (key in sharedPreferencesHandle.all)
    }

    private fun generateRandomString(length : Int) : String {
        return (1..length).map{_ -> Random.nextInt(0, charPool.size)}.map(charPool::get).joinToString("")
    }
}