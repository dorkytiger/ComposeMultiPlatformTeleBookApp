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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DownloadScreenModel(
    private val appDatabase: AppDatabase,
) : ViewModel() {

    private var _downloadListState: MutableState<RequestState<List<DownloadEntity>>> =
        mutableStateOf(RequestState.Loading)
    val downloadListState: MutableState<RequestState<List<DownloadEntity>>> = _downloadListState

    private var _downloadProgressList: MutableState<List<DownloadProgress>> =
        mutableStateOf(emptyList())
    val downloadProgressList = _downloadProgressList

    private var _decodeUrlState: MutableState<RequestState<Unit>> =
        mutableStateOf(RequestState.Idle)
    val decodeUrlState = _decodeUrlState

    private val downloadJobList: MutableState<List<DownloadJob>> = mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            getDownloadList()
        }
    }

    fun onAction(action: DownloadScreenAction) {
        when (action) {
            is DownloadScreenAction.InsertDownload -> {
                insertDownload(action.url)
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
                if (downloadJobList.value.any { job -> job.downloadId == it.id }) {
                    return@forEach
                }
                startDownload(it)
            }
            _downloadListState.value = RequestState.Success(downloadList)
        }
    }

    private fun insertDownload(url: String) {
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
            val downloadJob = viewModelScope.async {
                val pathUrlList = mutableListOf<String>()
                HTMLUtils.getImgByteByImgUrlList(downloadEntity.imgUrls)
                    .onStart {
                        if (_downloadProgressList.value.any { it.downloadId == downloadEntity.id }) {
                            return@onStart
                        }
                        _downloadProgressList.value += DownloadProgress(
                            downloadId = downloadEntity.id
                        )
                    }.onEach { (index, float, byteArray) ->
                        if (index < downloadEntity.completeIndex) {
                            return@onEach
                        }
                        val downloadProgress = _downloadProgressList.value.toMutableList()
                        val progress = downloadProgress.find { it.downloadId == downloadEntity.id }
                        if (float == 1f) {
                            val pathUrl = FileUtil.saveImagesToFolder(
                                "${downloadEntity.id}-${downloadEntity.title}",
                                index.toString(),
                                byteArray
                            )
                            pathUrlList += pathUrl
                            appDatabase.downloadDao().update(
                                downloadEntity.copy(
                                    pathUrls = pathUrlList,
                                    completeIndex = index
                                )
                            )
                            return@onEach
                        }
                        progress?.let {
                            downloadProgress[downloadProgress.indexOf(progress)] = progress.copy(
                                totalProgress = (index + 1).toFloat() / downloadEntity.imgUrls.size,
                                currentProgress = float,
                                preview = downloadEntity.imgUrls[0]
                            )
                            _downloadProgressList.value = downloadProgress
                        }
                    }.catch { error ->
                        downloadJobList.value = downloadJobList.value.map { downloadJob ->
                            if (downloadJob.downloadId == downloadEntity.id) {
                                downloadJob.copy(error = error.message ?: "Unknown error")
                            } else {
                                downloadJob
                            }
                        }
                        error.printStackTrace()
                        delay(1000)
                        appDatabase.downloadDao().update(
                            downloadEntity.copy(
                                completeIndex = 0
                            )
                        )
                        getDownloadList()
                    }.collect()
                appDatabase.bookDao().insert(
                    BookEntity(
                        title = downloadEntity.title,
                        pathUrls = pathUrlList
                    )
                )
                downloadJobList.value =
                    downloadJobList.value.filter { it.downloadId != downloadEntity.id }
                downloadProgressList.value =
                    downloadProgressList.value.filter { it.downloadId != downloadEntity.id }
                appDatabase.downloadDao().delete(downloadEntity)
                getDownloadList()
            }
            if (downloadJobList.value.any { it.downloadId == downloadEntity.id }) {
                return@launch
            }
            downloadJobList.value += DownloadJob(downloadEntity.id, downloadJob)
            downloadJob.await()
        }
    }
}

sealed interface DownloadScreenAction {
    data class InsertDownload(val url: String) : DownloadScreenAction
}

data class DownloadProgress(
    val downloadId: Int = 0,
    val preview: String = "",
    val totalProgress: Float = 0f,
    val currentProgress: Float = 0f,
)

data class DownloadJob(
    val downloadId: Int,
    val downloadJob: Job,
    val error: String = "",
)