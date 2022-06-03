package com.subkingofmobile.skpos_android

import android.app.Activity
import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ConnectionHandler(appContext : Context) {
    private val settingsManagerObject : SettingsManager
    private val requestQueue : RequestQueue

    init {
        settingsManagerObject = SettingsManager(appContext)
        requestQueue = Volley.newRequestQueue(appContext)
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

    // A method that uses volley to send synchronous HTTP(S) requests
    fun synchronousRequest(uri : String, method : String = "GET", data : String = "", authenticate : Boolean = true) : HttpResponse {
        val httpmethod : Int = httpMethodFromString(method)
        val url : String = "%s/%s".format(getServerIP(), uri)
        val bodyJSON : JSONObject = buildBodyText(data, authenticate)
        var statusCode : Int = 600 //Assume Network Fail, future.get() calls the parseNetworkResponse function if successful, which will change the status code
        var responseJSON : JSONObject?

        val future : RequestFuture<JSONObject> = RequestFuture.newFuture()
        val jsonReq : JsonObjectRequest = object : JsonObjectRequest(httpmethod, url, bodyJSON, future, future){
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                statusCode = response!!.statusCode
                return super.parseNetworkResponse(response)
            }
        }
        requestQueue.add(jsonReq)

        try {
            responseJSON = future.get(30, TimeUnit.SECONDS)
        } catch (e : InterruptedException) {
            responseJSON = JSONObject()
            responseJSON.put("err", e.message)
        } catch (e : ExecutionException) {
            responseJSON = JSONObject()
            responseJSON.put("err", e.message)
        } catch(e : TimeoutException) {
            responseJSON = JSONObject()
            responseJSON.put("err", e.message)
        }
        return HttpResponse(responseJSON, statusCode)
    }

    // A method that leverages Volley to send an Asynchronous HTTP(S) request with specified methods to execute when the connection is completed or fails
    fun asyncRequest(uri : String, method : String = "GET", data : String = "", authenticate : Boolean = true,
                     onCompletion: (resp : JSONObject?) -> Boolean, onFailure: (errormsg : String?) -> Boolean, activityObject : Activity) {
        val url : String = "%s/%s".format(getServerIP(), uri)
        val httpmethod : Int = httpMethodFromString(method)
        val bodyJSON : JSONObject = buildBodyText(data, authenticate)
        val jsonReq = JsonObjectRequest(
            httpmethod, url, bodyJSON,
            { response ->  activityObject.runOnUiThread {onCompletion(response)} },
            { error -> activityObject.runOnUiThread {onFailure(error.message)} }
        )
        requestQueue.add(jsonReq)
    }

    fun isServerUp() : Boolean {
        val testConnection = synchronousRequest(uri = "servertest", authenticate = false)
        if (testConnection.status() in 200..299) {
            return true
        }
        return false
    }

    fun isDeviceConnected() : Boolean {

    }

    fun isDeviceRegistered() : Boolean {

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
        var dataJSON = JSONObject()
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
}