package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSecondaryFixed
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.SecondaryFixedDim
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryFixed
import com.example.viewmodel.SpendlyUiState
import java.util.Locale

@Composable
fun AnalyticsScreen(
    state: SpendlyUiState
) {
    val context = LocalContext.current
    var horizon by remember { mutableStateOf("Month") } // "Week", "Month", "Year", "Custom"
    var currentMonthIndex by remember { mutableStateOf(0) } // offset from October 2023

    val monthNames = listOf("October 2023", "November 2023", "September 2023")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Horizon & Range Selector Control
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
                    listOf("Week", "Month", "Year", "Custom").forEach { opt ->
                        val isSel = horizon == opt
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSel) SurfaceContainerLowest else Color.Transparent,
                            shadowElevation = if (isSel) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { horizon = opt }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSel) Primary else OnSurfaceVariant,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        item {
            // Date Range Navigator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = monthNames[Math.abs(currentMonthIndex % monthNames.size)],
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Secondary)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { currentMonthIndex = (currentMonthIndex - 1 + monthNames.size) % monthNames.size },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { currentMonthIndex = (currentMonthIndex + 1) % monthNames.size },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = OnSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item {
            // Hero Spending Overview Bento Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL MONTH SPENDING",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
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
                                    contentDescription = "Down",
                                    tint = OnSecondaryFixed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "-8.2%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryFixed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "$" + String.format(Locale.US, "%,.2f", state.monthlySpend),
                            style = MaterialTheme.typography.displayMedium,
                            color = OnSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "/ $3,000",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Average & Highest Day
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLow)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Daily Avg",
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text("Daily Average", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Text("$80.00", style = MaterialTheme.typography.headlineSmall, color = OnSurface, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TertiaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Highest Day",
                                    tint = Tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text("Highest Day", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("$240.00", style = MaterialTheme.typography.titleMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                                    Text("Oct 14", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = OnSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Budget Health & Delight Alert Banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SecondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Health",
                            tint = OnSecondaryContainer,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Smart Health Score",
                                style = MaterialTheme.typography.labelLarge,
                                color = OnSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceContainerLowest
                            ) {
                                Text(
                                    text = "A+",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Great job! You are spending 14% less than last month and comfortably on track to save $520.00.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSecondaryContainer,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            // Category Breakdown Section with Donut Visual
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Category Allocation",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Where your funds traveled this month",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Donut Chart Graphic
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryDonutChart()

                        // Donut Center Content
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Largest Slice",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = "Food 38%",
                                style = MaterialTheme.typography.titleMedium,
                                color = Primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$942.40",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Category Progress Ledger Rows
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CategoryProgressRow(
                            name = "Food & Dining",
                            amount = "$942.40",
                            percentage = 38,
                            icon = Icons.Default.Restaurant,
                            color = Primary
                        )
                        CategoryProgressRow(
                            name = "Housing & Rent",
                            amount = "$750.00",
                            percentage = 30,
                            icon = Icons.Default.Home,
                            color = Secondary
                        )
                        CategoryProgressRow(
                            name = "Shopping & Goods",
                            amount = "$347.20",
                            percentage = 14,
                            icon = Icons.Default.ShoppingBag,
                            color = TertiaryContainer
                        )
                        CategoryProgressRow(
                            name = "Transportation",
                            amount = "$248.00",
                            percentage = 10,
                            icon = Icons.Default.DirectionsSubway,
                            color = PrimaryContainer
                        )
                        CategoryProgressRow(
                            name = "Entertainment",
                            amount = "$192.40",
                            percentage = 8,
                            icon = Icons.Default.SportsEsports,
                            color = SecondaryFixedDim
                        )
                    }
                }
            }
        }

        item {
            // Multi-Month Cashflow Trend Visualizer (5-Month Trajectory)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "5-Month Trajectory",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Consistent decline in monthly outflow",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SecondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Decline",
                                    tint = OnSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "-11.4%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5 Bars: Jun, Jul, Aug, Sep, Oct
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        MonthBarItem("Jun", "$2.8k", 0.75f, false)
                        MonthBarItem("Jul", "$3.1k", 0.88f, false)
                        MonthBarItem("Aug", "$2.6k", 0.68f, false)
                        MonthBarItem("Sep", "$2.7k", 0.72f, false)
                        MonthBarItem("Oct", "$2.48k", 0.62f, true)
                    }
                }
            }
        }

        item {
            // Active Recurring Subscriptions Spotlight Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text("Subscriptions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("4 Active • Auto-renews soon", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("$74.96", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = OnSurface)
                            Text("/month", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                    }

                    // 2x2 Grid of Subscriptions
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubscriptionPill("Spotify Duo", "$14.99/mo", Icons.Default.GraphicEq, Modifier.weight(1f))
                            SubscriptionPill("iCloud 2TB", "$9.99/mo", Icons.Default.Cloud, Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubscriptionPill("Netflix 4K", "$22.99/mo", Icons.Default.Movie, Modifier.weight(1f))
                            SubscriptionPill("Equinox Club", "$26.99/mo", Icons.Default.FitnessCenter, Modifier.weight(1f))
                        }
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Subscriptions management opened", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Manage Recurring Outflows", color = Primary, style = MaterialTheme.typography.labelLarge)
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Manage",
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            // Mindful Spending Inspiration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Mindful",
                            tint = SecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "MINDFUL SPENDING",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryFixed,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "You are building true freedom.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Maintaining your $80/day pace ensures you finish this quarter with a record emergency buffer. Keep up the delightful momentum!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnPrimaryContainer,
                        lineHeight = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategoryDonutChart() {
    Canvas(modifier = Modifier.size(150.dp)) {
        val strokeWidth = 32f

        // Draw track
        drawCircle(
            color = Color(0xFFEAEDFF),
            style = Stroke(width = strokeWidth)
        )

        // Arcs: Food (38%), Housing (30%), Shopping (14%), Transport (10%), Entertainment (8%)
        var currentStart = -90f

        val slices = listOf(
            Pair(0.38f * 360f, Color(0xFF3525CD)),
            Pair(0.30f * 360f, Color(0xFF006C49)),
            Pair(0.14f * 360f, Color(0xFFBF0F3C)),
            Pair(0.10f * 360f, Color(0xFF4F46E5)),
            Pair(0.08f * 360f, Color(0xFF4EDEA3))
        )

        for ((sweep, col) in slices) {
            drawArc(
                color = col,
                startAngle = currentStart,
                sweepAngle = sweep - 2f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            currentStart += sweep
        }
    }
}

@Composable
private fun CategoryProgressRow(
    name: String,
    amount: String,
    percentage: Int,
    icon: ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = name,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(name, style = MaterialTheme.typography.labelLarge, color = OnSurface)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(amount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$percentage%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontWeight = FontWeight.Bold)
                }
            }

            LinearProgressIndicator(
                progress = { (percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = SurfaceContainerHigh
            )
        }
    }
}

@Composable
private fun MonthBarItem(
    label: String,
    amountLabel: String,
    heightFraction: Float,
    isCurrent: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(48.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isCurrent) Primary else Color.Transparent
        ) {
            Text(
                text = amountLabel,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = if (isCurrent) Color.White else OnSurfaceVariant,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(28.dp)
                .height((100 * heightFraction).dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(if (isCurrent) Primary else SurfaceContainerHigh)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) Primary else OnSurfaceVariant,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun SubscriptionPill(
    name: String,
    price: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLow,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnSurface, maxLines = 1)
                Text(price, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
        }
    }
}
