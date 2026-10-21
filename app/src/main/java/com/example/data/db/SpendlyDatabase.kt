package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BudgetSettingEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [TransactionEntity::class, BudgetSettingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpendlyDatabase : RoomDatabase() {
    abstract fun spendlyDao(): SpendlyDao

    companion object {
        @Volatile
        private var INSTANCE: SpendlyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SpendlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpendlyDatabase::class.java,
                    "spendly_database"
                )
                    .addCallback(SpendlyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SpendlyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.spendlyDao())
                }
            }
        }
    }
}

suspend fun populateInitialData(dao: SpendlyDao) {
    // Default budget settings
    dao.upsertBudgetSetting(
        BudgetSettingEntity(
            id = 1,
            monthlyTarget = 4000.0,
            startingNetBalance = 14850.40,
            diningBudget = 500.0,
            housingBudget = 1200.0,
            shoppingBudget = 500.0,
            transportBudget = 350.0,
            entertainmentBudget = 250.0
        )
    )

    val now = Calendar.getInstance()
    val todayMillis = now.timeInMillis
    
    // Create timestamps for today, yesterday, and earlier in the month
    val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val threeDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) }
    val fourDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -4) }
    val fiveDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5) }
    val tenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -10) }
    val startOfMonth = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }

    val initialTransactions = listOf(
        // Today
        TransactionEntity(
            type = "expense",
            amount = 16.40,
            category = "Food & Dining",
            title = "Sweetgreen",
            note = "Lunch & Dining",
            paymentMethod = "Apple Pay",
            timestamp = todayMillis - (2 * 3600 * 1000)
        ),
        TransactionEntity(
            type = "expense",
            amount = 84.50,
            category = "Shopping",
            title = "Target Superstore",
            note = "Groceries & Home",
            paymentMethod = "Chase Sapphire ••4821",
            timestamp = todayMillis - (4 * 3600 * 1000)
        ),
        // Yesterday
        TransactionEntity(
            type = "expense",
            amount = 45.00,
            category = "Transport",
            title = "Shell Gas Station",
            note = "Fuel & Transport",
            paymentMethod = "Chase Sapphire ••4821",
            timestamp = yesterdayCal.timeInMillis
        ),
        TransactionEntity(
            type = "expense",
            amount = 15.99,
            category = "Entertainment",
            title = "Netflix Subscription",
            note = "Subscriptions",
            paymentMethod = "Amex Gold ••1092",
            timestamp = yesterdayCal.timeInMillis - (3600 * 1000)
        ),
        TransactionEntity(
            type = "expense",
            amount = 9.25,
            category = "Food & Dining",
            title = "Joe & The Juice",
            note = "Cafe & Snacks",
            paymentMethod = "Apple Pay",
            timestamp = yesterdayCal.timeInMillis - (5 * 3600 * 1000)
        ),
        // 3-4 days ago
        TransactionEntity(
            type = "income",
            amount = 1450.00,
            category = "Income",
            title = "Freelance Design Payout",
            note = "Income Credit",
            paymentMethod = "Direct Deposit",
            timestamp = threeDaysAgo.timeInMillis
        ),
        TransactionEntity(
            type = "expense",
            amount = 129.00,
            category = "Shopping",
            title = "Acme Electronics",
            note = "Tech & Gadgets",
            paymentMethod = "Chase Sapphire ••4821",
            timestamp = threeDaysAgo.timeInMillis - (2 * 3600 * 1000)
        ),
        TransactionEntity(
            type = "expense",
            amount = 64.20,
            category = "Groceries",
            title = "Whole Foods Market",
            note = "Weekly Market Groceries",
            paymentMethod = "Checking",
            timestamp = fourDaysAgo.timeInMillis
        ),
        TransactionEntity(
            type = "expense",
            amount = 18.50,
            category = "Transport",
            title = "Uber Ride",
            note = "Transport",
            paymentMethod = "Card ••8912",
            timestamp = fiveDaysAgo.timeInMillis
        ),
        TransactionEntity(
            type = "expense",
            amount = 6.75,
            category = "Food & Dining",
            title = "Blue Bottle Coffee",
            note = "Morning Coffee",
            paymentMethod = "Apple Pay",
            timestamp = fiveDaysAgo.timeInMillis - (3600 * 1000)
        ),
        // Earlier in Month
        TransactionEntity(
            type = "expense",
            amount = 1100.00,
            category = "Housing",
            title = "Apartment Rent",
            note = "Monthly Housing Lease",
            paymentMethod = "Direct Deposit",
            timestamp = startOfMonth.timeInMillis + (24 * 3600 * 1000)
        ),
        TransactionEntity(
            type = "income",
            amount = 4200.00,
            category = "Income",
            title = "Monthly Salary",
            note = "Primary Payroll",
            paymentMethod = "Direct Deposit",
            timestamp = startOfMonth.timeInMillis
        )
    )

    dao.insertAll(initialTransactions)
}
