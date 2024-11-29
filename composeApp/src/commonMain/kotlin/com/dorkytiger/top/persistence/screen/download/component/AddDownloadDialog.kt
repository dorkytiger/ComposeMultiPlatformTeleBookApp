package com.dorkytiger.top.persistence.screen.download.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.persistence.component.LoadingView
import com.dorkytiger.top.util.DisplayResult
import com.dorkytiger.top.util.RequestState
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus

@Composable
fun AddDownloadDialog(
    onAddDownload: (String, closeDialog: (() -> Unit)) -> Unit,
    requestState: RequestState<Unit>
) {
    var openDialog by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }

    IconButton(onClick = {
        openDialog = true
    }) {
        Icon(FeatherIcons.Plus, contentDescription = "Download")
    }

    if (openDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { openDialog = false },
            title = { Text("Telegram URL", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.height(80.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
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
                        onAddDownload(url, { openDialog = false })
                    }) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                if (requestState.isIdle()) {
                    TextButton(onClick = {
                        openDialog = false
                    }) {
                        Text("Cancel", fontWeight = FontWeight.Bold, color = Color(0xFFA6A6A6))
                    }
                }
            }
        )
    }
}