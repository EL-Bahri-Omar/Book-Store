package com.example.bookstore.database

import androidx.room.*
import com.example.bookstore.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: Order): Long

    @Update
    suspend fun update(order: Order)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersByUser(userId: Int): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrder(orderId: Int): Order?

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun delete(orderId: Int)
}