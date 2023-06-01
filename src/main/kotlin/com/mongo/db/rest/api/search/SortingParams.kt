package com.mongo.db.rest.api.search

data class SortingParams(
    val column: String,
    val sortOrder: SortOrder
)