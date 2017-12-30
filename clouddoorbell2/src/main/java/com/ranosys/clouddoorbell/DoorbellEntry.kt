package com.ranosys.clouddoorbell

/**
 * data class for parsing
 * @author Ranosys Technologies
 */
data class DoorbellEntry(
        var timestamp:Long,
        var image:String,
        var annotations:MutableMap<String,Float>)