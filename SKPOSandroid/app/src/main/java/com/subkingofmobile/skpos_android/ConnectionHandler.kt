package com.subkingofmobile.skpos_android

import android.app.Activity
import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ConnectionHandler(appContext : Context, currentActivity : Activity) {
    private val settingsManagerObject : SettingsManager
    private val requestQueue : RequestQueue
    private val activityObject : Activity
    private val JSONTESTRESOURCE = "https://jsonplaceholder.typicode.com/todos/1"


    init {
        settingsManagerObject = SettingsManager(appContext)
        requestQueue = Volley.newRequestQueue(appContext)
        activityObject = currentActivity
    }

    private fun getServerPort() : Int{
        return settingsManagerObject.getServerPort()
    }

    private fun getServerIP() : String? {
        return settingsManagerObject.getServerIP()
    }

    private fun getCookieVal() : String? {
        return settingsManagerObject.getAccessKey()
    }

    // A method that leverages Volley to send an Asynchronous HTTP(S) request with specified methods to execute when the connection is completed or fails
    // Arguments:
    // uri: indicates the resource to get at the requested destination (defaults to no uri, a blank string)
    // dest: indicates the external location to search for the uri (can be a url or IP address); this defaults to whatever is stored locally as the skPOS server IP
    // port: indicates the port that should be used to connect to the destination (the default port is -1, which defaults to the port stored for the external skPOS server, supplying a negative port besides -1 will result in no port being used)
    // method: indicates the HTTP Method that should be used for the connection (defaults to GET)
    // data: indicates the data that should be included in the body of the request
    // authenticate: indicates whether an authentication cookie should be added to the request body
    // onCompletion: a method that will run on the UI thread if the connection completes successfully (this message is supplied with a JSON response from the server as an argument)
    // onFailure: a method that will run on the UI thread if the connection fails (this method is supplied a string with an error message as an argument)
    private fun asyncRequest(uri : String = "", dest : String? = null, port : Int = -1, method : String = "GET", data : String = "", authenticate : Boolean = true,
                     onCompletion: (resp : JSONObject?) -> Unit, onFailure: (errormsg : String?) -> Unit) {
        var dest = dest
        var port = port
        if (dest == null) {
            dest = getServerIP()
        }
        if (port == -1) {
            port = getServerPort()
        }
        val url : String = "%s%s%s".format(dest, formatURI(uri), formatPort(port))
        val httpmethod : Int = httpMethodFromString(method)
        val bodyJSON : JSONObject = buildBodyText(data, authenticate)
        val jsonReq : JsonObjectRequest = object : JsonObjectRequest(
            httpmethod, url, bodyJSON,
            { response ->  activityObject.runOnUiThread {onCompletion(response)} },
            { error -> activityObject.runOnUiThread {onFailure(error.message)} }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                val resp = super.parseNetworkResponse(response)
                resp.result!!.put("statusCode", response!!.statusCode)
                return resp
            }
        }
        requestQueue.add(jsonReq)
    }

    fun isServerUp(onCompletion: (resp: JSONObject?) -> Unit, onFailure: (errormsg: String?) -> Unit) {
        asyncRequest(uri = "/servertest", authenticate = false, onCompletion = onCompletion, onFailure = onFailure)
    }

    fun isDeviceConnected(onCompletion: (resp: JSONObject?) -> Unit, onFailure: (errormsg: String?) -> Unit) {
        asyncRequest(dest = JSONTESTRESOURCE, port = -2, authenticate = false, onCompletion = onCompletion, onFailure = onFailure)
    }

    private fun httpMethodFromString(methodString : String) : Int {
        return when (methodString) {
            "GET" -> {
                Request.Method.GET
            }
            "POST" -> {
                Request.Method.POST
            }
            "PUT" -> {
                Request.Method.PUT
            }
            "DELETE" -> {
                Request.Method.DELETE
            }
            else -> {
                Request.Method.PATCH
            }
        }
    }

    private fun buildBodyText(data : String, authenticate : Boolean) : JSONObject {
        val dataJSON = JSONObject()
        dataJSON.put("body", data)
        return buildBodyText(dataJSON, authenticate)
    }

    private fun buildBodyText(data : JSONObject, authenticate: Boolean) : JSONObject {
        if (authenticate) {
            val cookie : String = getCookieVal()!!
            data.put("cookie", cookie)
        }
        return data
    }

    private fun formatURI(input : String) : String {
        var input = input
        if (input.isNotEmpty() && ! input.startsWith("/")) {
            input = "/$input"
        }
        return input
    }

    private fun formatPort(input : Int) : String {
        var input = input
        return if (input < 0) {
            ""
        } else {
            ":$input"
        }
    }
}