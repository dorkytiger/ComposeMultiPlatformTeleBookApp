package com.dorkytiger.top.persistence.screen.download.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.coil3.CoilImage
import kotlin.math.roundToInt

@Composable
fun DownloadCard(
    title: String,
    url: String,
    totalProgress: Float,
    currentProgress: Float,
    error: String=""
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (error.isNotBlank()){
           Box(
               modifier = Modifier.fillMaxSize(),
               contentAlignment = Alignment.Center
           ){
                Text(error)
           }

        }else{
            Box(
                modifier = Modifier.weight(1f)
            ) {
                CoilImage(
                    imageModel = { url },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(3f)
            ) {
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

}