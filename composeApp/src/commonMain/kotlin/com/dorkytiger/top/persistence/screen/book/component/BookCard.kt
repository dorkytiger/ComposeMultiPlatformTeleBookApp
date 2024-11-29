package com.dorkytiger.top.persistence.screen.book.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.db.dao.book.BookEntity
import com.dorkytiger.top.persistence.component.LoadingView
import com.dorkytiger.top.util.DisplayResult
import com.dorkytiger.top.util.RequestState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import compose.icons.FeatherIcons
import compose.icons.feathericons.X


@Composable
fun BookCard(
    navToPageScreen: () -> Unit,
    deleteBook: (closeDialog: () -> Unit) -> Unit,
    bookEntity: BookEntity,
    modifyState: RequestState<Unit>
) {
    var openDialog by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .width(150.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        openDialog = true
                    },
                    onTap = {
                        navToPageScreen()
                    }
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            CoilImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { bookEntity.pathUrls.firstOrNull() },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                ),
                failure = {
                    Icon(
                        imageVector = FeatherIcons.X,
                        contentDescription = "image request failed",
                        modifier = Modifier.size(50.dp)
                    )
                },
                loading = {
                    LoadingView(modifier = Modifier.size(150.dp))
                }
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(bookEntity.title, fontWeight = FontWeight.Bold, maxLines = 2)
    }

    if (openDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { openDialog = false },
            title = { Text("Delete Book", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.height(60.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    modifyState.DisplayResult(
                        onSuccess = {
                            Text("delete success", fontWeight = FontWeight.Bold)
                        },
                        onIdle = {
                            Text(
                                "Are you sure you want to delete this book?",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onLoading = {
                            LoadingView()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteBook { openDialog = false }

                    }
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false }
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, color = Color(0xFFA6A6A6))
                }
            }
        )
    }
}