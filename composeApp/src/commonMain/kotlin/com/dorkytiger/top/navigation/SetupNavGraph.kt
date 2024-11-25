package com.dorkytiger.top.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dorkytiger.top.persistence.screen.book.BookScreen
import com.dorkytiger.top.persistence.screen.download.DownloadScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Book.route
    ) {
        composable(Screen.Book.route) {
            BookScreen(
                navToDownload = {
                    navController.navigate(Screen.Download.route)
                }
            )
        }

        composable(Screen.Download.route) {
            DownloadScreen(
                navBackBook = {
                    navController.navigate(Screen.Book.route)
                }
            )
        }

    }

}