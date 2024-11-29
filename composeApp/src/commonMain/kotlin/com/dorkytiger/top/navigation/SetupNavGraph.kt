package com.dorkytiger.top.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dorkytiger.top.persistence.screen.book.BookScreen
import com.dorkytiger.top.persistence.screen.download.DownloadScreen
import com.dorkytiger.top.persistence.screen.page.PageScreen
import compose.icons.FeatherIcons
import compose.icons.feathericons.Book
import compose.icons.feathericons.Download

@Composable
fun SetupNavGraph(
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination

    val topLevelRoutes = listOf(
        TopLevelRoute("Book", Screen.Book.route, FeatherIcons.Book),
        TopLevelRoute("Download", Screen.Download.route, FeatherIcons.Download)
    )



    Scaffold(
        bottomBar = {
            if (currentDestination?.hierarchy?.any { it.route == Screen.Page.route } == false) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color.White,
                ) {
                    topLevelRoutes.forEach { route ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults.colors().copy(
                                selectedIndicatorColor = Color.White,
                                selectedIconColor = Color.Black,
                                selectedTextColor = Color.Black,
                                unselectedIconColor = Color(0xFFA6A6A6),
                                unselectedTextColor = Color(0xFFA6A6A6),
                            ),
                            icon = {
                                Icon(
                                    imageVector = route.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(route.name, fontWeight = FontWeight.Bold)
                            },
                            selected = currentDestination.hierarchy.any { it.route == route.route },
                            onClick = {
                                navController.navigate(route.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }

        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Screen.Book.route
        ) {
            composable(Screen.Book.route) {
                BookScreen(
                    navPageScreen = {
                        navController.navigate(Screen.Page.passId(it))
                    }
                )
            }

            composable(Screen.Download.route) {
                DownloadScreen(
                )
            }
            composable(
                route = Screen.Page.route,
                arguments = listOf(
                    navArgument(
                        name = BOOK_ID
                    ) {
                        type = NavType.IntType
                        defaultValue = 0
                    },
                )
            ) {
                PageScreen(
                    navBookScreen = {
                        navController.navigate(Screen.Book.route)
                    }
                )
            }
        }
    }
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)