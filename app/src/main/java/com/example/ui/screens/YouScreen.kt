package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.LocalizationManager
import com.example.model.Song
import com.example.model.SubscriptionTier
import com.example.model.UserProfile
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.HeartRed
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.UltraGold

@Composable
fun YouScreen(
  userProfile: UserProfile,
  appLanguage: AppLanguage,
  onSelectLanguage: (AppLanguage) -> Unit,
  isFloatingBubbleEnabled: Boolean,
  onToggleFloatingBubble: (Boolean) -> Unit,
  onOpenLockScreenPlayer: () -> Unit,
  uploadedDjMixes: List<Song>,
  onPlaySong: (Song) -> Unit,
  onToggleLike: (String) -> Unit,
  isDarkMode: Boolean,
  onToggleDarkMode: () -> Unit,
  onToggleSignIn: () -> Unit,
  onStartGooglePayCheckout: (SubscriptionTier) -> Unit,
  onToggleOfflineOnly: (Boolean) -> Unit,
  onOpenCustomerSupport: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalizationManager.getStrings(appLanguage)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("you_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Top Title
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = strings.youTab,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Google User Profile Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceCard)
          .padding(16.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Google Avatar
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userProfile.name.firstOrNull()?.uppercase() ?: "P",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = SpotifyGreen
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = userProfile.name,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Google "G" verified mark
                Box(
                  modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "G",
                    color = Color(0xFF4285F4),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }

              Text(
                text = userProfile.email,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
              )

              Spacer(modifier = Modifier.height(4.dp))

              // Tier badge
              Text(
                text = "Plan: ${userProfile.subscriptionTier.displayName.uppercase()} (Active until ${userProfile.subscriptionActiveUntil})",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) UltraGold else SpotifyGreen,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = onToggleSignIn,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("google_login_toggle_button"),
            shape = RoundedCornerShape(20.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "G",
                color = Color(0xFF4285F4),
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (userProfile.isSignedIn) "Switch Google Account" else "Sign in with Google",
                color = Color.White
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // --- UPLOADED DJ MIXES SECTION (Requested by User in Settings) ---
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = strings.uploadedMixesTitle,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
          Text(
            text = strings.uploadedMixesDesc,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2E1A47))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("2 TRACKS", color = ProPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(uploadedDjMixes, key = { "uploaded_${it.id}" }) { song ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceCard)
          .clickable { onPlaySong(song) }
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(
                  listOf(Color(song.coverColor1), Color(song.coverColor2))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "DJ", fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = song.title,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${song.artist} • ${song.formattedDuration} • Party Baila",
              style = MaterialTheme.typography.bodySmall.copy(color = SpotifyGreen),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          IconButton(onClick = { onToggleLike(song.id) }) {
            Icon(
              imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Like",
              tint = if (song.isLiked) HeartRed else Color.LightGray,
              modifier = Modifier.size(20.dp)
            )
          }

          IconButton(
            onClick = { onPlaySong(song) },
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(SpotifyGreen)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    item {
      Spacer(modifier = Modifier.height(14.dp))
    }

    // --- DISPLAY LANGUAGE SELECTOR (English, Spanish, Chinese, Sinhala) ---
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Language, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = strings.languageSetting,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AppLanguage.values().forEach { lang ->
          val isSelected = appLanguage == lang
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) SpotifyGreen else DarkSurfaceCard)
              .clickable { onSelectLanguage(lang) }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = lang.flag, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = lang.displayName,
                color = if (isSelected) Color.Black else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // --- PRO LOCK SCREEN PLAYER TRIGGER ---
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceCard)
          .border(1.dp, ProPurple, RoundedCornerShape(16.dp))
          .clickable(onClick = onOpenLockScreenPlayer)
          .padding(16.dp)
          .testTag("lock_screen_player_trigger")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color(0xFF2C1E3C)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = ProPurple, modifier = Modifier.size(24.dp))
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = strings.lockScreenPlayer,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(ProPurple)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(strings.proExclusiveBadge, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
            }
            Text(
              text = strings.lockScreenPlayerDesc,
              style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
            )
          }

          Icon(Icons.Default.OpenInNew, contentDescription = "Open Lock Screen", tint = ProPurple)
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // --- FLOATING POP-UP PLAYER SWITCH ---
    item {
      SettingToggleRow(
        title = strings.floatingPlayer,
        subtitle = strings.floatingPlayerDesc,
        icon = Icons.Default.PictureInPictureAlt,
        isChecked = isFloatingBubbleEnabled,
        onCheckedChange = onToggleFloatingBubble
      )
      Spacer(modifier = Modifier.height(18.dp))
    }

    // Subscription Plans Header
    item {
      Text(
        text = "MEMBERSHIP TIERS (සාමාජිකත්ව පැකේජ)",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color.LightGray,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Ultra Tier Card (Recommended)
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF201A03))
          .border(2.dp, UltraGold, RoundedCornerShape(16.dp))
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
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = UltraGold,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Ultra Tier",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(UltraGold)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text("BEST VALUE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "LKR 5,000 / month  •  LKR 48,000 / year (20% OFF)",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = UltraGold,
              fontWeight = FontWeight.Bold
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          TierPerk("👑 Exclusive: Unlimited Offline Song Downloads & Cache", UltraGold)
          TierPerk("🔒 Pro Lock Screen Ambient Player Included", UltraGold)
          TierPerk("🔊 Lossless FLAC Audio (1411 kbps Master Quality)", UltraGold)
          TierPerk("⚡ Unlimited Google Lyria & Suno AI Music Generation", UltraGold)
          TierPerk("🚫 Zero Ads & Unlimited Track Skips", UltraGold)

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = { onStartGooglePayCheckout(SubscriptionTier.ULTRA) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("subscribe_ultra_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = UltraGold,
              contentColor = Color.Black
            ),
            shape = RoundedCornerShape(22.dp)
          ) {
            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) "Current Active Plan" else "Subscribe Ultra via Google Pay",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Pro Tier Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceCard)
          .border(1.dp, ProPurple, RoundedCornerShape(16.dp))
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
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = ProPurple,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Pro Tier",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
            }

            Text(
              text = "LKR 1,500 / month",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = ProPurple,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          TierPerk("🔒 Pro Lock Screen Ambient Player Access", ProPurple)
          TierPerk("🚫 Ad-Free Music Streaming Experience", ProPurple)
          TierPerk("🎧 High Definition Audio (320 kbps MP3)", ProPurple)
          TierPerk("⏭ Unlimited Skips & Audio Equalizer", ProPurple)
          TierPerk("❌ Offline Downloads not included (Ultra only)", Color.Gray)

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = { onStartGooglePayCheckout(SubscriptionTier.PRO) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("subscribe_pro_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = ProPurple,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(22.dp)
          ) {
            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (userProfile.subscriptionTier == SubscriptionTier.PRO) "Current Active Plan" else "Subscribe Pro via Google Pay",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Free Tier Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceCard)
          .padding(16.dp)
      ) {
        Column {
          Text(
            text = "Free Tier",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Standard 160 kbps streaming with occasional sponsor audio ads.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Preferences & Settings
    item {
      Text(
        text = "PREFERENCES & SUPPORT",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color.LightGray,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Offline Mode Only Toggle
    item {
      SettingToggleRow(
        title = "Offline Mode Only (නොබැඳි ප්රකාරය)",
        subtitle = "Play only downloaded and cached songs on device without internet",
        icon = Icons.Default.CloudOff,
        isChecked = userProfile.isOfflineModeOnly,
        onCheckedChange = onToggleOfflineOnly
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Dark Mode Switch
    item {
      SettingToggleRow(
        title = "Dark Mode (අඳුරු තේමාව)",
        subtitle = "Spotify AMOLED black aesthetic",
        icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
        isChecked = isDarkMode,
        onCheckedChange = { onToggleDarkMode() }
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Customer Support Button
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(DarkSurfaceCard)
          .clickable(onClick = onOpenCustomerSupport)
          .padding(16.dp)
          .testTag("customer_support_button")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.SupportAgent,
            contentDescription = null,
            tint = SpotifyGreen,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "24/7 Customer Support & Help Desk",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
            Text(
              text = "Live assistance for Ultra subscriptions, offline listening & FAQs",
              style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
          }
          Text(text = "Help", color = SpotifyGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
      Spacer(modifier = Modifier.height(24.dp))
    }

    // App Version
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "VibeStream v1.2 • YouTube & Spotify Multi-Million Catalog • Google Lyria Audio",
          style = MaterialTheme.typography.labelSmall.copy(color = Color.DarkGray)
        )
      }
    }
  }
}

@Composable
fun TierPerk(text: String, color: Color) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 2.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall.copy(color = color)
    )
  }
}

@Composable
fun SettingToggleRow(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceCard)
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = SpotifyGreen,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
      }
      Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
          checkedThumbColor = Color.Black,
          checkedTrackColor = SpotifyGreen
        )
      )
    }
  }
}
