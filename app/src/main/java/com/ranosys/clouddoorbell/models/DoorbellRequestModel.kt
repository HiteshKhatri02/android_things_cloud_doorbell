package com.ranosys.clouddoorbell.models

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Ranosys Technologies
 */
class DoorbellRequestModel(@SerializedName("data")
                           @Expose var imgData: String?, @SerializedName("annotation")
                           @Expose var annotations: String?, @SerializedName("timestamp")
                           @Expose var timestamp: Long?) {

    fun getJson():String{
        val gson = Gson()
        return gson.toJson(DoorbellRequestModel(imgData,annotations,timestamp))
    }

}