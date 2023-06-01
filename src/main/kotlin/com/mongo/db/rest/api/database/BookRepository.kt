package com.mongo.db.rest.api.database

import com.mongo.db.rest.api.entities.Book
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class BookRepository {
    private val client = KMongo.createClient().coroutine
    private val database = client.getDatabase("users")
    private val bookCollection : CoroutineCollection<Book> = database.getCollection()

    suspend fun addBook(book: Book): Book {
        bookCollection.insertOne(book)
        return book;
    }

    suspend fun getAllBook(): List<Book> =  bookCollection.find().toList();

    suspend fun getBookById(bookId: String): Book? =  bookCollection.findOneById(bookId)

    suspend fun deleteBookById(bookId: String): Boolean {
        val result =  bookCollection.deleteOneById(bookId)
        return result.wasAcknowledged() && result.deletedCount > 0
    }

    suspend fun updateBookById(bookId: String, updatedBook: Book): Boolean =
        bookCollection.updateOneById(bookId, updatedBook).wasAcknowledged()
}