package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.SpendlyDatabase
import com.example.data.model.BudgetSettingEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: SpendlyDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SpendlyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Spendly", appName)
    }

    @Test
    fun `insert and retrieve transaction`() = runBlocking {
        val dao = db.spendlyDao()
        val tx = TransactionEntity(
            type = "expense",
            amount = 42.50,
            category = "Food & Dining",
            title = "Dinner at Osteria",
            note = "Team celebration",
            paymentMethod = "Chase Sapphire ••4821",
            timestamp = System.currentTimeMillis()
        )
        val id = dao.insertTransaction(tx)
        val retrieved = dao.getTransactionById(id)

        assertNotNull(retrieved)
        assertEquals("Dinner at Osteria", retrieved?.title)
        assertEquals(42.50, retrieved?.amount ?: 0.0, 0.001)

        val all = dao.getAllTransactions().first()
        assertEquals(1, all.size)
    }

    @Test
    fun `update budget setting`() = runBlocking {
        val dao = db.spendlyDao()
        val setting = BudgetSettingEntity(
            id = 1,
            monthlyTarget = 4500.0
        )
        dao.upsertBudgetSetting(setting)

        val retrieved = dao.getBudgetSetting().first()
        assertNotNull(retrieved)
        assertEquals(4500.0, retrieved?.monthlyTarget ?: 0.0, 0.001)
    }
}
