package com.mongo.db.rest.api.plugins

import com.mongo.db.rest.api.database.BookRepository
import com.mongo.db.rest.api.database.DatabaseFactory
import com.mongo.db.rest.api.routes.bookRoutes
import com.mongo.db.rest.api.routes.userRoutes
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureSerialization()
    val databaseFactory = DatabaseFactory();
    userRoutes(databaseFactory);

    val bookRepository = BookRepository();
    bookRoutes(bookRepository);
}
