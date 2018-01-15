package com.ranosys.clouddoorbell

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private var modelList : MutableList<GetDataResponse>? = arrayListOf()
    var recyclerView : RecyclerView? = null
    var doorbellAdapter : DoorbellAdapter? = null
    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<RecyclerView>(R.id.doorbellView)
        recyclerView?.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        doorbellAdapter = DoorbellAdapter(this, modelList!!, object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity,"Item has been clicked",
                        Toast.LENGTH_SHORT).show()
            }

        })
        recyclerView?.adapter = doorbellAdapter
        val deviceToken = SavedPreference(this).getDeviceToken()

        if (!TextUtils.isEmpty(deviceToken)) {
            Log.d(MyFirebaseMessagingService.TAG, "Refresh Token " + deviceToken)
            getEntries(GetDataRequest(deviceToken))
        }
    }


    private fun getEntries(deviceToken:GetDataRequest){
        val service  = RestClient.getGetClient()
        val call = service?.getEntries("getdata",deviceToken)
        call?.enqueue(object : Callback<MutableList<GetDataResponse>?> {

            override fun onResponse(call: Call<MutableList<GetDataResponse>?>?, response: Response<MutableList<GetDataResponse>?>?) {
                if (response!!.isSuccessful){
                    if (response.body()?.isNotEmpty()!!) {
                        Log.d(TAG, response.body().toString())
                        modelList = response.body()
                        doorbellAdapter?.updateList(modelList!!)
                        Log.d(TAG, recyclerView?.adapter?.itemCount.toString())
                    }
                }else{

                }
            }

            override fun onFailure(call: Call<MutableList<GetDataResponse>?>?, t: Throwable?) {
                Log.d(TAG, "Error occurred.")
            }

        })
    }

}
