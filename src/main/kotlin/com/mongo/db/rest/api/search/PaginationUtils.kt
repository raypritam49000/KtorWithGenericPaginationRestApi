package com.mongo.db.rest.api.search

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import kotlin.math.ceil

class PaginationUtils {

    suspend inline fun <reified T : Any> getPaginatedResults(
        genericPluralResourceName : String,
        collection: CoroutineCollection<T>,
        paginationParams: PaginationParams,
        sortingParams: SortingParams?,
        vararg filters: Bson
    ): BasePageDTO<T> {

        val combinedFilter = if (filters.isNotEmpty()) {
            Filters.and(*filters)
        } else {
            Filters.empty()
        }

        val count = collection.countDocuments(combinedFilter)
        val skip = (paginationParams.page - 1) * paginationParams.pageSize

        val sortField = sortingParams?.column ?: ""
        val sortOrder = sortingParams?.sortOrder ?: SortOrder.ASC

        val results = if (sortField.isNotEmpty()) {
            val sort = if (sortOrder == SortOrder.ASC) {
                Sorts.ascending(sortField)
            } else {
                Sorts.descending(sortField)
            }
            collection.find(combinedFilter)
                .sort(sort)
                .skip(skip)
                .limit(paginationParams.pageSize)
                .toList()
        } else {
            collection.find(combinedFilter)
                .skip(skip)
                .limit(paginationParams.pageSize)
                .toList()
        }

        val basePageDTO = BasePageDTO<T>().apply {
            hasContent = results.isNotEmpty()
            hasNext = count > (skip + paginationParams.pageSize)
            hasPrevious = skip > 0
            isFirst = paginationParams.page == 1
            isLast = skip + paginationParams.pageSize >= count
            totalElements = count
            totalPages = ceil(count.toDouble() / paginationParams.pageSize).toInt()
            data = results
            perPage = paginationParams.pageSize
            pageNumber = paginationParams.page
            size = results.size
            pluralResourceName = genericPluralResourceName // Set the plural resource name
            isSorted = sortField.isNotEmpty()
            sortColumn = sortField
            this.sortOrder = sortOrder.toString()
        }

        return basePageDTO
    }

}




