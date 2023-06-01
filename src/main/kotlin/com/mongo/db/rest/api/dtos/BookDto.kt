package com.mongo.db.rest.api.dtos

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class BookDto(
    val bookId: String? = ObjectId().toString(),
    val title: String? = null,
    val description: String? = null,
    val price : Int? = null,
    val items : Int? = null
)
