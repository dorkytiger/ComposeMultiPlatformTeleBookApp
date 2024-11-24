package com.dorkytiger.top

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform