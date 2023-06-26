package com.example.prayercaptious

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}! Learning is going to be hell T_T"
    }
}