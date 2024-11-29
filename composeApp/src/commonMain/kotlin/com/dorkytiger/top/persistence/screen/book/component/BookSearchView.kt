package com.dorkytiger.top.persistence.screen.book.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import compose.icons.FeatherIcons
import compose.icons.feathericons.Search

@Composable
fun BookSearchView(
    searchBook: (String) -> Unit
) {

    var openDialog by remember { mutableStateOf(false) }
    var keyword by remember { mutableStateOf("") }

    IconButton(
        onClick = {
            openDialog = true
        }
    ) {
        Icon(FeatherIcons.Search, contentDescription = "Search")
    }

    if (
        openDialog
    ) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = {
                openDialog = false
            },
            title = {
                Text("Search Book", fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = keyword,
                    onValueChange = {
                        keyword = it
                    },
                    label = {
                        Text("Enter keyword")
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        searchBook(keyword)
                        openDialog = false
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold,color = Color(0xFFA6A6A6))
                }
            }
        )
    }

}