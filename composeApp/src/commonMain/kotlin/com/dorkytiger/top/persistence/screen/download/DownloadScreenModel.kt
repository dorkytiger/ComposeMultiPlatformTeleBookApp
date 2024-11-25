package com.dorkytiger.top.persistence.screen.download

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.db.AppDatabase
import com.dorkytiger.top.db.dao.download.DownloadEntity
import com.dorkytiger.top.util.RequestState
import kotlinx.coroutines.launch

class DownloadScreenModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    private var _downloadListState: MutableState<RequestState<List<DownloadEntity>>> =
        mutableStateOf(RequestState.Loading)
    val downloadListState: MutableState<RequestState<List<DownloadEntity>>> = _downloadListState

    init {
        viewModelScope.launch {
            getDownloadList()
        }
    }

    private suspend fun getDownloadList() {
        appDatabase.downloadDao().getAll()
    }
}