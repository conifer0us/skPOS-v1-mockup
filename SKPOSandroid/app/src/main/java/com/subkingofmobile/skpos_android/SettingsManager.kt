package com.subkingofmobile.skpos_android

import android.content.Context
import kotlin.random.Random
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SettingsManager(applicationContext : Context) {
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val sharedPreferencesHandle : SharedPreferences

    init {
        sharedPreferencesHandle = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    fun getAccessKey() : String? {
        if (! keyExists(R.string.access_key_pref_key.toString())) {
            val randomString = generateRandomString(256)
            writeKeyValue(R.string.access_key_pref_key.toString(), randomString)
        }
        return getValFromKey(R.string.access_key_pref_key.toString())
    }

    fun getServerIP() : String? {
        return getValFromKey(R.string.IP_pref_key.toString())
    }

    fun getServerPort() : Int {
        return getNumFromKey(R.string.port_pref_key.toString())
    }

    fun getValFromKey(key : String) : String? {
        return sharedPreferencesHandle.getString(key, null)
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
        if (key in sharedPreferencesHandle.all) {
            return true
        }
        return false
    }

    private fun generateRandomString(length : Int) : String {
        return (1..length).map{_ -> Random.nextInt(0, charPool.size)}.map(charPool::get).joinToString("")
    }

    private fun getFileKey() : String {
        return R.string.access_key_file_name.toString()
    }
}