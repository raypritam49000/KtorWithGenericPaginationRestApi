package com.mongo.db.rest.api.routes

import com.mongo.db.rest.api.database.DatabaseFactory
import com.mongo.db.rest.api.dtos.ApiResponse
import com.mongo.db.rest.api.dtos.UserDto
import com.mongo.db.rest.api.dtos.UserSearchParams
import com.mongo.db.rest.api.entities.User
import com.mongo.db.rest.api.search.*
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

fun Application.userRoutes(db: DatabaseFactory) {

    routing {

        route("/users") {


            post("/search") {

                // RequestBody Data
                val requestBody : UserSearchParams = call.receive<UserSearchParams>();

                println("@@@ requestBody : $requestBody");

                // Database Connection and Get Collections
                val client = KMongo.createClient().coroutine
                val database = client.getDatabase("users")
                val userCollection: CoroutineCollection<User> = database.getCollection()

                //  Define multiple filters
                val userIdFilter : Bson = if(requestBody.userId.isNullOrBlank()) Filters.empty() else Filters.eq("userId", requestBody.userId);
                val firstNameFilter: Bson = if(requestBody.firstName.isNullOrBlank()) Filters.empty() else Filters.eq("firstName", requestBody.firstName);
                val lastNameFilter: Bson =  if(requestBody.lastName.isNullOrBlank()) Filters.empty() else Filters.eq("lastName", requestBody.lastName);
                val emailFilter: Bson =  if(requestBody.email.isNullOrBlank()) Filters.empty() else Filters.eq("email", requestBody.email);
                val cityFilter: Bson =  if(requestBody.salary.isNullOrBlank()) Filters.empty() else Filters.eq("salary", requestBody.salary);
                val salaryFilter: Bson =  if(requestBody.city.isNullOrBlank()) Filters.empty() else Filters.eq("city", requestBody.city);

                // Define pagination Parameter
                val paginationParams = PaginationParams(page = requestBody.pageNumber, pageSize = requestBody.pageSize);

                // Define Sorting parameters
                val sortingParams = SortingParams(column = requestBody.sort, sortOrder = requestBody.sortOrder)

                // Pagination Object
                val paginationUtils = PaginationUtils()

                // Find documents matching the combined filter and populate BasePageDTO
                val basePageDTO: BasePageDTO<User> = paginationUtils.getPaginatedResults(
                    "Users Details Services",
                    userCollection,
                    paginationParams,
                    sortingParams,
                    userIdFilter,
                    firstNameFilter,
                    lastNameFilter,
                    emailFilter,
                    cityFilter,
                    salaryFilter
                )

                call.respond(HttpStatusCode.OK,basePageDTO )
            }

            post("/") {
                val requestBody : UserDto = call.receive<UserDto>();
                val user = db.addUser(User(null, requestBody.firstName, requestBody.lastName, requestBody.email,requestBody.city,requestBody.salary));
                return@post call.respond(
                    HttpStatusCode.Created,
                    ApiResponse(
                        true,
                        201,
                        "Created",
                        "User has been created",
                        UserDto(user.userId, user.firstName, user.lastName, user.email,user.city,user.salary)
                    )
                )
            }

            get("/") {

                val users: List<UserDto> = db.getAllUser().map { user ->
                    UserDto(user.userId, user.firstName, user.lastName, user.email,user.city,user.salary)
                }
                return@get call.respond(HttpStatusCode.OK, ApiResponse(true, 200, "OK", "Users Details Lists", users))
            }


            get("/{userId}") {
                val userId: String = call.parameters["userId"].toString()
                val user: User? = userId.let { it1 -> db.getUserById(it1) }
                if (user != null) {
                    return@get call.respond(HttpStatusCode.OK, ApiResponse(true, 200, "OK", "User Details", user))
                } else {
                    return@get call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(false, 404, "Not Found", "User not found", "")
                    )
                }
            }

            delete("/{userId}") {
                val userId: String = call.parameters["userId"].toString()
                val isDeleted: Boolean = userId.let { db.deleteUserById(it) } ?: false
                if (isDeleted) {
                    return@delete call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(true, 200, "OK", "User has been deleted", "")
                    )
                } else {
                    return@delete call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse(false, 404, "Not Found", "User not found", "")
                    )
                }
            }

            put("/{userId}") {
                val userId: String = call.parameters["userId"].toString()
                val userDto = call.receive<UserDto>()
                val user = userId.let { it1 -> db.getUserById(it1) }

                if (user != null) {
                    // map UserDto to User entity
                    val updatedUser = user.copy(
                        firstName = userDto.firstName ?: user.firstName,
                        email = userDto.email ?: user.email,
                        lastName = userDto.lastName ?: user.lastName,
                        city = userDto.city ?: user.city,
                        salary = userDto.salary ?: user.salary
                    )

                    // update user in the database
                    db.updateUserById(userId, updatedUser)

                    call.respond(HttpStatusCode.OK, ApiResponse(true, 200, "OK", "User has been updated", ""))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiResponse(false, 404, "Not Found", "User not found", ""))
                }
            }


        }

    }
}