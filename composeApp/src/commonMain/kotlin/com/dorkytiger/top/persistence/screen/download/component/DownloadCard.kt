package com.dorkytiger.top.persistence.screen.download.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.skydoves.landscapist.coil3.CoilImage
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
        CoilImage(
            imageModel = {url},
            modifier = Modifier.weight(1f)
        )
        Column {
            Text(title)
            Text("totalProgress: ${(totalProgress * 100).roundToInt()}%")
            LinearProgressIndicator(
                progress = { totalProgress },
            )
            Text("currentProgress: ${(currentProgress * 100).roundToInt()}%")
            LinearProgressIndicator(
                progress = { currentProgress },
            )

        }
    }

}