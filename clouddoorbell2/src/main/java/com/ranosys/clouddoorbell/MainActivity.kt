package com.ranosys.clouddoorbell

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var modelList : MutableList<DoorbellEntry> ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.doorbellView)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)
        recyclerView.adapter = DoorbellAdapter(this,modelList!!){

            Toast.makeText(this@MainActivity,"${it.timestamp} has been clicked",
                    Toast.LENGTH_SHORT).show()
        }
    }

    fun callWebservice(){

    doAsync { var result = URL("").readText()
        uiThread { longToast("show message") }
    }

    }
}
