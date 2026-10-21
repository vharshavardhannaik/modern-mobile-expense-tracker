package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondaryFixed
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Outline
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import com.example.viewmodel.SpendlyUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesLedgerScreen(
    state: SpendlyUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit
) {
    // Filter and search transactions
    val filteredTxns = remember(state.transactions, state.searchQuery, state.activeFilterCategory) {
        var list = state.transactions

        // Apply query
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.lowercase().trim()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        it.note.lowercase().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        it.paymentMethod.lowercase().contains(q)
            }
        }

        // Apply category filter
        list = when (state.activeFilterCategory) {
            "food" -> list.filter { it.category.lowercase().contains("food") || it.category.lowercase().contains("cafe") }
            "shopping" -> list.filter { it.category.lowercase().contains("shopping") || it.category.lowercase().contains("grocer") }
            "travel" -> list.filter { it.category.lowercase().contains("transport") || it.category.lowercase().contains("fuel") }
            "bills" -> list.filter { it.category.lowercase().contains("utilit") || it.category.lowercase().contains("subscri") }
            "high-low" -> list.sortedByDescending { it.amount }
            else -> list
        }

        list
    }

    // Group by formatted date label
    val groupedTxns = remember(filteredTxns) {
        val todayCal = Calendar.getInstance()
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        filteredTxns.groupBy { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            when {
                isSameDay(txCal, todayCal) -> "TODAY, " + SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(tx.timestamp)).uppercase()
                isSameDay(txCal, yesterdayCal) -> "YESTERDAY, " + SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(tx.timestamp)).uppercase()
                else -> SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(tx.timestamp)).uppercase()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Monthly Summary Banner
            MonthlySummaryBanner(state = state)
        }

        item {
            // Mini Metrics Row: Volume (28 txns) & Daily Average ($88.57)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    icon = Icons.Default.Receipt,
                    iconBg = PrimaryFixed,
                    iconTint = Primary,
                    label = "Volume",
                    value = "${state.transactions.size} txns",
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    icon = Icons.Default.Analytics,
                    iconBg = SecondaryFixed,
                    iconTint = Secondary,
                    label = "Daily Average",
                    value = "$" + String.format(Locale.US, "%.2f", state.dailyAverage),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // Search Input
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ledger_search_input"),
                placeholder = { Text("Search expenses, merchants, notes...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Outline
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = SurfaceContainerLowest,
                    unfocusedContainerColor = SurfaceContainerLowest
                ),
                singleLine = true
            )
        }

        item {
            // Filter Chips Scrollable Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterPill(
                    label = "All Categories",
                    icon = if (state.activeFilterCategory == "all") Icons.Default.Done else null,
                    isSelected = state.activeFilterCategory == "all",
                    onClick = { onFilterChange("all") }
                )
                FilterPill(
                    label = "Food & Drink",
                    icon = Icons.Default.Restaurant,
                    iconTint = Color(0xFFB45309),
                    isSelected = state.activeFilterCategory == "food",
                    onClick = { onFilterChange("food") }
                )
                FilterPill(
                    label = "Shopping",
                    icon = Icons.Default.ShoppingBag,
                    iconTint = Primary,
                    isSelected = state.activeFilterCategory == "shopping",
                    onClick = { onFilterChange("shopping") }
                )
                FilterPill(
                    label = "Travel",
                    icon = Icons.Default.Flight,
                    iconTint = Tertiary,
                    isSelected = state.activeFilterCategory == "travel",
                    onClick = { onFilterChange("travel") }
                )
                FilterPill(
                    label = "Bills",
                    icon = Icons.Default.Receipt,
                    iconTint = Outline,
                    isSelected = state.activeFilterCategory == "bills",
                    onClick = { onFilterChange("bills") }
                )
                FilterPill(
                    label = "High-Low",
                    icon = Icons.Default.ArrowDownward,
                    iconTint = Secondary,
                    isSelected = state.activeFilterCategory == "high-low",
                    onClick = { onFilterChange("high-low") }
                )
                FilterPill(
                    label = "Oct 1 - Oct 31",
                    icon = Icons.Default.DateRange,
                    iconTint = Outline,
                    isSelected = false,
                    onClick = {}
                )
            }
        }

        // Empty state if no items match
        if (groupedTxns.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "No match",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "Try adjusting your filters or search keywords.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onSearchChange("")
                                onFilterChange("all")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryFixed),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Clear All Filters", color = Primary, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        } else {
            // Grouped transaction lists
            groupedTxns.forEach { (dateHeader, txnsInGroup) ->
                item {
                    val groupTotal = txnsInGroup.sumOf { if (it.type == "expense") -it.amount else it.amount }
                    val totalLabel = (if (groupTotal < 0) "-$" else "+$") + String.format(Locale.US, "%.2f", Math.abs(groupTotal)) + if (groupTotal < 0) " total" else " net"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                            )
                            Text(
                                text = dateHeader,
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = totalLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (groupTotal >= 0) Secondary else OnSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                items(txnsInGroup, key = { it.id }) { tx ->
                    TransactionRowItem(
                        transaction = tx,
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }
        }

        item {
            // Reassurance footnote
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Verified",
                    tint = Secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "All transactions encrypted and verified via Plaid",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MonthlySummaryBanner(state: SpendlyUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "October",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "OCTOBER OVERVIEW",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "$" + String.format(Locale.US, "%,.2f", state.monthlySpend),
                        style = MaterialTheme.typography.displayMedium,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Total spent this calendar cycle",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SecondaryFixed
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = "Trend",
                                tint = OnSecondaryFixed,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "-12% vs last mo",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSecondaryFixed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = "Insights",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sparkline bars (7 bars)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.4f, 0.65f, 0.3f, 0.85f, 0.95f, 0.5f, 0.25f)
                heights.forEachIndexed { idx, frac ->
                    val isPeak = idx == 4
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .height((40 * frac).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (isPeak) Primary else SurfaceContainerHigh)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Week 1", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = OnSurfaceVariant)
                Text("Week 2", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = OnSurfaceVariant)
                Text("Week 3", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = OnSurfaceVariant)
                Text("Week 4 (Now)", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FilterPill(
    label: String,
    icon: ImageVector? = null,
    iconTint: Color = OnSurfaceVariant,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Primary else SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color.White else iconTint,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else OnSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
