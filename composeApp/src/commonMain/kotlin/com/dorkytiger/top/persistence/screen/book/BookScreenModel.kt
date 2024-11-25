package com.dorkytiger.top.persistence.screen.book

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.db.AppDatabase
import com.dorkytiger.top.db.dao.book.BookEntity
import com.dorkytiger.top.util.RequestState
import kotlinx.coroutines.launch

class BookScreenModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val _bookListState: MutableState<RequestState<List<BookEntity>>> =
        mutableStateOf(RequestState.Loading)
    val bookListState: MutableState<RequestState<List<BookEntity>>> = _bookListState

    init {
        viewModelScope.launch {
            getBookList()
        }
    }

    suspend fun bookScreenAction(action: BookScreenAction) {
        when (action) {
            is BookScreenAction.GetBookList -> getBookList()
        }
    }

    private suspend fun getBookList() {
        runCatching {
            _bookListState.value = RequestState.Loading
            appDatabase.bookDao().getAll().let {
                _bookListState.value = RequestState.Success(it)
            }
        }.onFailure {
            _bookListState.value = RequestState.Error(it.message ?: "Unknown error")
            it.printStackTrace()
        }
    }

}

sealed interface BookScreenAction {
    data object GetBookList : BookScreenAction
}