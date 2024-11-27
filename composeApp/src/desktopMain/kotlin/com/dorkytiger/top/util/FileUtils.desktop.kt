package com.dorkytiger.top.util

import java.io.File

actual object FileUtil {
    actual fun saveImagesToFolder(
        title: String,
        fileName: String,
        byteArray: ByteArray
    ): String {
        // 1. 在本地手机创建一个文件夹
        // Determine the base directory based on the platform
        val baseDir = when {
            System.getProperty("os.name").contains("Windows", ignoreCase = true) -> {
                System.getenv("USERPROFILE") + "\\Downloads\\KotlinBookApp"
            }

            System.getProperty("os.name").contains("Mac", ignoreCase = true) -> {
                System.getProperty("user.home") + "/Downloads/KotlinBookApp"
            }

            else -> {
                "/storage/emulated/0/Download/KotlinBookApp"
            }
        }
        val folder = File(baseDir)
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

}