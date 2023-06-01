package com.mongo.db.rest.api.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val userId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val city: String? = null,
    val salary: String? = null
)