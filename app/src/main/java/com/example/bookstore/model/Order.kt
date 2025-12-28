package com.example.bookstore.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.bookstore.database.Converters

@Entity(tableName = "orders")
@TypeConverters(Converters::class)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val books: List<OrderItem>,
    val totalAmount: Double,
    val shippingAddress: String,
    val status: OrderStatus = OrderStatus.PENDING,
    val orderDate: Long = System.currentTimeMillis(),
    val estimatedDelivery: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000 // 7 days
) {
    fun formattedTotal(): String = "${"%.2f".format(totalAmount)} DT"
}

data class OrderItem(
    val bookId: String,
    val title: String,
    val quantity: Int,
    val unitPrice: Double
) {
    fun subtotal(): Double = unitPrice * quantity
}

enum class OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}