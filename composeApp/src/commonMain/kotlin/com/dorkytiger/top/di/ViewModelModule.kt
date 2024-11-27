package com.dorkytiger.top.di

import org.koin.core.module.dsl.viewModelOf
import com.dorkytiger.top.persistence.screen.book.BookScreenModel
import com.dorkytiger.top.persistence.screen.download.DownloadScreenModel
import com.dorkytiger.top.persistence.screen.page.PageScreenModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::BookScreenModel)
    viewModelOf(::DownloadScreenModel)
    viewModelOf(::PageScreenModel)
}