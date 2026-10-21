package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
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
import com.example.viewmodel.SpendlyUiState
import java.util.Locale

@Composable
fun BudgetsScreen(
    state: SpendlyUiState,
    onUpdateMonthlyTarget: (Double) -> Unit,
    onUpdateCategoryBudget: (String, Double) -> Unit
) {
    val context = LocalContext.current
    var isEditOverallOpen by remember { mutableStateOf(false) }
    var editCategoryName by remember { mutableStateOf<String?>(null) }
    var editCategoryCurrentVal by remember { mutableStateOf(0.0) }

    if (isEditOverallOpen) {
        EditBudgetDialog(
            title = "Set Monthly Target",
            initialAmount = state.budgetSetting.monthlyTarget,
            onDismiss = { isEditOverallOpen = false },
            onConfirm = { newAmt ->
                onUpdateMonthlyTarget(newAmt)
                isEditOverallOpen = false
                Toast.makeText(context, "Monthly budget updated to $$newAmt", Toast.LENGTH_SHORT).show()
            }
        )
    }

    editCategoryName?.let { cat ->
        EditBudgetDialog(
            title = "Set $cat Budget",
            initialAmount = editCategoryCurrentVal,
            onDismiss = { editCategoryName = null },
            onConfirm = { newAmt ->
                onUpdateCategoryBudget(cat, newAmt)
                editCategoryName = null
                Toast.makeText(context, "$cat budget updated to $$newAmt", Toast.LENGTH_SHORT).show()
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Monthly Budget Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "OCTOBER BUDGET TARGET",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$" + String.format(Locale.US, "%,.2f", state.budgetSetting.monthlyTarget),
                                style = MaterialTheme.typography.displayMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { isEditOverallOpen = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("edit_monthly_budget_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Budget",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Progress bar
                    val spentPercent = if (state.budgetSetting.monthlyTarget > 0) {
                        (state.monthlySpend / state.budgetSetting.monthlyTarget).toFloat().coerceIn(0f, 1f)
                    } else 0f

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: $" + String.format(Locale.US, "%,.2f", state.monthlySpend),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "Left: $" + String.format(Locale.US, "%,.2f", state.remainingBudget),
                                style = MaterialTheme.typography.labelMedium,
                                color = SecondaryFixed,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { spentPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SecondaryFixed,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                    }

                    // Safe Daily Spend Velocity
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alert",
                                tint = SecondaryFixed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Safe Pace: $" + String.format(Locale.US, "%.2f", state.remainingBudget / 10.0) + "/day to stay within limit",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        item {
            // Category Budgets Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category Limits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    text = "Tap any to edit",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }

        item {
            // Category List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CategoryBudgetCard(
                    categoryName = "Food & Dining",
                    spent = 942.40,
                    limit = state.budgetSetting.diningBudget,
                    icon = Icons.Default.Restaurant,
                    color = Primary,
                    onClick = {
                        editCategoryName = "Food & Dining"
                        editCategoryCurrentVal = state.budgetSetting.diningBudget
                    }
                )

                CategoryBudgetCard(
                    categoryName = "Housing",
                    spent = 750.00,
                    limit = state.budgetSetting.housingBudget,
                    icon = Icons.Default.Home,
                    color = Secondary,
                    onClick = {
                        editCategoryName = "Housing"
                        editCategoryCurrentVal = state.budgetSetting.housingBudget
                    }
                )

                CategoryBudgetCard(
                    categoryName = "Shopping",
                    spent = 347.20,
                    limit = state.budgetSetting.shoppingBudget,
                    icon = Icons.Default.ShoppingBag,
                    color = Tertiary,
                    onClick = {
                        editCategoryName = "Shopping"
                        editCategoryCurrentVal = state.budgetSetting.shoppingBudget
                    }
                )

                CategoryBudgetCard(
                    categoryName = "Transportation",
                    spent = 248.00,
                    limit = state.budgetSetting.transportBudget,
                    icon = Icons.Default.Commute,
                    color = PrimaryContainer,
                    onClick = {
                        editCategoryName = "Transportation"
                        editCategoryCurrentVal = state.budgetSetting.transportBudget
                    }
                )

                CategoryBudgetCard(
                    categoryName = "Entertainment",
                    spent = 192.40,
                    limit = state.budgetSetting.entertainmentBudget,
                    icon = Icons.Default.Movie,
                    color = SecondaryFixed,
                    onClick = {
                        editCategoryName = "Entertainment"
                        editCategoryCurrentVal = state.budgetSetting.entertainmentBudget
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryBudgetCard(
    categoryName: String,
    spent: Double,
    limit: Double,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val progress = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
    val isOver = spent > limit

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = categoryName,
                            tint = color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(categoryName, style = MaterialTheme.typography.labelLarge, color = OnSurface, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (isOver) "Over budget!" else "$${(limit - spent).toInt()} left",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOver) Tertiary else OnSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$${spent.toInt()} / $${limit.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isOver) Tertiary else color,
                trackColor = SurfaceContainerHigh
            )
        }
    }
}

@Composable
private fun EditBudgetDialog(
    title: String,
    initialAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var textInput by remember { mutableStateOf(initialAmount.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceContainerLowest,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { newVal ->
                        textInput = newVal.filter { it.isDigit() || it == '.' }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("$ ", color = Primary, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("budget_amount_field")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = OnSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = textInput.toDoubleOrNull() ?: initialAmount
                            onConfirm(amt)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Save Target")
                    }
                }
            }
        }
    }
}
