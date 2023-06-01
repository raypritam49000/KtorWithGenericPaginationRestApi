package com.mongo.db.rest.api.routes

import com.mongo.db.rest.api.database.BookRepository
import com.mongo.db.rest.api.dtos.ApiResponse
import com.mongo.db.rest.api.dtos.BookDto
import com.mongo.db.rest.api.dtos.BookSearchParams
import com.mongo.db.rest.api.dtos.UserDto
import com.mongo.db.rest.api.entities.Book
import com.mongo.db.rest.api.entities.User
import com.mongo.db.rest.api.search.BasePageDTO
import com.mongo.db.rest.api.search.PaginationParams
import com.mongo.db.rest.api.search.PaginationUtils
import com.mongo.db.rest.api.search.SortingParams
import com.mongodb.client.model.Filters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun Application.bookRoutes(bookRepository: BookRepository) {
    routing {

        route("/books"){

            post("/search"){

                val requestBody : BookSearchParams = call.receive<BookSearchParams>();

                println("@@@ requestBody : $requestBody");

                // Database Connection and Get Collections
                val client = KMongo.createClient().coroutine
                val database = client.getDatabase("users")
                val bookCollection: CoroutineCollection<Book> = database.getCollection()

                //  Define multiple filters
                val bookIdFilter : Bson = if(requestBody.bookId.isNullOrBlank()) Filters.empty() else Filters.eq("bookId", requestBody.bookId);
                val titleFilter: Bson = if(requestBody.title.isNullOrBlank()) Filters.empty() else Filters.eq("title", requestBody.title);
                val descriptionFilter: Bson = if(requestBody.description.isNullOrBlank()) Filters.empty() else Filters.eq("description", requestBody.description);
                val itemsFilter: Bson =  if(requestBody.items==null) Filters.empty() else Filters.eq("items", requestBody.items);
                val priceFilter: Bson =  if(requestBody.price==null) Filters.empty() else Filters.eq("price", requestBody.price);

                // Define pagination Parameter
                val paginationParams = PaginationParams(page = requestBody.pageNumber, pageSize = requestBody.pageSize);

                // Define Sorting parameters
                val sortingParams = SortingParams(column = requestBody.sort, sortOrder = requestBody.sortOrder)

                // Pagination Object
                val paginationUtils = PaginationUtils()

                // Find documents matching the combined filter and populate BasePageDTO
                val basePageDTO: BasePageDTO<Book> = paginationUtils.getPaginatedResults(
                    "Book Details Services",
                    bookCollection,
                    paginationParams,
                    sortingParams,
                    bookIdFilter,
                    titleFilter,
                    descriptionFilter,
                    itemsFilter,
                    priceFilter
                )

                call.respond(HttpStatusCode.OK,basePageDTO )

            }


            post("/") {
                val requestBody : BookDto = call.receive<BookDto>();
                val book = bookRepository.addBook(Book(null, requestBody.title, requestBody.description, requestBody.price,requestBody.items));
                return@post call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        true,
                        201,
                        "Created",
                        "Book has been created",
                        BookDto(book.bookId, book.title, book.description, book.price,book.items)))

            }

            get("/") {
                val books: List<BookDto> = bookRepository.getAllBook().map { book ->
                    BookDto(book.bookId, book.title, book.description, book.price,book.items)
                }
                return@get call.respond(HttpStatusCode.OK, ApiResponse(true, 200, "OK", "Books Details Lists", books))
            }


            get("/{bookId}") {
                val bookId: String = call.parameters["bookId"].toString()
                val book: Book? = bookId.let { it1 -> bookRepository.getBookById(it1) }
                if (book != null) {
                    return@get call.respond(HttpStatusCode.OK, ApiResponse(true, 200, "OK", "Book Details", book))
                } else {
                    return@get call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(false, 404, "Not Found", "Book not found", "")
                    )
                }
            }

            delete("/{bookId}") {
                val bookId: String = call.parameters["bookId"].toString()
                val isDeleted: Boolean = bookId.let { bookRepository.deleteBookById(it) } ?: false
                if (isDeleted) {
                    return@delete call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(true, 200, "OK", "Book has been deleted", "")
                    )
                } else {
                    return@delete call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(false, 404, "Not Found", "Book not found", "")
                    )
                }
            }

            put("/{bookId}") {
                val bookId: String = call.parameters["bookId"].toString()
                val bookDto = call.receive<BookDto>()
                val book = bookId.let { it1 -> bookRepository.getBookById(it1) }

                if (book != null) {
                    // map BookDto to Book entity
                    val updatedBook = book.copy(
                        title = bookDto.title ?: book.title,
                        description = bookDto.description ?: book.description,
                        items = bookDto.items ?: book.items,
                        price = bookDto.price ?: book.price,
                    )

                    // update book in the database
                    bookRepository.updateBookById(bookId,updatedBook);

                    call.respond(HttpStatusCode.OK, ApiResponse(true, 200, "OK", "Book has been updated", ""))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiResponse(false, 404, "Not Found", "Book not found", ""))
                }
            }


        }
    }
}