package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_settings")
data class BudgetSettingEntity(
    @PrimaryKey
    val id: Int = 1,
    val monthlyTarget: Double = 4000.0,
    val startingNetBalance: Double = 14850.40,
    val diningBudget: Double = 500.0,
    val housingBudget: Double = 1200.0,
    val shoppingBudget: Double = 500.0,
    val transportBudget: Double = 350.0,
    val entertainmentBudget: Double = 250.0
)
