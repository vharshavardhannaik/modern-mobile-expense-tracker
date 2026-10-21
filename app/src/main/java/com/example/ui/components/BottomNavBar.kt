package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
    ) {
        // Base bar background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter)
                .shadow(elevation = 12.dp, ambientColor = Color(0x15131B2E)),
            color = SurfaceContainerLowest,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Home
                NavBarItem(
                    icon = Icons.Default.Dashboard,
                    label = "Home",
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f).testTag("nav_tab_home")
                )

                // Tab 1: Expenses
                NavBarItem(
                    icon = Icons.Default.ReceiptLong,
                    label = "Expenses",
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f).testTag("nav_tab_expenses")
                )

                // Center placeholder for FAB
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                // Tab 2: Analytics
                NavBarItem(
                    icon = Icons.Default.Analytics,
                    label = "Analytics",
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    modifier = Modifier.weight(1f).testTag("nav_tab_analytics")
                )

                // Tab 3: Budgets
                NavBarItem(
                    icon = Icons.Default.Savings,
                    label = "Budgets",
                    selected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    modifier = Modifier.weight(1f).testTag("nav_tab_budgets")
                )
            }
        }

        // Center Elevated Floating Action Button
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = Primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 2.dp)
                .size(56.dp)
                .testTag("nav_fab_add")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Expense",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = if (selected) Primary else OnSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
