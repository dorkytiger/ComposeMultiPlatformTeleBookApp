package com.dorkytiger.top.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.Json

object HTMLUtils {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getImgCountFromURL(url: String): Int {
        val response = client.get(url).bodyAsText()
        // 获取所有图片标签
        val imgTagList = response.split("<img")
        // 获取图片的src
        val imgSrcList = imgTagList.mapNotNull {
            val srcIndex = it.indexOf("src=\"")
            if (srcIndex != -1) {
                val src = it.substring(srcIndex + 5)
                val srcEndIndex = src.indexOf("\"")
                if (srcEndIndex != -1) {
                    src.substring(0, srcEndIndex)
                } else {
                    null
                }
            } else {
                null
            }
        }
        return imgSrcList.size
    }

    suspend fun getHtmlTitleFromURL(url: String): String {
        val response = client.get(url).bodyAsText()
        val titleIndex = response.indexOf("<h1>")
        val titleEndIndex = response.indexOf("</h1>")
        return response.substring(titleIndex + 4, titleEndIndex)
    }

    fun getImgByteListFromURL(url: String): Flow<Pair<Int, ByteArray>> = channelFlow {
        runCatching {
            val response = client.get(url).bodyAsText()
            // 获取所有图片标签
            val imgTagList = response.split("<img")
            // 获取图片的src
            val imgSrcList = imgTagList.mapNotNull {
                val srcIndex = it.indexOf("src=\"")
                if (srcIndex != -1) {
                    val src = it.substring(srcIndex + 5)
                    val srcEndIndex = src.indexOf("\"")
                    if (srcEndIndex != -1) {
                        src.substring(0, srcEndIndex)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            // 遍历图片的src，获取图片的byte数组
            imgSrcList.forEachIndexed { index, src ->
                val httpResponse = client.get("https://telegra.ph/$src") {
                    onDownload { bytesSentTotal, contentLength ->
                        println("Received $bytesSentTotal bytes from $contentLength")
                    }
                }
                val responseBody: ByteArray = httpResponse.body()
                send(index to responseBody)
            }

        }.onFailure {
            it.printStackTrace()
        }
    }

    suspend fun getImgUrlsFromURL(url: String): List<String> =
        runCatching {
            val response = client.get(url).bodyAsText()
            // 获取所有图片标签
            val imgTagList = response.split("<img")
            // 获取图片的src
            val imgSrcList = imgTagList.mapNotNull {
                val srcIndex = it.indexOf("src=\"")
                if (srcIndex != -1) {
                    val src = it.substring(srcIndex + 5)
                    val srcEndIndex = src.indexOf("\"")
                    if (srcEndIndex != -1) {
                        src.substring(0, srcEndIndex)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            // 遍历图片的src，获取图片的byte数组
            imgSrcList.map { src ->
                "https://telegra.ph/$src"
            }
        }.onFailure {
            it.printStackTrace()
        }.getOrDefault(emptyList())

    fun getImgByteByImgUrlList(imgUrlList: List<String>): Flow<Triple<Int, Float, ByteArray>> =
        channelFlow {
            imgUrlList.forEachIndexed { index, url ->
                val httpResponse = client.get(url) {
                    onDownload { bytesSentTotal, contentLength ->
                        if (contentLength == null) {
                            return@onDownload
                        }
                        val progress = bytesSentTotal.toFloat() / contentLength.toFloat()
                        send(Triple(index, progress, byteArrayOf()))
                    }
                }
                val responseBody: ByteArray = httpResponse.body()
                send(Triple(index, 1f, responseBody))
            }
        }

}