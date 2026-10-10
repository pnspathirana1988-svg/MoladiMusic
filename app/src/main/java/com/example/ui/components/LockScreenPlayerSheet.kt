package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SwipeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.LocalizationManager
import com.example.player.PlaybackState
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.UltraGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockScreenPlayerSheet(
  playbackState: PlaybackState,
  appLanguage: AppLanguage,
  onUnlockScreen: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onPlayPrevious: () -> Unit,
  onSeekTo: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val song = playbackState.currentSong ?: return
  val strings = LocalizationManager.getStrings(appLanguage)

  val currentTimeStr = SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
  val currentDateStr = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

  // Rotating vinyl transition
  val transition = rememberInfiniteTransition(label = "disc_rot")
  val rotationAngle by transition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(14000, easing = LinearEasing),
      repeatMode = AnimRepeatMode.Restart
    ),
    label = "rotation"
  )

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("lock_screen_player_sheet"),
    color = DarkBackground
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(song.coverColor1).copy(alpha = 0.40f),
              DarkBackground,
              DarkBackground
            )
          )
        )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top status row (Time, Lock Icon, Pro badge)
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(top = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("VibeStream", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            // Pro Active Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(ProPurple)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(strings.proExclusiveBadge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(16.dp))
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Big Digital Clock
          Text(
            text = currentTimeStr,
            style = MaterialTheme.typography.displayLarge.copy(
              fontWeight = FontWeight.Light,
              color = Color.White,
              fontSize = 72.sp
            )
          )
          Text(
            text = currentDateStr,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
          )
        }

        // Center Album Disc
        Box(
          modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .background(Color(0xFF101010))
            .border(3.dp, Color(0x33FFFFFF), CircleShape)
            .rotate(if (playbackState.isPlaying) rotationAngle else 0f),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(190.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(Color(song.coverColor1), Color(song.coverColor2), Color(0xFF181818))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .border(2.dp, Color.White, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.MusicNote, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(28.dp))
            }
          }
        }

        // Song Info & Controls
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = song.title,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
          )

          Text(
            text = "${song.artist} • ${song.album}",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Scrubber
          Slider(
            value = playbackState.progressFraction,
            onValueChange = { fraction ->
              val targetMs = (fraction * playbackState.totalDurationMs).toLong()
              onSeekTo(targetMs)
            },
            colors = SliderDefaults.colors(
              thumbColor = Color.White,
              activeTrackColor = SpotifyGreen,
              inactiveTrackColor = Color(0x33FFFFFF)
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = playbackState.currentPositionFormatted, color = Color.Gray, fontSize = 11.sp)
            Text(text = playbackState.totalDurationFormatted, color = Color.Gray, fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Controls Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onPlayPrevious,
              modifier = Modifier.size(48.dp)
            ) {
              Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(20.dp))

            IconButton(
              onClick = onTogglePlayPause,
              modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(SpotifyGreen)
                .testTag("lock_player_play_pause")
            ) {
              if (playbackState.isBuffering) {
                CircularProgressIndicator(modifier = Modifier.size(26.dp), color = Color.Black, strokeWidth = 2.5.dp)
              } else {
                Icon(
                  imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "Play/Pause",
                  tint = Color.Black,
                  modifier = Modifier.size(34.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(20.dp))

            IconButton(
              onClick = onPlayNext,
              modifier = Modifier.size(48.dp)
            ) {
              Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(32.dp))
            }
          }
        }

        // Bottom "Slide to Unlock" bar
        Button(
          onClick = onUnlockScreen,
          shape = RoundedCornerShape(24.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x33FFFFFF),
            contentColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .testTag("lock_player_unlock_button")
        ) {
          Icon(Icons.Default.SwipeUp, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = strings.slideToUnlock, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
