package com.dorkytiger.top.persistence.screen.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    navBackBook: () -> Unit
) {
    val viewModel = koinViewModel<DownloadScreenModel>()

    val downloadListState by viewModel.downloadListState

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
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            ).padding(horizontal = 16.dp)
        ) {
            downloadListState.DisplayResult(
                onLoading = {
                    Text("Loading...")
                },
                onError = {
                    Text("Error: $it")
                },
                onSuccess = {
                    Text("Success: $it")
                }
            )
        }

    }
}