package com.dorkytiger.top.persistence.screen.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dorkytiger.top.persistence.component.LoadingView
import com.dorkytiger.top.util.DisplayResult
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Page")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navBookScreen()
                        }
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())
        ) {
            bookInfoState.DisplayResult(
                onSuccess = { bookEntity ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CoilImage(
                            imageModel = { bookEntity.pathUrls[index] },
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .background(Color.Blue)
                                .fillMaxHeight()
                                .width(200.dp)
                                .align(Alignment.CenterStart)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            viewModel.onEvent(PageScreenEvent.PreviousPage)
                                        }
                                    )
                                }
                        )

                        Box(
                            modifier = Modifier
                                .background(Color.Blue)
                                .fillMaxHeight()
                                .width(200.dp)
                                .align(Alignment.CenterEnd)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            viewModel.onEvent(PageScreenEvent.NextPage)
                                        }
                                    )
                                }
                        )
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