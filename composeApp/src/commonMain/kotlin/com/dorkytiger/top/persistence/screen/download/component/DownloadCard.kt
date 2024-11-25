package com.dorkytiger.top.persistence.screen.download.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import kotlin.math.roundToInt

@Composable
fun DownloadCard(
    title: String,
    url: String,
    totalProgress: Float,
    currentProgress: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = url,
            contentDescription = title,
        )
        Column {
            Text(title)
            Text("totalProgress: ${totalProgress * 100.00.roundToInt()}%")
            LinearProgressIndicator(
                progress = { currentProgress },
            )
            Text("currentProgress: ${currentProgress * 100.00.roundToInt()}%")
            LinearProgressIndicator(
                progress = { currentProgress },
            )

        }
    }

}