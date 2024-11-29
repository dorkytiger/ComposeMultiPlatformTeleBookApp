package com.dorkytiger.top.persistence.screen.page

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.persistence.component.LoadingView
import com.dorkytiger.top.util.DisplayResult
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScreen(
    navBookScreen: () -> Unit
) {
    val viewModel = koinViewModel<PageScreenModel>()

    val bookInfoState by viewModel.bookInfoState
    val index by viewModel.currentPage
    var showBottomBar by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(bookInfoState.isSuccess()) {
                        Text(
                            bookInfoState.getSuccessData().title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navBookScreen()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            ) {
                if (bookInfoState.isSuccess() && showBottomBar) {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
                    ) {
                        itemsIndexed(bookInfoState.getSuccessData().pathUrls) { index, url ->
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(50.dp)
                                    .clickable {
                                        viewModel.onEvent(PageScreenEvent.ChangePage(index))
                                    }
                            ) {
                                CoilImage(
                                    imageModel = { url },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            bookInfoState.DisplayResult(
                onSuccess = { bookEntity ->
                    AnimatedContent(
                        targetState = index,
                    ) { targetState ->
                        Box(
                            modifier = Modifier
                                .animateContentSize()
                                .fillMaxSize().onSizeChanged {
                                }
                                .pointerInput(Unit) {
                                    var dragAmount = 0f
                                    var isPageChangeTriggered = false // 防止多次触发页面切换

                                    detectHorizontalDragGestures(
                                        onDragStart = { _ ->
                                            dragAmount = 0f
                                            isPageChangeTriggered = false // 在拖动开始时重置标志
                                        },
                                        onHorizontalDrag = { change, dragDelta ->
                                            dragAmount += dragDelta

                                            val dragThreshold = 150f

                                            // 只有在没有触发过一次页面切换时才进行判断
                                            if (!isPageChangeTriggered) {
                                                if (dragAmount > dragThreshold) {
                                                    // 向右拖动，触发上一页
                                                    viewModel.onEvent(PageScreenEvent.PreviousPage)
                                                    isPageChangeTriggered = true // 标记为已触发
                                                    dragAmount = 0f // 重置拖动距离
                                                } else if (dragAmount < -dragThreshold) {
                                                    // 向左拖动，触发下一页
                                                    viewModel.onEvent(PageScreenEvent.NextPage)
                                                    isPageChangeTriggered = true // 标记为已触发
                                                    dragAmount = 0f // 重置拖动距离
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            dragAmount = 0f
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            CoilImage(
                                imageModel = { bookEntity.pathUrls[targetState] },
                                modifier = Modifier.fillMaxWidth(),
                                imageOptions = ImageOptions(
                                    alignment = Alignment.Center,
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            viewModel.onEvent(PageScreenEvent.PreviousPage)
                                        }
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            showBottomBar = !showBottomBar
                                        }
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            viewModel.onEvent(PageScreenEvent.NextPage)
                                        }
                                )
                            }
                        }
                    }
                },
                onLoading = {
                    LoadingView()
                },
                onError = {
                    Text(it)
                }
            )
        }
    }
}