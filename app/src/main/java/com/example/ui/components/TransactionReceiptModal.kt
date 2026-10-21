package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TransactionEntity
import com.example.ui.theme.Error
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionReceiptModal(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type.equals("income", ignoreCase = true)
    val formattedDate = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
        .format(Date(transaction.timestamp))
    val txCode = "TX-${transaction.id.toString().padStart(6, '0')}"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = SurfaceContainerLowest,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSACTION RECEIPT",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = OnSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon & Merchant Title
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (isIncome) Secondary.copy(alpha = 0.15f) else PrimaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Receipt",
                        tint = if (isIncome) Secondary else Primary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = (if (isIncome) "+$" else "-$") + String.format(Locale.US, "%.2f", transaction.amount),
                    style = MaterialTheme.typography.displayMedium,
                    color = if (isIncome) Secondary else OnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = transaction.category + if (transaction.note.isNotBlank() && transaction.note != transaction.title) " • " + transaction.note else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Details Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow(
                            label = "Payment Method",
                            value = transaction.paymentMethod
                        )
                        DetailRow(
                            label = "Date & Time",
                            value = formattedDate
                        )
                        DetailRow(
                            label = "Status",
                            value = "Settled",
                            isStatus = true
                        )
                        DetailRow(
                            label = "Transaction ID",
                            value = txCode,
                            isMonospace = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("receipt_edit_button"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Error.copy(alpha = 0.12f),
                            contentColor = Error
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("receipt_delete_button"),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isStatus: Boolean = false,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
        if (isStatus) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Secondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = OnSurface,
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
