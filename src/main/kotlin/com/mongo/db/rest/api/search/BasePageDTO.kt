package com.mongo.db.rest.api.search

import kotlinx.serialization.Serializable

@Serializable
class BasePageDTO<T> {
    var hasContent: Boolean = false
    var hasNext: Boolean = false
    var hasPrevious: Boolean = false
    var isFirst: Boolean = false
    var isLast: Boolean = false
    var totalElements: Long = 0
    var totalPages: Int = 0
    var data: Collection<T> = emptyList()
    var perPage: Int = 0
    var pageNumber: Int = 0
    var size: Int = 0
    var pluralResourceName: String = ""
    var isSorted: Boolean = false
    var sortColumn: String = ""
    var sortOrder: String = ""
}
