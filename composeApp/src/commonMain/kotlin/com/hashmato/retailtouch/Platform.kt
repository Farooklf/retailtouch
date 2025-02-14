package com.hashmato.retailtouch

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform