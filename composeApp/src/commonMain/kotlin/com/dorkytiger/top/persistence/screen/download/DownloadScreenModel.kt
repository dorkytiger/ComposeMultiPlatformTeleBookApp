package com.dorkytiger.top.persistence.screen.download

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dorkytiger.top.db.AppDatabase
import com.dorkytiger.top.db.dao.book.BookEntity
import com.dorkytiger.top.db.dao.download.DownloadEntity
import com.dorkytiger.top.util.FileUtil
import com.dorkytiger.top.util.HTMLUtils
import com.dorkytiger.top.util.RequestState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DownloadScreenModel(
    private val appDatabase: AppDatabase,
) : ViewModel() {

    private var _downloadListState: MutableState<RequestState<List<DownloadEntity>>> =
        mutableStateOf(RequestState.Loading)
    val downloadListState: MutableState<RequestState<List<DownloadEntity>>> = _downloadListState


    private var _decodeUrlState: MutableState<RequestState<Unit>> =
        mutableStateOf(RequestState.Idle)
    val decodeUrlState = _decodeUrlState

    private var _downloadJobListState: MutableState<List<DownloadJob>> = mutableStateOf(emptyList())
    val downloadJobListState: MutableState<List<DownloadJob>> = _downloadJobListState


    init {
        viewModelScope.launch {
            getDownloadList()
        }
    }

    fun onAction(action: DownloadScreenAction) {
        when (action) {
            is DownloadScreenAction.InsertDownload -> {
                insertDownload(action.url, action.closeDialog)
            }
        }
    }

    private fun getDownloadList() {
        viewModelScope.launch {
            _downloadListState.value = RequestState.Loading
            val downloadList = appDatabase.downloadDao().getAll()
            if (downloadList.isEmpty()) {
                _downloadListState.value = RequestState.Empty
                return@launch
            }
            downloadList.forEach {
                if (_downloadJobListState.value.any { downloadJob -> downloadJob.downloadId == it.id }) {
                    return@forEach
                }
                startDownload(it)
            }
            _downloadListState.value = RequestState.Success(downloadList)
        }
    }

    private fun insertDownload(url: String, closeDialog: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                _decodeUrlState.value = RequestState.Loading
                val title = HTMLUtils.getHtmlTitleFromURL(url)
                val imgUrls = HTMLUtils.getImgUrlsFromURL(url)
                if (title.isEmpty() || imgUrls.isEmpty()) {
                    _decodeUrlState.value = RequestState.Error("Decode url failed")
                    return@launch
                }
                val downloadEntity = DownloadEntity(title = title, imgUrls = imgUrls)
                appDatabase.downloadDao().insert(downloadEntity)
                _decodeUrlState.value = RequestState.Success(Unit)
                delay(1000)
                closeDialog()
                getDownloadList()
                _decodeUrlState.value = RequestState.Idle
            }.onFailure {
                _decodeUrlState.value = RequestState.Error(it.message ?: "Unknown error")
                it.printStackTrace()
            }
        }
    }

    private fun startDownload(downloadEntity: DownloadEntity) {

        viewModelScope.launch {
            if (_downloadJobListState.value.any { it.downloadId == downloadEntity.id }) {
                return@launch
            }

            val downloadJob = viewModelScope.async {
                HTMLUtils.getImgByteByImgUrlList(
                    downloadEntity.completeIndex,
                    downloadEntity.imgUrls
                ).onEach { (index, float, byteArray) ->
                    if (index < downloadEntity.completeIndex) {
                        return@onEach
                    }
                    val currentDownloadJob =
                        _downloadJobListState.value.find { it.downloadId == downloadEntity.id }
                    if (float == 1f) {
                        val pathUrl = FileUtil.saveImagesToFolder(
                            "${downloadEntity.id}-${downloadEntity.title}",
                            index.toString(),
                            byteArray
                        )
                        appDatabase.downloadDao().getDownload(downloadEntity.id).let {
                            appDatabase.downloadDao().update(
                                downloadEntity.copy(
                                    pathUrls = it.pathUrls + pathUrl,
                                    completeIndex = index
                                )
                            )
                        }

                        return@onEach
                    }
                    currentDownloadJob?.let {
                        _downloadJobListState.value = _downloadJobListState.value.map {
                            if (it.downloadId == downloadEntity.id) {
                                it.copy(
                                    totalProgress = index + 1 / downloadEntity.imgUrls.size.toFloat(),
                                    currentProgress = float
                                )
                            } else {
                                it
                            }
                        }
                    }
                }.catch { error ->
                    val downloadJob =
                        _downloadJobListState.value.find { it.downloadId == downloadEntity.id }
                    downloadJob?.let {
                        downloadJob.downloadJob.cancel()
                        _downloadJobListState.value = _downloadJobListState.value.map {
                            if (it.downloadId == downloadEntity.id) {
                                it.copy(
                                    error = error.message ?: "Unknown error"
                                )
                            } else {
                                it
                            }
                        }
                    }

                }.collect()
                val download = appDatabase.downloadDao().getDownload(downloadEntity.id)
                appDatabase.bookDao().insert(
                    BookEntity(
                        title = downloadEntity.title,
                        pathUrls = download.pathUrls
                    )
                )
                appDatabase.downloadDao().delete(downloadEntity)
                getDownloadList()
            }
            _downloadJobListState.value += DownloadJob(
                preview = downloadEntity.imgUrls.first(),
                downloadId = downloadEntity.id,
                downloadJob = downloadJob
            )
            downloadJob.await()
        }
    }
}

sealed interface DownloadScreenAction {
    data class InsertDownload(val url: String, val closeDialog: () -> Unit) : DownloadScreenAction
}

data class DownloadJob(
    val downloadId: Int,
    val preview: String = "",
    val error: String = "",
    val totalProgress: Float = 0f,
    val currentProgress: Float = 0f,
    val downloadJob: Job,
)