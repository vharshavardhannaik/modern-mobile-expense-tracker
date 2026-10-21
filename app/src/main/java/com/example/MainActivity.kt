package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.SpendlyDatabase
import com.example.data.repository.SpendlyRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopHeader
import com.example.ui.components.TransactionReceiptModal
import com.example.ui.components.TransferDialog
import com.example.ui.screens.AddExpenseScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesLedgerScreen
import com.example.ui.theme.SpendlyTheme
import com.example.ui.theme.Surface
import com.example.viewmodel.SpendlyViewModel
import com.example.viewmodel.SpendlyViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = SpendlyDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = SpendlyRepository(database.spendlyDao())
        val factory = SpendlyViewModelFactory(repository)

        setContent {
            SpendlyTheme {
                val viewModel: SpendlyViewModel = viewModel(factory = factory)
                SpendlyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SpendlyApp(viewModel: SpendlyViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isAddExpenseOpen by viewModel.isAddExpenseOpen.collectAsStateWithLifecycle()
    val txToEdit by viewModel.transactionToEdit.collectAsStateWithLifecycle()

    var showTransferDialog by remember { mutableStateOf(false) }

    // Intercept back presses
    BackHandler(enabled = isAddExpenseOpen || uiState.selectedTransactionForDetail != null || currentTab != 0) {
        when {
            isAddExpenseOpen -> viewModel.closeAddExpense()
            uiState.selectedTransactionForDetail != null -> viewModel.closeTransactionDetail()
            currentTab != 0 -> viewModel.selectTab(0)
        }
    }

    if (showTransferDialog) {
        TransferDialog(
            onDismiss = { showTransferDialog = false },
            onConfirm = { amount, from, to, note ->
                viewModel.saveTransaction(
                    type = "transfer",
                    amount = amount,
                    category = "Transfer",
                    title = "Transfer to $to",
                    note = "From $from • $note",
                    paymentMethod = from
                )
                showTransferDialog = false
            }
        )
    }

    uiState.selectedTransactionForDetail?.let { tx ->
        TransactionReceiptModal(
            transaction = tx,
            onDismiss = { viewModel.closeTransactionDetail() },
            onEdit = {
                viewModel.closeTransactionDetail()
                viewModel.openAddExpense(tx)
            },
            onDelete = {
                viewModel.deleteTransaction(tx)
            }
        )
    }

    if (isAddExpenseOpen) {
        AddExpenseScreen(
            transactionToEdit = txToEdit,
            onDismiss = { viewModel.closeAddExpense() },
            onSave = { id, type, amount, category, title, note, payment, timestamp ->
                viewModel.saveTransaction(
                    id = id,
                    type = type,
                    amount = amount,
                    category = category,
                    title = title,
                    note = note,
                    paymentMethod = payment,
                    timestamp = timestamp
                )
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface),
            topBar = {
                val headerTitle = when (currentTab) {
                    0 -> "Dashboard"
                    1 -> "Expenses Ledger"
                    2 -> "Analytics"
                    3 -> "Budgets"
                    else -> "Spendly"
                }
                TopHeader(
                    title = headerTitle,
                    onNotificationsClick = {
                        // micro-feedback
                    },
                    onProfileClick = {
                        viewModel.selectTab(3)
                    }
                )
            },
            bottomBar = {
                BottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) },
                    onAddClick = { viewModel.openAddExpense() }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Surface)
            ) {
                when (currentTab) {
                    0 -> DashboardScreen(
                        state = uiState,
                        onAddClick = { viewModel.openAddExpense() },
                        onTransferClick = { showTransferDialog = true },
                        onTargetClick = { viewModel.selectTab(3) },
                        onReportClick = { viewModel.selectTab(2) },
                        onSeeAllClick = { viewModel.selectTab(1) },
                        onTransactionClick = { tx -> viewModel.openTransactionDetail(tx) }
                    )
                    1 -> ExpensesLedgerScreen(
                        state = uiState,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onFilterChange = { viewModel.setFilter(it) },
                        onTransactionClick = { tx -> viewModel.openTransactionDetail(tx) }
                    )
                    2 -> AnalyticsScreen(
                        state = uiState
                    )
                    3 -> BudgetsScreen(
                        state = uiState,
                        onUpdateMonthlyTarget = { viewModel.updateMonthlyBudget(it) },
                        onUpdateCategoryBudget = { cat, amt -> viewModel.updateCategoryBudget(cat, amt) }
                    )
                }
            }
        }
    }
}
