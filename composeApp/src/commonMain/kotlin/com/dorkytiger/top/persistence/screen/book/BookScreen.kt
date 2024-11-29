package com.dorkytiger.top.persistence.screen.book

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.persistence.component.EmptyView
import com.dorkytiger.top.persistence.component.LoadingView
import com.dorkytiger.top.persistence.screen.book.component.BookCard
import com.dorkytiger.top.persistence.screen.book.component.BookSearchView
import com.dorkytiger.top.util.DisplayResult
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCw
import compose.icons.feathericons.Search
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navPageScreen: (Int) -> Unit
) {


    val scope = rememberCoroutineScope()

    val viewModel = koinViewModel<BookScreenModel>()
    val bookListState by viewModel.bookListState
    val modifyBookState by viewModel.modifyBookState


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Book", fontWeight = FontWeight.Bold)
                },
                actions = {
                    BookSearchView(
                        searchBook = {
                            scope.launch {
                                viewModel.bookScreenAction(BookScreenAction.SearchBook(it))
                            }
                        }
                    )
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.bookScreenAction(BookScreenAction.GetBookList)
                        }
                    }) {
                        Icon(FeatherIcons.RefreshCw, contentDescription = "Refresh")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            ).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            bookListState.DisplayResult(
                onLoading = {
                    LoadingView()
                },
                onError = {
                    Text(it)
                },
                onEmpty = {
                    EmptyView()
                },
                onSuccess = {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.FixedSize(150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        items(it) { book ->
                            BookCard(
                                navToPageScreen = {
                                    navPageScreen(book.id)
                                },
                                bookEntity = book,
                                deleteBook = { closeDialog ->
                                    viewModel.bookScreenAction(
                                        BookScreenAction.DeleteBook(
                                            book,
                                            closeDialog
                                        )
                                    )
                                },
                                modifyState = modifyBookState
                            )
                        }
                    }
                }
            )
        }
    }
}