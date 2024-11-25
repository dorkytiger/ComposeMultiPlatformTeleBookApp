package com.dorkytiger.top.persistence.screen.download.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AddDownloadDialog(
    onAddDownload: (String) -> Unit
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
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                )
            },
            confirmButton = { onAddDownload(url) },
            dismissButton = { openDialog = false }
        )
    }
}