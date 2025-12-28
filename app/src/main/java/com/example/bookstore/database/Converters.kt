package com.example.bookstore.database

import androidx.room.TypeConverter
import com.example.bookstore.model.OrderItem
import com.example.bookstore.model.OrderStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromOrderItemsList(value: List<OrderItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toOrderItemsList(value: String): List<OrderItem> {
        return try {
            val type = object : TypeToken<List<OrderItem>>() {}.type
            gson.fromJson(value, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String {
        return value.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.valueOf(value)
    }
}