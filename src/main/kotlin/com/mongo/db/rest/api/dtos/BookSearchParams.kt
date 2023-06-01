package com.mongo.db.rest.api.dtos

import com.mongo.db.rest.api.search.SortOrder
import kotlinx.serialization.Serializable

@Serializable
data class BookSearchParams(
    val pageNumber: Int = 0,
    val pageSize: Int = 15,
    val sort: String = "title",
    val sortOrder: SortOrder = SortOrder.ASC,
    val bookId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val price : Int? = null,
    val items : Int? = null
)
