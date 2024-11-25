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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DownloadScreenModel(
    private val appDatabase: AppDatabase,
    private val htmlUtils: HTMLUtils
) : ViewModel() {

    private var _downloadListState: MutableState<RequestState<List<DownloadEntity>>> =
        mutableStateOf(RequestState.Loading)
    val downloadListState: MutableState<RequestState<List<DownloadEntity>>> = _downloadListState

    private var _downloadProgressList: MutableState<List<DownloadProgress>> =
        mutableStateOf(emptyList())
    val downloadProgressList = _downloadProgressList

    private val downloadJobList: MutableState<List<DownloadJob>> = mutableStateOf(emptyList())

    init {
        viewModelScope.launch {
            getDownloadList()
        }
    }

    fun onAction(action: DownloadScreenAction) {
        when (action) {
            is DownloadScreenAction.InsertDownload -> {
                viewModelScope.launch {
                    insertDownload(action.url)
                }
            }
        }
    }

    private suspend fun getDownloadList() {
        _downloadListState.value = RequestState.Loading
        val downloadList = appDatabase.downloadDao().getAll()
        if (downloadList.isEmpty()) {
            _downloadListState.value = RequestState.Empty
            return
        }
        downloadList.forEach {
            if (downloadJobList.value.any { job -> job.downloadId == it.id }) {
                return@forEach
            }
            startDownload(it)
        }
        _downloadListState.value = RequestState.Success(downloadList)
    }

    private suspend fun insertDownload(url: String) {
        val title = htmlUtils.getHtmlTitleFromURL(url)
        val imgUrls = htmlUtils.getImgUrlsFromURL(url)
        val downloadEntity = DownloadEntity(title = title, imgUrls = imgUrls)
        appDatabase.downloadDao().insert(downloadEntity)
        startDownload(downloadEntity)
    }

    private suspend fun startDownload(downloadEntity: DownloadEntity) {
        val downloadJob = viewModelScope.async {
            val pathUrlList = mutableListOf<String>()
            htmlUtils.getImgByteByImgUrlList(downloadEntity.imgUrls)
                .onStart {
                    if (_downloadProgressList.value.any { it.downloadId == downloadEntity.id }) {
                        return@onStart
                    }
                    _downloadProgressList.value += DownloadProgress(
                        downloadId = downloadEntity.id
                    )
                }.onEach { (index, float, byteArray) ->
                    if (
                        downloadEntity.completeIndex == index &&
                        downloadEntity.pathUrls.isNotEmpty()
                    ) {
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
                            totalProgress = index.toFloat() / downloadEntity.imgUrls.size,
                            currentProgress = float,
                            preview = if (index > 0) downloadEntity.imgUrls[index - 1] else ""
                        )
                        _downloadProgressList.value = downloadProgress
                    }
                }.collect()
            appDatabase.bookDao().insert(
                BookEntity(
                    title = downloadEntity.title,
                    pathUrls = downloadEntity.pathUrls
                )
            )
            downloadJobList.value =
                downloadJobList.value.filter { it.downloadId != downloadEntity.id }
        }
        if (downloadJobList.value.any { it.downloadId == downloadEntity.id }) {
            return
        }
        downloadJobList.value += DownloadJob(downloadEntity.id, downloadJob)
        downloadJob.await()
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
    val downloadJob: Job
)