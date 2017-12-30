package com.ranosys.clouddoorbell

/**
 * @author Ranosys Technologies
 */



class AppConstant {
    companion object {
        const val  CLOUD_VISION_API_KEY = "AIzaSyBlkDTVu6M6oQyNLaycPTlUZ8PqKMevcrM"
        const val MAX_LABEL_RESULT = 1
        const val CLOUD_VISION_URL:String = "https://vision.googleapis.com/v1/images:annotate?key="+CLOUD_VISION_API_KEY
    }
}