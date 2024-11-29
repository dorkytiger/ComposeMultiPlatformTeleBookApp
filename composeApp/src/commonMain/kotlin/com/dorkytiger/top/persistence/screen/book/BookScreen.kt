package com.dorkytiger.top.persistence.screen.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.persistence.screen.book.component.BookCard
import com.dorkytiger.top.util.DisplayResult
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCcw
import compose.icons.feathericons.RefreshCw
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navPageScreen: (Int) -> Unit
) {


    val scope = rememberCoroutineScope()

    var gridCount by remember { mutableStateOf(2) }
    var screenWidth by remember { mutableStateOf(0.dp) }

    val viewModel = koinViewModel<BookScreenModel>()
    val bookListState = viewModel.bookListState.value




    Scaffold(
        modifier = Modifier.fillMaxSize().onGloballyPositioned { screenWidth = it.size.width.dp },
        topBar = {
            TopAppBar(
                title = {
                    Text("Book", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.bookScreenAction(BookScreenAction.GetBookList)
                        }
                    }) {
                        Icon(FeatherIcons.RefreshCw, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            ).padding(16.dp)
        ) {
            bookListState.DisplayResult(
                onLoading = {
                    Text("Loading")
                },
                onError = {
                    Text(it)
                },
                onSuccess = {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.FixedSize(150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        items(it) { book ->
                            BookCard(
                                onClick = {
                                    navPageScreen(book.id)
                                },
                                url = book.pathUrls.firstOrNull() ?: "",
                                title = book.title
                            )
                        }
                    }
                }
            )
        }
    }
}