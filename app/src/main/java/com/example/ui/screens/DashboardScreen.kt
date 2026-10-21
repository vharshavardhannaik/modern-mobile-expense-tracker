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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryFixed
import com.example.ui.theme.TertiaryFixedDim
import com.example.viewmodel.SpendlyUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    state: SpendlyUiState,
    onAddClick: () -> Unit,
    onTransferClick: () -> Unit,
    onTargetClick: () -> Unit,
    onReportClick: () -> Unit,
    onSeeAllClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit
) {
    var chartTab by remember { mutableStateOf("week") } // "week" or "month"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Bento Hero Card
            BentoHeroCard(
                state = state,
                onAddClick = onAddClick,
                onTransferClick = onTransferClick,
                onTargetClick = onTargetClick,
                onReportClick = onReportClick
            )
        }

        item {
            // Spending Trend Interactive Chart
            SpendingTrendCard(
                state = state,
                currentTab = chartTab,
                onTabChange = { chartTab = it },
                onInsightsClick = onReportClick
            )
        }

        item {
            // Category Pulse horizontal row
            CategoryPulseSection(state = state)
        }

        item {
            // Recent Transactions Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Secondary)
                    )
                }

                TextButton(
                    onClick = onSeeAllClick,
                    modifier = Modifier.testTag("see_all_transactions_btn")
                ) {
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Recent items (up to 5)
        val recentItems = state.transactions.take(5)
        if (recentItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions logged yet. Tap + to add one!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recentItems, key = { it.id }) { tx ->
                TransactionRowItem(
                    transaction = tx,
                    onClick = { onTransactionClick(tx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BentoHeroCard(
    state: SpendlyUiState,
    onAddClick: () -> Unit,
    onTransferClick: () -> Unit,
    onTargetClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, shape = RoundedCornerShape(24.dp), ambientColor = Primary.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upper: Total Balance & Trending status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "TOTAL NET BALANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnPrimaryContainer,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$" + String.format(Locale.US, "%,.2f", state.totalNetBalance),
                        style = MaterialTheme.typography.displayMedium,
                        color = OnPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SecondaryContainer,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Growth",
                            tint = OnSecondaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+4.8%",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Mid: Two-Column Sub-Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Monthly Spend
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowOutward,
                                contentDescription = "Spend",
                                tint = TertiaryFixedDim,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Monthly Spend",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$" + String.format(Locale.US, "%,.2f", state.monthlySpend),
                            style = MaterialTheme.typography.headlineSmall,
                            color = TertiaryFixedDim,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.transactions.count { it.type == "expense" }} items logged",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Remaining Budget
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Remaining Budget",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnPrimaryContainer
                            )
                            Text(
                                text = "${state.budgetPercentRemaining}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryFixed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$" + String.format(Locale.US, "%,.2f", state.remainingBudget),
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { (state.budgetPercentRemaining / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = SecondaryFixed,
                            trackColor = Color.White.copy(alpha = 0.2f),
                        )
                        Text(
                            text = "of $" + String.format(Locale.US, "%,.2f", state.budgetSetting.monthlyTarget) + " target",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Quick Actions: Add, Transfer, Target, Report
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Add",
                    isPrimary = true,
                    onClick = onAddClick,
                    modifier = Modifier.testTag("bento_add_btn")
                )
                QuickActionButton(
                    icon = Icons.Default.SwapHoriz,
                    label = "Transfer",
                    isPrimary = false,
                    onClick = onTransferClick,
                    modifier = Modifier.testTag("bento_transfer_btn")
                )
                QuickActionButton(
                    icon = Icons.Default.Flag,
                    label = "Target",
                    isPrimary = false,
                    onClick = onTargetClick,
                    modifier = Modifier.testTag("bento_target_btn")
                )
                QuickActionButton(
                    icon = Icons.Default.Summarize,
                    label = "Report",
                    isPrimary = false,
                    onClick = onReportClick,
                    modifier = Modifier.testTag("bento_report_btn")
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isPrimary) Color.White else Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrimary) Primary else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

@Composable
private fun SpendingTrendCard(
    state: SpendlyUiState,
    currentTab: String,
    onTabChange: (String) -> Unit,
    onInsightsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Spending Trend",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily velocity & threshold",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }

                // Segmented control
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceContainer,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (currentTab == "week") Primary else OnSurfaceVariant,
                            fontWeight = if (currentTab == "week") FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (currentTab == "week") SurfaceContainerLowest else Color.Transparent)
                                .clickable { onTabChange("week") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Text(
                            text = "This Month",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (currentTab == "month") Primary else OnSurfaceVariant,
                            fontWeight = if (currentTab == "month") FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (currentTab == "month") SurfaceContainerLowest else Color.Transparent)
                                .clickable { onTabChange("month") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                // Average benchmark indicator line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SurfaceContainerHigh
                    ) {
                        Text(
                            text = "$99/d avg",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Bars view
                if (currentTab == "week") {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        state.weeklyTrend.forEach { day ->
                            val heightFraction = (day.amount / 220.0).toFloat().coerceIn(0.12f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$${day.amount.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (day.isPeak) Primary else OnSurfaceVariant,
                                    fontWeight = if (day.isPeak) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height((100 * heightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (day.isPeak) Primary else SurfaceContainerHigh)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day.dayLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (day.isPeak) Primary else OnSurfaceVariant,
                                    fontWeight = if (day.isPeak) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        state.monthlyTrend.forEach { week ->
                            val heightFraction = (week.amount / 800.0).toFloat().coerceIn(0.2f, 1f)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$${week.amount.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (week.isPeak) Primary else OnSurfaceVariant,
                                    fontWeight = if (week.isPeak) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height((100 * heightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(if (week.isPeak) Primary else SurfaceContainerHigh)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = week.weekLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (week.isPeak) Primary else OnSurfaceVariant,
                                    fontWeight = if (week.isPeak) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Pacing well: 14% below monthly max",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurface
                        )
                    }
                    Text(
                        text = "Insights",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onInsightsClick() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPulseSection(state: SpendlyUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category Pulse",
                style = MaterialTheme.typography.headlineSmall,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "October",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            state.categorySummaries.take(5).forEach { summary ->
                CategoryPulseCard(summary = summary)
            }
        }
    }
}

@Composable
private fun CategoryPulseCard(summary: com.example.viewmodel.CategorySummary) {
    val icon = when (summary.category.lowercase()) {
        "housing", "rent" -> Icons.Default.Home
        "food & dining", "food", "cafe" -> Icons.Default.Restaurant
        "transportation", "transport" -> Icons.Default.Commute
        "entertainment", "fun" -> Icons.Default.Movie
        else -> Icons.Default.ShoppingCart
    }

    val iconBgColor = when (summary.category.lowercase()) {
        "housing" -> PrimaryFixed
        "food & dining", "food" -> SecondaryFixed
        "transportation", "transport" -> SurfaceContainerHigh
        "entertainment" -> TertiaryFixed
        else -> PrimaryFixed
    }

    val iconTintColor = when (summary.category.lowercase()) {
        "housing" -> Primary
        "food & dining", "food" -> Secondary
        "transportation", "transport" -> OnSurface
        "entertainment" -> Tertiary
        else -> Primary
    }

    Card(
        modifier = Modifier
            .width(136.dp)
            .height(106.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = summary.category,
                        tint = iconTintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "${summary.percentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = OnSurfaceVariant
                )
            }

            Column {
                Text(
                    text = summary.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = "$" + String.format(Locale.US, "%,.0f", summary.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val isIncome = transaction.type.equals("income", ignoreCase = true)
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val timeLabel = timeFormat.format(Date(transaction.timestamp))

    val icon: ImageVector = when (transaction.category.lowercase()) {
        "groceries", "grocery" -> Icons.Default.ShoppingCart
        "transport", "transportation" -> Icons.Default.LocalTaxi
        "cafe", "coffee", "food & dining", "food" -> Icons.Default.Coffee
        "income" -> Icons.Default.AccountBalance
        else -> Icons.Default.ShoppingCart
    }

    val iconBg = if (isIncome) SecondaryContainer else SurfaceContainerHigh
    val iconTint = if (isIncome) OnSecondaryContainer else OnSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("txn_item_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = transaction.note.ifBlank { transaction.category },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isIncome) Secondary else OnSurfaceVariant,
                            fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(OutlineVariant)
                        )
                        Text(
                            text = timeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = (if (isIncome) "+$" else "-$") + String.format(Locale.US, "%.2f", transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isIncome) Secondary else OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.paymentMethod,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
