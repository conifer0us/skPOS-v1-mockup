package com.subkingofmobile.skpos_android

import org.json.JSONObject

class HttpResponse(ResponseText : JSONObject?, StatusCode : Int) {
    private val respText : JSONObject?
    private val statusCode : Int

    init {
        respText = ResponseText
        statusCode = StatusCode
    }

    fun json() : JSONObject? {
        return respText
    }

    fun status() : Int {
        return statusCode
    }
}