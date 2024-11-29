package com.dorkytiger.top.util

expect object FileUtil {

    fun saveImagesToFolder(title: String, fileName: String, byteArray: ByteArray): String

    fun deleteFile(filePath: String): Boolean

}