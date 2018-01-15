package com.ranosys.clouddoorbell

/**
 * @author Ranosys Technologies
 */



class AppConstant {
    companion object {
        const val  CLOUD_VISION_API_KEY = "AIzaSyAex0X2KlVQd672vcHsGbxH6lowc3BBZTU"
        const val MAX_LABEL_RESULT = 5
        const val CLOUD_VISION_URL:String = "https://vision.googleapis.com/v1/images:annotate?key="+CLOUD_VISION_API_KEY
    }
}