package com.dorkytiger.top.persistence.screen.download

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.persistence.screen.download.component.AddDownloadDialog
import com.dorkytiger.top.persistence.screen.download.component.DownloadCard
import com.dorkytiger.top.util.DisplayResult
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
) {
    val viewModel = koinViewModel<DownloadScreenModel>()

    val downloadListState by viewModel.downloadListState
    val downloadJobListState by viewModel.downloadJobListState
    val decodeState by viewModel.decodeUrlState

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("Download", fontWeight = FontWeight.Bold)
            },
            actions = {
                AddDownloadDialog(
                    onAddDownload = { url, closeDialog ->
                        viewModel.onAction(DownloadScreenAction.InsertDownload(url, closeDialog))
                    },
                    requestState = decodeState
                )
            })
    }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            ).padding(horizontal = 16.dp)
        ) {
            downloadListState.DisplayResult(onLoading = {
                Text("Loading...")
            }, onError = {
                Text("Error: $it")
            }, onSuccess = {
                LazyColumn {
                    items(it) { downloadEntity ->
                        val downloadJob =
                            downloadJobListState.find { it.downloadId == downloadEntity.id }
                        DownloadCard(
                            title = downloadEntity.title,
                            url = downloadJob?.preview ?: "",
                            totalProgress = downloadJob?.totalProgress ?: 0f,
                            currentProgress = downloadJob?.currentProgress ?: 0f
                        )
                    }
                }
            })
        }

    }
}