package com.dorkytiger.top.persistence.screen.book

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.db.AppDatabase
import com.dorkytiger.top.db.dao.book.BookEntity
import com.dorkytiger.top.util.FileUtil
import com.dorkytiger.top.util.RequestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookScreenModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val _bookListState: MutableState<RequestState<List<BookEntity>>> =
        mutableStateOf(RequestState.Loading)
    val bookListState: MutableState<RequestState<List<BookEntity>>> = _bookListState

    private val _modifyBookState: MutableState<RequestState<Unit>> =
        mutableStateOf(RequestState.Idle)
    val modifyBookState: MutableState<RequestState<Unit>> = _modifyBookState

    init {
        getBookList()
    }

    fun bookScreenAction(action: BookScreenAction) {
        when (action) {
            is BookScreenAction.GetBookList -> getBookList()
            is BookScreenAction.SearchBook -> searchBook(action.keyWord)
            is BookScreenAction.DeleteBook -> deleteBook(action.book, action.closeDialog)
        }
    }

    private fun getBookList() {
        viewModelScope.launch {
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

    private fun searchBook(keyWord: String) {
        viewModelScope.launch {
            runCatching {
                _bookListState.value = RequestState.Loading
                appDatabase.bookDao().search(keyWord).let {
                    if (it.isEmpty()) {
                        _bookListState.value = RequestState.Empty
                        return@let
                    }
                    _bookListState.value = RequestState.Success(it)
                }
            }.onFailure {
                _bookListState.value = RequestState.Error(it.message ?: "Unknown error")
                it.printStackTrace()
            }
        }
    }

    private fun deleteBook(book: BookEntity, closeDialog: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                _modifyBookState.value = RequestState.Loading
                book.pathUrls.forEach {
                    FileUtil.deleteFile(it)
                }
                appDatabase.bookDao().delete(book)
                delay(1000)
                _modifyBookState.value = RequestState.Success(Unit)
                delay(1000)
                closeDialog()
                getBookList()
                _modifyBookState.value = RequestState.Idle
            }.onFailure {
                _modifyBookState.value = RequestState.Error(it.message ?: "Unknown error")
                it.printStackTrace()
            }
        }
    }

}

sealed interface BookScreenAction {
    data object GetBookList : BookScreenAction
    data class SearchBook(val keyWord: String) : BookScreenAction
    data class DeleteBook(val book: BookEntity, val closeDialog: () -> Unit) : BookScreenAction
}