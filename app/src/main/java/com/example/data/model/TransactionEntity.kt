package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "expense", "income", "transfer"
    val amount: Double,
    val category: String, // "Food & Dining", "Groceries", "Transport", "Shopping", "Entertainment", "Utilities", "Health", "Housing", "Income", "Other"
    val title: String, // e.g. "Sweetgreen", "Whole Foods Market"
    val note: String, // e.g. "Lunch & Dining"
    val paymentMethod: String, // e.g. "Apple Pay", "Chase Sapphire ••4821"
    val timestamp: Long,
    val receiptUri: String? = null
)
