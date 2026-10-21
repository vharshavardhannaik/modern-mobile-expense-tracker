package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.Tertiary

@Composable
fun TopHeader(
    title: String,
    greeting: String = "Good morning, Sarah",
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Title
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Spendly Logo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    lineHeight = 14.sp
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }
        }

        // Action Buttons: Notifications & Profile
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .testTag("notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                // Unread dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Tertiary)
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryFixed)
                    .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape)
                    .clickable { onProfileClick() }
                    .testTag("profile_avatar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    color = Primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
