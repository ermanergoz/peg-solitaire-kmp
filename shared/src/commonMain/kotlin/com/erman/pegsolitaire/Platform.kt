package com.erman.pegsolitaire

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform