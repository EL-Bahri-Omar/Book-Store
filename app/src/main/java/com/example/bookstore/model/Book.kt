package com.example.bookstore.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val price: Double,
    val category: String,
    val coverImageUrl: String,
    val isbn: String,
    val pages: Int,
    val publisher: String,
    val publishedDate: String,
    val language: String,
    val stock: Int = 10,
    val rating: Double = 0.0,
    val reviews: Int = 0,
    val isInWishlist: Boolean = false
) {
    fun formattedPrice(): String = "${"%.2f".format(price)} DT"
}