package com.example.data.repository

import com.example.data.db.SpendlyDao
import com.example.data.model.BudgetSettingEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class SpendlyRepository(private val dao: SpendlyDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val budgetSetting: Flow<BudgetSettingEntity?> = dao.getBudgetSetting()

    suspend fun getTransactionById(id: Long): TransactionEntity? {
        return dao.getTransactionById(id)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        dao.deleteTransactionById(id)
    }

    suspend fun upsertBudgetSetting(setting: BudgetSettingEntity) {
        dao.upsertBudgetSetting(setting)
    }
}
