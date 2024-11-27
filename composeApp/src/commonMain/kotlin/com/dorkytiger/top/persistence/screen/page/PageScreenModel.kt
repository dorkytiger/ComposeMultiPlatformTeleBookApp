package com.dorkytiger.top.persistence.screen.page

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.db.AppDatabase
import com.dorkytiger.top.db.dao.book.BookEntity
import com.dorkytiger.top.navigation.BOOK_ID
import com.dorkytiger.top.util.RequestState
import kotlinx.coroutines.launch

class PageScreenModel(
    private val appDatabase: AppDatabase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val bookId = savedStateHandle.get<Int>(BOOK_ID) ?: -1

    private var _bookInfoState: MutableState<RequestState<BookEntity>> =
        mutableStateOf(RequestState.Loading)
    val bookInfoState: MutableState<RequestState<BookEntity>> = _bookInfoState

    private var _currentPage = mutableStateOf(0)
    val currentPage: MutableState<Int> = _currentPage

    init {
        getBookInfo()
    }

    fun onEvent(event: PageScreenEvent) {
        when (event) {
            is PageScreenEvent.NextPage -> nextPage()
            is PageScreenEvent.PreviousPage -> previousPage()
        }
    }

    private fun nextPage() {
        if (currentPage.value < _bookInfoState.value.getSuccessData().pathUrls.size - 1) {
            _currentPage.value++
        }
    }

    private fun previousPage() {
        if (currentPage.value > 0) {
            _currentPage.value--
        }
    }

    private fun getBookInfo() {
        viewModelScope.launch {
            _bookInfoState.value = RequestState.Loading
            val bookInfoResponse = appDatabase.bookDao().getById(bookId)
            _bookInfoState.value = RequestState.Success(bookInfoResponse)
        }
    }
}

sealed interface PageScreenEvent {
    data object NextPage : PageScreenEvent
    data object PreviousPage : PageScreenEvent
}