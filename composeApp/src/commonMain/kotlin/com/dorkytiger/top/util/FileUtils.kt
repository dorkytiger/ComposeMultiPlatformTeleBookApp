package com.dorkytiger.top.util

import kotlinx.coroutines.flow.Flow

expect object FileUtil {

    fun saveImagesToFolder(title: String, fileName: String, byteArray: ByteArray): String

}