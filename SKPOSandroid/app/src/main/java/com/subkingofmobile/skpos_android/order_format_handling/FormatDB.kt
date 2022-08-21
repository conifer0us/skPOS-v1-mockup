package com.subkingofmobile.skpos_android.order_format_handling

import android.app.Activity
import android.content.Context
import com.subkingofmobile.skpos_android.ConnectionHandler
import com.subkingofmobile.skpos_android.DialogBox
import com.subkingofmobile.skpos_android.SettingsManager
import org.json.JSONObject

class FormatDB(appContext : Context, currentActivity : Activity) {
    private val connectionHandler : ConnectionHandler
    private val settingsManager : SettingsManager
    private val dialog : DialogBox

    init {
        connectionHandler = ConnectionHandler(appContext, currentActivity)
        settingsManager = SettingsManager(appContext)
        dialog = DialogBox(appContext)
    }

    fun getCurrentFormatID(onLoadID : (ID : String) -> Unit, onConnectionFailure : (errormsg : String) -> Unit) {
        connectionHandler.asyncRequest(uri="/currentOrderFormat", port=-2, method = "POST", authenticate = true, onCompletion = {
            resp -> onLoadID(resp.getString("ID"))
        }, onFailure = onConnectionFailure)
    }

    fun getCurrentFormatData(onLoadFormatData: (formatData : JSONObject) -> Unit, onFailure: (errormsg : String) -> Unit) {
        getCurrentFormatID(onLoadID = { formatID ->
            if (settingsManager.keyExists(formatID)) {
                onLoadFormatData(settingsManager.getJSONFromKey(formatID))
            } else {
                connectionHandler.asyncRequest(uri = "formatDataByID", method = "POST", port = -2, authenticate = true, data = JSONObject("{'orderID':'${formatID}'}"),
                onCompletion = { formatData ->
                    settingsManager.writeKeyValue(formatID, formatData)
                    onLoadFormatData(formatData)
                }, onFailure = onFailure)
            }
        }, onConnectionFailure = onFailure)
    }
}