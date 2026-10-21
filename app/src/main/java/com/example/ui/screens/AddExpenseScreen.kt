package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnPrimaryFixed
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Outline
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    transactionToEdit: TransactionEntity?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        type: String,
        amount: Double,
        category: String,
        title: String,
        note: String,
        paymentMethod: String,
        timestamp: Long
    ) -> Unit
) {
    val context = LocalContext.current
    var currentType by remember { mutableStateOf(transactionToEdit?.type ?: "expense") }
    var amount by remember { mutableDoubleStateOf(transactionToEdit?.amount ?: 42.50) }
    var rawAmountInput by remember { mutableStateOf(String.format(Locale.US, "%.2f", amount)) }
    var selectedCategory by remember { mutableStateOf(transactionToEdit?.category ?: "Food & Dining") }
    var note by remember { mutableStateOf(transactionToEdit?.note ?: "Dinner with team at Osteria") }
    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var selectedPayment by remember { mutableStateOf(transactionToEdit?.paymentMethod ?: "Chase Sapphire ••4821") }
    var timestamp by remember { mutableLongStateOf(transactionToEdit?.timestamp ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val categories = listOf(
        CategoryItem("Food & Dining", "Food", Icons.Default.Restaurant),
        CategoryItem("Groceries", "Groceries", Icons.Default.ShoppingBasket),
        CategoryItem("Transport", "Transport", Icons.Default.DirectionsCar),
        CategoryItem("Shopping", "Shopping", Icons.Default.ShoppingBag),
        CategoryItem("Entertainment", "Fun", Icons.Default.Movie),
        CategoryItem("Utilities", "Utilities", Icons.Default.Bolt),
        CategoryItem("Health", "Health", Icons.Default.Favorite),
        CategoryItem("Other", "Other", Icons.Default.MoreHoriz)
    )

    val paymentOptions = listOf(
        Pair("Chase Sapphire ••4821", Icons.Default.Contactless),
        Pair("Apple Pay", Icons.Default.PhoneIphone),
        Pair("Cash", Icons.Default.Payments),
        Pair("Amex Gold ••1092", Icons.Default.CreditCard)
    )

    val dateStr = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault()).format(Date(timestamp))

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = timestamp)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        timestamp = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("add_expense_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Spendly",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (transactionToEdit == null) "Add Expense" else "Edit Expense",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Profile Avatar icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryFixed)
                        .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        item {
            // Interactive Sub Bar (Cancel, Record Status, Save button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("add_expense_cancel_btn")
                ) {
                    Text("Cancel", color = OnSurfaceVariant, style = MaterialTheme.typography.labelLarge)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                    Text(
                        text = if (transactionToEdit == null) "Add Record" else "Edit Record",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }

                Button(
                    onClick = {
                        val finalAmt = rawAmountInput.toDoubleOrNull() ?: amount
                        if (finalAmt <= 0) {
                            Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onSave(
                            transactionToEdit?.id ?: 0L,
                            currentType,
                            finalAmt,
                            selectedCategory,
                            title.ifBlank { note.ifBlank { selectedCategory } },
                            note,
                            selectedPayment,
                            timestamp
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("add_expense_top_save_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Save", color = OnPrimary, style = MaterialTheme.typography.labelLarge)
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = OnPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        item {
            // Segmented Type Selector
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Expense
                    TypeOptionPill(
                        label = "Expense",
                        icon = Icons.Default.ArrowDownward,
                        iconTint = Tertiary,
                        isSelected = currentType == "expense",
                        onClick = { currentType = "expense" },
                        modifier = Modifier.weight(1f)
                    )

                    // Income
                    TypeOptionPill(
                        label = "Income",
                        icon = Icons.Default.ArrowUpward,
                        iconTint = Secondary,
                        isSelected = currentType == "income",
                        onClick = { currentType = "income" },
                        modifier = Modifier.weight(1f)
                    )

                    // Transfer
                    TypeOptionPill(
                        label = "Transfer",
                        icon = Icons.Default.SyncAlt,
                        iconTint = Primary,
                        isSelected = currentType == "transfer",
                        onClick = { currentType = "transfer" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            // Hero Amount Display & Keypad / Increments
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ENTER AMOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.displayMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // Amount input or display
                        BasicAmountInput(
                            value = rawAmountInput,
                            onValueChange = { newVal ->
                                val filtered = newVal.filter { it.isDigit() || it == '.' }
                                rawAmountInput = filtered
                                filtered.toDoubleOrNull()?.let { amount = it }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Increment Delighters: +$5, +$10, +$25, clear
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IncrementButton("+$5") {
                            amount += 5.0
                            rawAmountInput = String.format(Locale.US, "%.2f", amount)
                        }
                        IncrementButton("+$10") {
                            amount += 10.0
                            rawAmountInput = String.format(Locale.US, "%.2f", amount)
                        }
                        IncrementButton("+$25") {
                            amount += 25.0
                            rawAmountInput = String.format(Locale.US, "%.2f", amount)
                        }
                        // Clear button
                        Surface(
                            shape = CircleShape,
                            color = ErrorContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    amount = 0.0
                                    rawAmountInput = "0.00"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Clear",
                                tint = Tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            // Category Selection Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedCategory,
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // 4x2 Category Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.take(4).forEach { cat ->
                            CategoryPill(
                                item = cat,
                                isSelected = selectedCategory == cat.id,
                                onClick = { selectedCategory = cat.id },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.drop(4).take(4).forEach { cat ->
                            CategoryPill(
                                item = cat,
                                isSelected = selectedCategory == cat.id,
                                onClick = { selectedCategory = cat.id },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            // Date & Time Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PrimaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Date",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Date & Time",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.labelLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            // Note & Quick Tags Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Note",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Description & Note",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = {
                            note = it
                            if (title.isBlank()) title = it
                        },
                        placeholder = { Text("e.g. Dinner with team at Osteria") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_note_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = SurfaceContainerHigh,
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow
                        ),
                        singleLine = true
                    )

                    // Suggestions chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Suggestions:",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        listOf("Dinner", "Coffee", "Commute", "Groceries").forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        note = tag
                                        title = tag
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Payment Account Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Payment",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Payment Method",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Secondary)
                            )
                            Text(
                                text = "Active Card",
                                style = MaterialTheme.typography.labelSmall,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Horizontal chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paymentOptions.forEach { (payName, payIcon) ->
                            val isSelected = selectedPayment == payName
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Primary else SurfaceContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedPayment = payName }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = payIcon,
                                        contentDescription = payName,
                                        tint = if (isSelected) Color.White else OnSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = payName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else OnSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Add Receipt Image
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Receipt",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Add Receipt Image",
                                style = MaterialTheme.typography.labelLarge,
                                color = OnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Auto-scan amounts & items",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                Toast.makeText(context, "Receipt camera scan activated", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Scan",
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Scan",
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            // Category Budget Left Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Dining",
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$selectedCategory Budget Left",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = "$348.50 remaining",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.62f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryContainer,
                            trackColor = SurfaceContainer
                        )
                    }
                }
            }
        }

        item {
            // Primary Big Save CTA Button
            val ctaText = "Save ${currentType.replaceFirstChar { it.uppercase() }} ($" + String.format(Locale.US, "%.2f", amount) + ")"
            Button(
                onClick = {
                    val finalAmt = rawAmountInput.toDoubleOrNull() ?: amount
                    if (finalAmt <= 0) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onSave(
                        transactionToEdit?.id ?: 0L,
                        currentType,
                        finalAmt,
                        selectedCategory,
                        title.ifBlank { note.ifBlank { selectedCategory } },
                        note,
                        selectedPayment,
                        timestamp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_expense_main_cta"),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Save",
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = ctaText,
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Transaction syncs instantly with all linked banks.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun BasicAmountInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = MaterialTheme.typography.displayMedium.copy(
            color = OnSurface,
            fontWeight = FontWeight.ExtraBold
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.width(160.dp).testTag("amount_number_input")
    )
}

@Composable
private fun TypeOptionPill(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SurfaceContainerLowest else Color.Transparent,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) iconTint else OnSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Primary else OnSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun IncrementButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CategoryPill(
    item: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("cat_pill_${item.name}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryFixed else SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Primary else SurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = if (isSelected) Color.White else OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) OnPrimaryFixed else OnSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}
