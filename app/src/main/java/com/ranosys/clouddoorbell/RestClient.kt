package com.ranosys.clouddoorbell

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Ranosys Technologies
 */
class RestClient {
    companion object {

        private val getURL = "http://192.168.18.104/Cloud_Doorbell/"
        private var apiServices: ApiServices? = null

        fun getGetClient(): ApiServices? {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okClient = OkHttpClient.Builder()
            okClient.connectTimeout(150, TimeUnit.SECONDS)
            okClient.readTimeout(150, TimeUnit.SECONDS)
            okClient.addInterceptor(logging)
            okClient.addInterceptor { chain -> chain.proceed(chain.request()) }
            val client = Retrofit.Builder()
                    .baseUrl(getURL)
                    .client(okClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            apiServices = client.create(ApiServices::class.java)
            return apiServices
        }
    }

}
