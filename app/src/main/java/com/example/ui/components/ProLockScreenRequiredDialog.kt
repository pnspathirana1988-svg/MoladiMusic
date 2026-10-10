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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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
import com.example.model.AppLanguage
import com.example.model.LocalizationManager
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ProPurple

@Composable
fun ProLockScreenRequiredDialog(
  appLanguage: AppLanguage,
  onDismiss: () -> Unit,
  onUpgradeToPro: () -> Unit
) {
  val strings = LocalizationManager.getStrings(appLanguage)

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
            .background(Color(0xFF2C1E3C)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = ProPurple,
            modifier = Modifier.size(26.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = strings.lockScreenPlayer,
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
          text = strings.unlockProPrompt,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color.White,
            fontWeight = FontWeight.SemiBold
          ),
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Pro plan (LKR 1,500/mo) includes Ad-Free streaming, HD 320kbps audio, and Ambient Lock Screen Controls. Upgrade seamlessly with Google Pay!",
          style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray),
          textAlign = TextAlign.Center
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onUpgradeToPro,
        colors = ButtonDefaults.buttonColors(
          containerColor = ProPurple,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("dialog_upgrade_pro_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Upgrade to Pro (LKR 1,500)", fontWeight = FontWeight.Bold)
        }
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
    modifier = Modifier.testTag("pro_lock_screen_required_dialog")
  )
}
