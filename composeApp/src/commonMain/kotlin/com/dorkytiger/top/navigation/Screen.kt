package com.dorkytiger.top.navigation

const val BOOK_ID = "bookID"

sealed class Screen(val route: String) {
    data object Book : Screen("book")
    data object Download : Screen("download")
}