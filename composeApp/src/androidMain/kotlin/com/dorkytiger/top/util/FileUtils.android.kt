package com.dorkytiger.top.util

import java.io.File

actual object FileUtil {
    actual fun saveImagesToFolder(
        title: String,
        fileName: String,
        byteArray: ByteArray
    ): String {

        // 1. 在本地手机创建一个文件夹
        val folder = File("/storage/emulated/0/download/KotlinBookApp")

        // 2. 如果文件夹不存在，则创建文件夹
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // 3. 再在文件夹下创建一个文件夹
        val subFolder = File(folder, title)
        if (!subFolder.exists()) {
            subFolder.mkdirs()
        }

        // 4. 保存图片到文件夹
        val file = File(subFolder, "${fileName}.png")

        if (file.exists()) {
            file.delete()
        }

        file.outputStream().write(byteArray)

        return file.absolutePath
    }

    actual fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)
        return file.delete()
    }

}