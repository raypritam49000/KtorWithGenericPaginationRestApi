package com.mongo.db.rest.api.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Book(
    @BsonId
    val bookId: String? = ObjectId().toString(),
    val title: String?,
    val description: String?,
    val price : Int?,
    val items : Int?
)
