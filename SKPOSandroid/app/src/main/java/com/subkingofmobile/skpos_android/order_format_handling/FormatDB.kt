package com.subkingofmobile.skpos_android.order_format_handling

import android.app.Activity
import android.content.Context
import com.subkingofmobile.skpos_android.ConnectionHandler
import com.subkingofmobile.skpos_android.SettingsManager
import org.json.JSONObject

class FormatDB(appContext : Context, currentActivity : Activity) {
    private val connectionHandler : ConnectionHandler
    private val settingsManager : SettingsManager

    init {
        connectionHandler = ConnectionHandler(appContext, currentActivity)
        settingsManager = SettingsManager(appContext)
    }

    private fun getCurrentFormatID(onCompletion : (ID : String) -> Unit, onFailure : (errormsg : String) -> Unit) {

    }

    fun getCurrentFormatData(onCompletion: (formatData : JSONObject) -> Unit, onFailure: (errormsg : String) -> Unit, exitPage : Boolean) {

    }

    fun getFormatDataByID(onCompletion: (formatData: JSONObject) -> Unit, onFailure: (errormsg: String) -> Unit) {
        
    }
}