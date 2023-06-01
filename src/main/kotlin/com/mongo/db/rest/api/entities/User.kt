package com.mongo.db.rest.api.entities

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId
    val userId: String? = ObjectId().toString(),
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val city: String?,
    val salary: String?
)