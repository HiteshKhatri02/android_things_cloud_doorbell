package com.ranosys.clouddoorbell

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author Ranosys Technologies
 */
interface ApiServices {

    @POST("api.php")

    fun getEntries(@Query("method") city:String, @Body doorbellRequest: GetDataRequest): Call<MutableList<GetDataResponse>?>
}