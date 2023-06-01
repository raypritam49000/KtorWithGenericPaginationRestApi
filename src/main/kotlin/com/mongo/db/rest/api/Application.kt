package com.mongo.db.rest.api

import io.ktor.server.application.*
import com.mongo.db.rest.api.plugins.configureRouting


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureRouting()
}
