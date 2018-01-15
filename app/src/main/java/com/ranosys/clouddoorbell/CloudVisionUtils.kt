package com.ranosys.clouddoorbell

import android.graphics.Bitmap
import android.os.AsyncTask
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

/**
 * @author Ranosys Technologies
 */
class CloudVisionUtils {
    companion object {

        @Throws(IOException::class)
        fun annotateImage(imageBytes : ByteArray):MutableMap<String,Float>{
            //construct vision API instance
            val httpTransport : HttpTransport = AndroidHttp.newCompatibleTransport()
            val jsonFactory : JsonFactory = GsonFactory.getDefaultInstance()
            val initializer = VisionRequestInitializer(AppConstant.CLOUD_VISION_API_KEY)
            val vision:Vision = Vision.Builder(httpTransport,jsonFactory,null)
                    .setVisionRequestInitializer(initializer)
                    .build()

            //create image request
            val imageRequest  = AnnotateImageRequest()
            val image = Image()
            image.encodeContent(imageBytes)
            imageRequest.image = image

            //Add the feature we want
            val labelDetection = Feature()
            labelDetection.type = "LABEL_DETECTION"
            labelDetection.maxResults = AppConstant.MAX_LABEL_RESULT
            imageRequest.setFeatures(Collections.singletonList(labelDetection))

            //Batch and excute the request
            val requestBatch = BatchAnnotateImagesRequest()
            requestBatch.requests = Collections.singletonList(imageRequest)

            val response : BatchAnnotateImagesResponse = vision.images()
                    .annotate(requestBatch)
                    .setDisableGZipContent(true)
                    .execute()

            return convertResponseToMap(response)
        }

        private fun convertResponseToMap(reponse : BatchAnnotateImagesResponse):MutableMap<String,Float>{
            val annotation:MutableMap<String, Float> = HashMap()

            // Convert response into a readable collection of annotations
            reponse.responses[0].labelAnnotations?.forEach{ item -> annotation.put(item.description,item.score) }
            return annotation
        }
    }


}