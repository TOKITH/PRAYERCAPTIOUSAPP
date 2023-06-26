package com.example.prayercaptious

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform