package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BillingCycle
import com.example.model.SubscriptionTier
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.UltraGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GooglePayBottomSheet(
  tier: SubscriptionTier,
  selectedCycle: BillingCycle,
  onCycleSelected: (BillingCycle) -> Unit,
  onDismiss: () -> Unit,
  onPaymentSuccess: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()
  var isProcessingPayment by remember { mutableStateOf(false) }
  var paymentCompleted by remember { mutableStateOf(false) }

  val priceText = if (selectedCycle == BillingCycle.MONTHLY) {
    "LKR ${tier.monthlyPriceLkr.formatNumber()} / month"
  } else {
    "LKR ${tier.yearlyPriceLkr.formatNumber()} / year"
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkSurface,
    modifier = Modifier.testTag("google_pay_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp)
    ) {
      // Google Pay Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Google G icon badge
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color.White),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "G",
              color = Color(0xFF4285F4),
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Pay",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cancel",
            tint = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (!paymentCompleted) {
        // Plan Overview Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceCard)
            .border(
              width = 1.5.dp,
              color = if (tier == SubscriptionTier.ULTRA) UltraGold else ProPurple,
              shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (tier == SubscriptionTier.ULTRA) Icons.Default.WorkspacePremium else Icons.Default.Star,
                  contentDescription = null,
                  tint = if (tier == SubscriptionTier.ULTRA) UltraGold else ProPurple,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "VibeStream ${tier.displayName}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                )
              }

              Text(
                text = priceText,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (tier == SubscriptionTier.ULTRA) UltraGold else SpotifyGreen
                )
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Billing Cycle Switcher (Ultra offers yearly option)
            if (tier == SubscriptionTier.ULTRA) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFF2C2C2C))
                  .padding(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedCycle == BillingCycle.MONTHLY) UltraGold else Color.Transparent)
                    .clickable { onCycleSelected(BillingCycle.MONTHLY) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Monthly (LKR 5,000)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (selectedCycle == BillingCycle.MONTHLY) Color.Black else Color.LightGray
                  )
                }

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedCycle == BillingCycle.YEARLY) UltraGold else Color.Transparent)
                    .clickable { onCycleSelected(BillingCycle.YEARLY) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "Yearly (LKR 48,000) 20% OFF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (selectedCycle == BillingCycle.YEARLY) Color.Black else Color.LightGray
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
            }

            // Benefits list
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              if (tier == SubscriptionTier.ULTRA) {
                BenefitRow("Unlimited Offline Downloads & Cache", UltraGold)
                BenefitRow("Lossless FLAC Hi-Fi Audio (1411 kbps)", UltraGold)
                BenefitRow("Unlimited Google Lyria & Suno AI Generations", UltraGold)
                BenefitRow("Ad-Free Premium Experience", UltraGold)
              } else {
                BenefitRow("Ad-Free Listening", ProPurple)
                BenefitRow("High Definition Audio (320 kbps)", ProPurple)
                BenefitRow("Unlimited Skips & Equalizer", ProPurple)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method Selector
        Text(
          text = "PAYMENT METHOD",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color.LightGray,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceCard)
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CreditCard,
            contentDescription = null,
            tint = Color(0xFF4285F4),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Google Pay •••• 4242",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
              )
            )
            Text(
              text = "Verified via Google Play Billing Services",
              style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
          }
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Encrypted",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pay Button
        Button(
          onClick = {
            scope.launch {
              isProcessingPayment = true
              delay(1600)
              isProcessingPayment = false
              paymentCompleted = true
              delay(900)
              onPaymentSuccess()
            }
          },
          enabled = !isProcessingPayment,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("google_pay_confirm_button"),
          shape = RoundedCornerShape(26.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
          )
        ) {
          if (isProcessingPayment) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              strokeWidth = 2.dp,
              color = Color.Black
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Connecting to Google Pay...", fontWeight = FontWeight.Bold)
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Pay ",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = priceText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "with GPay",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color(0xFF4285F4)
              )
            }
          }
        }
      } else {
        // Success celebration state
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(SpotifyGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Success",
              tint = Color.Black,
              modifier = Modifier.size(36.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Payment Approved!",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Welcome to VibeStream ${tier.displayName}! Offline downloads and premium perks are now unlocked.",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = Color.LightGray
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 20.sp
          )
        }
      }
    }
  }
}

@Composable
fun BenefitRow(text: String, accentColor: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = Icons.Default.Check,
      contentDescription = null,
      tint = accentColor,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
    )
  }
}

private fun Int.formatNumber(): String {
  return String.format("%,d", this)
}
