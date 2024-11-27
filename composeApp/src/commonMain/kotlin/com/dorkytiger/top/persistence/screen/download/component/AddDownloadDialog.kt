package com.dorkytiger.top.persistence.screen.download.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.persistence.component.LoadingView
import com.dorkytiger.top.util.DisplayResult
import com.dorkytiger.top.util.RequestState

@Composable
fun AddDownloadDialog(
    onAddDownload: (String) -> Unit,
    requestState: RequestState<Unit>
) {
    var openDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }

    IconButton(onClick = {
        openDialog = true
    }) {
        Icon(Icons.Default.Add, contentDescription = "Download")
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text("Telegram URL") },
            text = {
                Column(
                    modifier = Modifier.height(150.dp)
                ) {
                    requestState.DisplayResult(
                        onSuccess = {
                            Text("Decode Success")
                        },
                        onLoading = {
                            LoadingView()
                        },
                        onIdle = {
                            OutlinedTextField(
                                value = url,
                                onValueChange = { url = it },
                            )
                        },
                        onError = {
                            Text("Error: $it")
                        }
                    )
                }
            },
            confirmButton = {
                if (requestState.isIdle()) {
                    TextButton(onClick = {
                        onAddDownload(url)
                        openDialog = false
                    }) {
                        Text("Add")
                    }
                }
            },
            dismissButton = {
                if (requestState.isIdle()) {
                    TextButton(onClick = {
                        openDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}