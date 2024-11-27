package com.dorkytiger.top.persistence.screen.book.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.coil3.CoilImage

@Composable
fun BookCard(
    onClick: () -> Unit,
    url: String,
    title: String
) {

    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        CoilImage(
            imageModel = { url },
            modifier = Modifier.size(100.dp)
        )
        Text(title)
    }
}