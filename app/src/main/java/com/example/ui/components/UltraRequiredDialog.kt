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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubscriptionTier
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.UltraGold

@Composable
fun UltraRequiredDialog(
  onDismiss: () -> Unit,
  onUpgradeToUltra: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    titleContentColor = Color.White,
    textContentColor = Color.LightGray,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFF2C2405)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = UltraGold,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "Ultra Tier Required",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "නොබැඳිව සිංදු අහන්න Download පහසුකම ඇත්තේ Ultra සාමාජිකයින්ට පමණි.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.SemiBold
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Offline downloads are exclusively available for Ultra Tier subscribers (LKR 5,000 / month or LKR 48,000 / year). Upgrade now using Google Pay to unlock unlimited offline song caching & lossless audio!",
          style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onUpgradeToUltra,
        colors = ButtonDefaults.buttonColors(
          containerColor = UltraGold,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("dialog_upgrade_ultra_button")
      ) {
        Text("Upgrade to Ultra (LKR 5,000)", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Not Now", color = Color.LightGray)
      }
    },
    modifier = Modifier.testTag("ultra_required_dialog")
  )
}
