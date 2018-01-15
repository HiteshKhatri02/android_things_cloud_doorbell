package com.ranosys.clouddoorbell

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.media.Image
import android.media.ImageReader
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import com.google.android.things.contrib.driver.button.ButtonInputDriver
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat
import java.io.IOException
import android.os.HandlerThread
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.ranosys.clouddoorbell.models.DoorbellRequestModel
import com.ranosys.clouddoorbell.models.DoorbellResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class AndroidThingMainActivity : Activity() {
    //var buttonA : Button ?= null

    private var buttonADriver : ButtonInputDriver ?= null
    //private var buzzer : Speaker ?= null
    private var isPressed : Boolean = true


    /**
     * A Handler for running tasks in the background.
     */
    private var mCameraHandler: Handler? = null
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mCameraThread: HandlerThread? = null

    /**
     * Camera capture device wrapper
     */
    private var camera: DoorbellCamera? = null

    private var imageProcessor : ImagePreprocessor? = null

    private var cloudHandler : Handler?=null

    companion object {
        val TAG = "AndroidThingMainActivity"
    }

    private val onImageAvailableListener = object : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(p0: ImageReader?) {
            //Getthe raw image bytes
            val image : Image? = p0?.acquireNextImage()

            val bitmap: Bitmap? =imageProcessor?.preprocessImage(image)

            if (bitmap==null) {
                Log.e(TAG, "bitmap is null")
                return
            }
            val bytteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,bytteArrayOutputStream)
            val imageBytes : ByteArray = bytteArrayOutputStream.toByteArray()

            runOnUiThread { findViewById<ImageView>(R.id.image_view).setImageBitmap(bitmap) }
            Log.d(TAG,"Image has been availabled.")
            onPictureTaken(imageBytes)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(AndroidThingMainActivity.TAG, "Starting AndroidThingMainActivity...")
        setContentView(R.layout.activity_main)
        //initialize buzeer
        //initBuzzer()
        //initialize button a.
        initButtonA()
        //initialise camera handler
        initCameraHandler()
        initCamera()

        initHandler()

    }

    private fun initBuzzer(){
        Log.i(AndroidThingMainActivity.TAG, "Initializing Buzzer...")
        //buzzer = RainbowHat.openPiezo()
    }



    private fun initButtonA(){
        Log.i(AndroidThingMainActivity.TAG, "Initializing Button A...")
        try {
            buttonADriver = RainbowHat.createButtonAInputDriver(KeyEvent.KEYCODE_ENTER)
            buttonADriver!!.register()
        }catch (e:IOException){
            Log.e(AndroidThingMainActivity.TAG,"Some error occur in initializing the apk. ")
        }
    }

    private fun initCameraHandler(){
        // Creates new handlers and associated threads for camera and networking operations.
        mCameraThread = HandlerThread("CameraBackground")
        mCameraThread!!.start()
        mCameraHandler = Handler(mCameraThread!!.looper)
    }

    private fun initCamera(){
        imageProcessor = ImagePreprocessor()
        camera = DoorbellCamera.getInstance()
        camera?.initCamera(this,mCameraHandler!!, onImageAvailableListener )

    }


    override fun onDestroy() {
        Log.i(AndroidThingMainActivity.TAG, "Closing Activity...")

        try {
            buttonADriver!!.unregister()
            buttonADriver?.close()
        }catch (e:IOException){
            Log.e(AndroidThingMainActivity.TAG,"Some error occur while closing the button A.")
            e.printStackTrace()
        }

        try {
            //buzzer?.close()
        }catch (e:IOException){
            Log.e(AndroidThingMainActivity.TAG,"Some error occur while closing the buzzer.")
            e.printStackTrace()

        }

        mCameraThread!!.quitSafely()
        camera?.shutDownCamera()

        if (null!=cloudHandler){
            cloudHandler = null
        }
        super.onDestroy()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d(TAG,"Buttpn Pressed")
            camera?.takePicture()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    fun initHandler(){
        cloudHandler = Handler()
    }

    private fun onPictureTaken( imageBytes : ByteArray?){
        // ...process the captured image...
        if (null!=imageBytes){

            val imgStr : String = Base64.encodeToString(imageBytes,Base64.DEFAULT).replace("\n", "")

            runOnUiThread{AsyncTaskExample(imageBytes,imgStr).execute()}

        }
    }



    private fun sendEntries(doorbellRequestModel: DoorbellRequestModel){
        val service  = RestClient.getGetClient()
        val call = service?.sendEntry("storedata",doorbellRequestModel)
        Log.d(TAG,call?.request().toString())
        call?.enqueue(object : Callback<DoorbellResponseModel> {

            override fun onResponse(call: Call<DoorbellResponseModel>?, response: Response<DoorbellResponseModel>?) {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                if (response!!.isSuccessful){
                    Log.d(TAG, response.body().toString())
                }
            }

            override fun onFailure(call: Call<DoorbellResponseModel>?, t: Throwable?) {
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                Log.d(TAG, "Error occurred.")
                Log.d(TAG, t.toString())
            }

        })
    }


    @SuppressLint("StaticFieldLeak")
    inner class AsyncTaskExample(val imgBytes:ByteArray?, val imgStr : String): AsyncTask<Void, Void, Void>(){
        var annotations:MutableMap<String,Float>? = null

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        }
        override fun doInBackground(vararg params: Void?): Void? {
            annotations = CloudVisionUtils.annotateImage(imgBytes!!)
            return null;

        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (annotations != null) {
                Log.d(TAG,annotations?.keys.toString())
                val timestamp = System.currentTimeMillis()

                findViewById<TextView>(R.id.textView).setText(annotations?.keys.toString())
                sendEntries(DoorbellRequestModel(imgStr,annotations.toString(),timestamp.toString()))
            }else{
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
            }
        }
    }
}
