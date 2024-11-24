package com.dorkytiger.top

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val client = HttpClient()
    val scope = rememberCoroutineScope()

    fun testDownload() {
        scope.launch {
           runCatching {
               val url = "https://telegra.ph/test-01-15-254"
//               val url="https://filehelper.weixin.qq.com/?from=windows&type=recommend"
               val response = client.get(url).bodyAsText()
               println("response:${response}")
               // 使用简单的正则表达式提取图片 URL
               val imageUrls = Regex("<img[^>]+src=\"([^\"]+)\"").findAll(response).map { match ->
                   match.groups[1]?.value ?: ""
               }.filter { it.isNotEmpty() }.toList()

               imageUrls.forEachIndexed { index, imageUrl ->
                   println("Image URL $index: $imageUrl")
               }

               client.close()
           }.onFailure {
               it.printStackTrace()
           }
        }
    }



    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                testDownload()
            }) {
                Text("test")
            }
        }
    }
}