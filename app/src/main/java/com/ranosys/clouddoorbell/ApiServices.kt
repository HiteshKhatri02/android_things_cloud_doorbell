package com.ranosys.clouddoorbell


import com.ranosys.clouddoorbell.models.DoorbellRequestModel
import com.ranosys.clouddoorbell.models.DoorbellResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author Ranosys Technologies
 */
interface ApiServices {
@POST("api.php")
    fun sendEntry(@Query("method") string:String, @Body doorbellRequestModel: DoorbellRequestModel):Call<DoorbellResponseModel>
}