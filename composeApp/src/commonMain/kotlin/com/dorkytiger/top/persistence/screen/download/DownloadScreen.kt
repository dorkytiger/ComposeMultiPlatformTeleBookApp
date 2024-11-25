package com.dorkytiger.top.persistence.screen.download

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    navBackBook: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Download")
                },
                navigationIcon = {
                     IconButton(onClick = { navBackBook() }) {
                         Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Download")
                     }
                }
            )
        }
    ) {

    }
}