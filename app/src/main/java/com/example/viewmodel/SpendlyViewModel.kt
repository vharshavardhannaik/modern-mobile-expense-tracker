package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.BudgetSettingEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.SpendlyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class CategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Int,
    val iconName: String
)

data class DayTrend(
    val dayLabel: String,
    val amount: Double,
    val isPeak: Boolean
)

data class WeekTrend(
    val weekLabel: String,
    val amount: Double,
    val isPeak: Boolean
)

data class SpendlyUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val budgetSetting: BudgetSettingEntity = BudgetSettingEntity(),
    val totalNetBalance: Double = 14850.40,
    val monthlySpend: Double = 2480.00,
    val remainingBudget: Double = 1520.00,
    val budgetPercentRemaining: Int = 62,
    val categorySummaries: List<CategorySummary> = emptyList(),
    val weeklyTrend: List<DayTrend> = emptyList(),
    val monthlyTrend: List<WeekTrend> = emptyList(),
    val dailyAverage: Double = 88.57,
    val highestDayAmount: Double = 240.00,
    val highestDayDate: String = "Oct 14",
    val searchQuery: String = "",
    val activeFilterCategory: String = "all", // "all", "food", "shopping", "travel", "bills", "high-low"
    val selectedTransactionForDetail: TransactionEntity? = null,
    val isAddExpenseOpen: Boolean = false,
    val transactionToEdit: TransactionEntity? = null
)

class SpendlyViewModel(
    private val repository: SpendlyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow("all")
    val activeFilter = _activeFilter.asStateFlow()

    private val _selectedTransaction = MutableStateFlow<TransactionEntity?>(null)
    val selectedTransaction = _selectedTransaction.asStateFlow()

    private val _currentTab = MutableStateFlow(0) // 0: Home, 1: Expenses, 2: Analytics, 3: Budgets
    val currentTab = _currentTab.asStateFlow()

    private val _isAddExpenseOpen = MutableStateFlow(false)
    val isAddExpenseOpen = _isAddExpenseOpen.asStateFlow()

    private val _transactionToEdit = MutableStateFlow<TransactionEntity?>(null)
    val transactionToEdit = _transactionToEdit.asStateFlow()

    private data class DataSnapshot(
        val txns: List<TransactionEntity>,
        val budget: BudgetSettingEntity,
        val query: String,
        val filter: String
    )

    private data class ModalSnapshot(
        val selectedTx: TransactionEntity?,
        val isAddOpen: Boolean,
        val txToEdit: TransactionEntity?
    )

    private val dataFlow = combine(
        repository.allTransactions,
        repository.budgetSetting,
        _searchQuery,
        _activeFilter
    ) { txns, budget, query, filter ->
        DataSnapshot(txns, budget ?: BudgetSettingEntity(), query, filter)
    }

    private val modalFlow = combine(
        _selectedTransaction,
        _isAddExpenseOpen,
        _transactionToEdit
    ) { selectedTx, isAddOpen, txToEdit ->
        ModalSnapshot(selectedTx, isAddOpen, txToEdit)
    }

    val uiState: StateFlow<SpendlyUiState> = combine(dataFlow, modalFlow) { data, modal ->
        computeUiState(
            txns = data.txns,
            budget = data.budget,
            query = data.query,
            filter = data.filter,
            selectedTx = modal.selectedTx,
            isAddOpen = modal.isAddOpen,
            txToEdit = modal.txToEdit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SpendlyUiState()
    )

    private fun computeUiState(
        txns: List<TransactionEntity>,
        budget: BudgetSettingEntity,
        query: String,
        filter: String,
        selectedTx: TransactionEntity?,
        isAddOpen: Boolean,
        txToEdit: TransactionEntity?
    ): SpendlyUiState {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        val expensesThisMonth = txns.filter {
            it.type.equals("expense", ignoreCase = true) && isSameMonth(it.timestamp, currentMonth, currentYear)
        }
        val incomeThisMonth = txns.filter {
            it.type.equals("income", ignoreCase = true) && isSameMonth(it.timestamp, currentMonth, currentYear)
        }

        val totalExpenses = expensesThisMonth.sumOf { it.amount }
        val totalIncome = incomeThisMonth.sumOf { it.amount }

        val monthlySpend = if (totalExpenses > 0) totalExpenses else 2480.00
        val remainingBudget = maxOf(0.0, budget.monthlyTarget - monthlySpend)
        val percentRemaining = if (budget.monthlyTarget > 0) {
            ((remainingBudget / budget.monthlyTarget) * 100).toInt().coerceIn(0, 100)
        } else 0

        val totalBalance = budget.startingNetBalance + (totalIncome - totalExpenses)

        // Categories breakdown
        val categoryMap = mutableMapOf<String, Double>()
        for (item in expensesThisMonth) {
            val key = when (item.category.lowercase()) {
                "food", "food & dining", "cafe", "dining", "restaurant" -> "Food & Dining"
                "groceries" -> "Groceries"
                "housing", "rent" -> "Housing"
                "transport", "transportation", "fuel" -> "Transportation"
                "shopping", "shopping & goods" -> "Shopping"
                "entertainment", "fun" -> "Entertainment"
                "utilities", "bills" -> "Utilities"
                "health" -> "Health"
                else -> item.category
            }
            categoryMap[key] = (categoryMap[key] ?: 0.0) + item.amount
        }

        // Defaults if empty
        if (categoryMap.isEmpty()) {
            categoryMap["Food & Dining"] = 942.40
            categoryMap["Housing"] = 750.00
            categoryMap["Shopping"] = 347.20
            categoryMap["Transportation"] = 248.00
            categoryMap["Entertainment"] = 192.40
        }

        val catTotal = categoryMap.values.sum()
        val categorySummaries = categoryMap.map { (cat, amt) ->
            val pct = if (catTotal > 0) ((amt / catTotal) * 100).toInt() else 0
            val icon = when (cat) {
                "Food & Dining" -> "restaurant"
                "Housing" -> "home"
                "Transportation" -> "commute"
                "Shopping" -> "shopping_bag"
                "Entertainment" -> "movie"
                "Groceries" -> "shopping_basket"
                "Utilities" -> "bolt"
                "Health" -> "favorite"
                else -> "more_horiz"
            }
            CategorySummary(cat, amt, pct, icon)
        }.sortedByDescending { it.amount }

        // Weekly trend (M, T, W, T, F, S, S)
        val weeklyTrend = listOf(
            DayTrend("M", 45.0, false),
            DayTrend("T", 120.0, false),
            DayTrend("W", 30.0, false),
            DayTrend("T", 85.0, false),
            DayTrend("F", 210.0, true),
            DayTrend("S", 145.0, false),
            DayTrend("S", 60.0, false)
        )

        // Monthly trend (W1, W2, W3, W4)
        val monthlyTrend = listOf(
            WeekTrend("W1", 610.0, false),
            WeekTrend("W2", 540.0, false),
            WeekTrend("W3", 780.0, true),
            WeekTrend("W4", 550.0, false)
        )

        val daysInMonthSoFar = maxOf(1, now.get(Calendar.DAY_OF_MONTH))
        val dailyAvg = monthlySpend / daysInMonthSoFar

        return SpendlyUiState(
            transactions = txns,
            budgetSetting = budget,
            totalNetBalance = totalBalance,
            monthlySpend = monthlySpend,
            remainingBudget = remainingBudget,
            budgetPercentRemaining = percentRemaining,
            categorySummaries = categorySummaries,
            weeklyTrend = weeklyTrend,
            monthlyTrend = monthlyTrend,
            dailyAverage = dailyAvg,
            highestDayAmount = 240.00,
            highestDayDate = "Oct 14",
            searchQuery = query,
            activeFilterCategory = filter,
            selectedTransactionForDetail = selectedTx,
            isAddExpenseOpen = isAddOpen,
            transactionToEdit = txToEdit
        )
    }

    private fun isSameMonth(timeMillis: Long, month: Int, year: Int): Boolean {
        val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        return cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
    }

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    fun openAddExpense(editTx: TransactionEntity? = null) {
        _transactionToEdit.value = editTx
        _isAddExpenseOpen.value = true
    }

    fun closeAddExpense() {
        _isAddExpenseOpen.value = false
        _transactionToEdit.value = null
    }

    fun openTransactionDetail(tx: TransactionEntity) {
        _selectedTransaction.value = tx
    }

    fun closeTransactionDetail() {
        _selectedTransaction.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun saveTransaction(
        id: Long = 0,
        type: String,
        amount: Double,
        category: String,
        title: String,
        note: String,
        paymentMethod: String,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                id = id,
                type = type,
                amount = amount,
                category = category,
                title = if (title.isNotBlank()) title else note.ifBlank { category },
                note = note,
                paymentMethod = paymentMethod,
                timestamp = timestamp
            )
            if (id == 0L) {
                repository.insertTransaction(tx)
            } else {
                repository.updateTransaction(tx)
            }
            closeAddExpense()
            closeTransactionDetail()
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
            closeTransactionDetail()
        }
    }

    fun updateMonthlyBudget(newTarget: Double) {
        viewModelScope.launch {
            val current = uiState.value.budgetSetting
            repository.upsertBudgetSetting(current.copy(monthlyTarget = newTarget))
        }
    }

    fun updateCategoryBudget(category: String, newBudget: Double) {
        viewModelScope.launch {
            val current = uiState.value.budgetSetting
            val updated = when (category) {
                "Dining", "Food & Dining" -> current.copy(diningBudget = newBudget)
                "Housing" -> current.copy(housingBudget = newBudget)
                "Shopping" -> current.copy(shoppingBudget = newBudget)
                "Transportation" -> current.copy(transportBudget = newBudget)
                "Entertainment" -> current.copy(entertainmentBudget = newBudget)
                else -> current
            }
            repository.upsertBudgetSetting(updated)
        }
    }
}

class SpendlyViewModelFactory(private val repository: SpendlyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpendlyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpendlyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
