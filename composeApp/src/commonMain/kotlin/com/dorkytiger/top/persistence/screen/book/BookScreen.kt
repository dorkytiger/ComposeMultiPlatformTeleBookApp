package com.dorkytiger.top.persistence.screen.book

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.util.DisplayResult
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navToDownload: () -> Unit
) {

    val viewModel = koinViewModel<BookScreenModel>()
    val scope = rememberCoroutineScope()
    val bookListState = viewModel.bookListState.value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Book")
                },
                actions = {
                    IconButton(onClick = { navToDownload() }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Download")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.bookScreenAction(BookScreenAction.GetBookList)
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) {
        bookListState.DisplayResult(
            onLoading = {
                Text("Loading")
            },
            onError = {
                Text(it)
            },
            onSuccess = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(it) { book ->
                        Text(book.title)
                    }
                }
            }
        )
    }
}