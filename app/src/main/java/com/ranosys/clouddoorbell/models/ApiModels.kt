package com.ranosys.clouddoorbell.models

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

/**
 * @author Ranosys Technologies
 */

class DoorbellRequestModel(@SerializedName("data")
                           @Expose var imgData: String?,
                           @SerializedName("annotation")
                           @Expose var annotations: String?,
                           @SerializedName("timestamp")
                           @Expose var timestamp: String?)

class DoorbellResponseModel


