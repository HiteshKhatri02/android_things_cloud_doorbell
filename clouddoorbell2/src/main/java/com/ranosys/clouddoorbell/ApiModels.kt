package com.ranosys.clouddoorbell

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Ranosys Technologies
 */

class GetDataResponse(@SerializedName("image")
                           @Expose var imgData: String?, @SerializedName("annotation")
                           @Expose var annotations: String?, @SerializedName("timestamp")
                           @Expose var timestamp: Long?)

class GetDataRequest(@SerializedName("device_token")
                     @Expose var deviceToken: String?)