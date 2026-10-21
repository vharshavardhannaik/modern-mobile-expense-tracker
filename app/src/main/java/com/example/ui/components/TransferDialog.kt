package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun TransferDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, fromAccount: String, toAccount: String, note: String) -> Unit
) {
    var amountInput by remember { mutableStateOf("250.00") }
    var fromAccount by remember { mutableStateOf("Checking Account") }
    var toAccount by remember { mutableStateOf("High-Yield Savings") }
    var transferNote by remember { mutableStateOf("Monthly Rainy Day Transfer") }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Transfer",
                        tint = Primary
                    )
                    Text(
                        text = "Transfer Funds",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { newVal ->
                        amountInput = newVal.filter { it.isDigit() || it == '.' }
                    },
                    label = { Text("Transfer Amount") },
                    prefix = { Text("$ ", color = Primary, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transfer_amount_input")
                )

                OutlinedTextField(
                    value = fromAccount,
                    onValueChange = { fromAccount = it },
                    label = { Text("From Account") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = toAccount,
                    onValueChange = { toAccount = it },
                    label = { Text("To Account") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = transferNote,
                    onValueChange = { transferNote = it },
                    label = { Text("Note / Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                            val amt = amountInput.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onConfirm(amt, fromAccount, toAccount, transferNote)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.testTag("transfer_confirm_btn")
                    ) {
                        Text("Execute Transfer")
                    }
                }
            }
        }
    }
}
