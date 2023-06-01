package com.mongo.db.rest.api.dtos

import com.mongo.db.rest.api.search.SortOrder
import kotlinx.serialization.Serializable

@Serializable
data class UserSearchParams(
    val pageNumber: Int = 0,
    val pageSize: Int = 15,
    val sort: String = "firstName",
    val sortOrder: SortOrder = SortOrder.ASC,
    val userId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val city: String? = null,
    val salary: String? = null
)