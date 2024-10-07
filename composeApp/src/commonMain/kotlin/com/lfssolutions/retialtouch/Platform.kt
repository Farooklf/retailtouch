package com.lfssolutions.retialtouch

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform