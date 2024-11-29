package com.dorkytiger.top.persistence.screen.book.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import compose.icons.FeatherIcons
import compose.icons.feathericons.X

@Composable
fun BookCard(
    onClick: () -> Unit,
    url: String,
    title: String
) {


    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.width(150.dp),
            contentAlignment = Alignment.Center
        ){
            CoilImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { url },
                failure = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FeatherIcons.X,
                            contentDescription = "image request failed",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(title,  fontWeight = FontWeight.Bold, maxLines = 2)
    }
}