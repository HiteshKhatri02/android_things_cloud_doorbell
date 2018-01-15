package com.ranosys.clouddoorbell

import android.content.Context
import android.media.ImageReader

import android.content.ContentValues.TAG
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.os.Handler
import android.util.Log
import java.util.*


/**
 * @author Ranosys Technologies
 */
class DoorbellCamera {

    companion object {
        const val IMAGE_WIDTH:Int = 640
        const val IMAGE_HEIGHT:Int = 480
        const val MAX_IMAGES:Int = 1
        const val TAG = "DoorbellCamera"

        private class InstanceHolder{
            companion object {
                val camera:DoorbellCamera = DoorbellCamera()
            }
        }

        fun getInstance():DoorbellCamera = InstanceHolder.camera
    }


    //Image result processor
    private var imageReader : ImageReader ?= null
    //Active camera device connection
    private var cameraDevice : CameraDevice ?= null
    //Active camera capture session
    private var cameraCaptureSession : CameraCaptureSession ?= null

    //call back used while opening the camera and notify the current status of the camera.
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onDisconnected(p0: CameraDevice?) {
            Log.i(TAG,"Camera has been disconnected.")
        }

        override fun onError(p0: CameraDevice?, p1: Int) {
            Log.e(TAG,"Some error has occur found while opening the camera.")
        }

        override fun onOpened(cad: CameraDevice) {
            Log.i(TAG,"Camera has been opened.")
            cameraDevice = cad
        }
    }

    //Callback hndling capture sessions
    private val sessionCallback = object : CameraCaptureSession.StateCallback(){
        override fun onConfigureFailed(p0: CameraCaptureSession?) {
            Log.w(TAG, "Failed to configure camera")
        }

        override fun onConfigured(p0: CameraCaptureSession?) {
            if (cameraDevice == null) {
                return
            }
            cameraCaptureSession = p0
            triggerImageCapture()
        }
    }

    // Callback handling capture progress events
    private val captureCallback : CameraCaptureSession.CaptureCallback
            = object  : CameraCaptureSession.CaptureCallback(){

        override fun onCaptureCompleted(session:CameraCaptureSession?,
                                        request: CaptureRequest?,
                                        result: TotalCaptureResult?) {
            if (session != null) {
                session.close();
                cameraCaptureSession?.close()
                //cameraCaptureSession = null;

                Log.d(TAG, "CaptureSession closed");
            }
        }
    }

     fun initCamera(context: Context, backgroundHandler: Handler,
                          imageAvailableListener: ImageReader.OnImageAvailableListener ){
        //Find the camera instance
        val manager : CameraManager = context.getSystemService(Context.CAMERA_SERVICE)
                as CameraManager
        var camIds = arrayOf<String>()
        try {
            camIds = manager.cameraIdList
        } catch (e: CameraAccessException) {
            Log.d(TAG, "Cam access exception getting IDs", e)
        }

        if (camIds.size < 1) {
            Log.d(TAG, "No cameras found")
            return;
        }

        val id : String = camIds[0]
        Log.d(TAG, "Using camera id " + id)

        //Initialise image processor
        imageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
                ImageFormat.JPEG, MAX_IMAGES)
        imageReader?.setOnImageAvailableListener(imageAvailableListener,backgroundHandler)

        //open camera
        try {
            manager.openCamera(id,stateCallback,backgroundHandler)
        }catch (e:CameraAccessException){
            Log.e(TAG,"Some error has been occur while accessing camera." +e)
            e.printStackTrace()
            e.reason
        }

    }

    public fun shutDownCamera(){
        if (null!=cameraDevice){cameraDevice!!.close()}
    }

    public fun takePicture(){
        if (null==cameraDevice){
            Log.w(TAG,"Cannot capture image. Camera is not initialised.")
            return
        }
        //here we create a Camera capture session for capturing still images.
        try {
            cameraDevice?.createCaptureSession(Collections.singletonList(imageReader?.surface),
                    sessionCallback, null)

        }catch (e:CameraAccessException){
            Log.e(TAG,"Access exception while preparing pic", e)
        }
    }

    private fun triggerImageCapture(){
        try {
            val captureBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder?.addTarget(imageReader?.surface)
            captureBuilder?.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON)
            Log.d(TAG, "Session initialized.")
            cameraCaptureSession?.capture(captureBuilder?.build(), captureCallback, null)
        }catch (e:CameraAccessException){
            Log.e(TAG,"Access exception while triggering imagecapture", e)
        }
    }
}