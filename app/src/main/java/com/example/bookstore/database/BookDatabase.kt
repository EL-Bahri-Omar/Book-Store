package com.example.bookstore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bookstore.model.Book
import com.example.bookstore.model.Order
import com.example.bookstore.model.User

@Database(
    entities = [Book::class, User::class, Order::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,
                    "book_database"
                )
                    .fallbackToDestructiveMigration() // clear the old data when schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}