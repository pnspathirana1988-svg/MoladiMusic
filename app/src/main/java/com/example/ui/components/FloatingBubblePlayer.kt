package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.player.PlaybackState
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SpotifyGreen
import kotlin.math.roundToInt

@Composable
fun FloatingBubblePlayer(
  playbackState: PlaybackState,
  onOpenApp: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val song = playbackState.currentSong ?: return

  val configuration = LocalConfiguration.current
  val density = LocalDensity.current

  val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
  val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

  // Initial offset near the right edge
  var offsetX by remember { mutableFloatStateOf(screenWidthPx - with(density) { 260.dp.toPx() }) }
  var offsetY by remember { mutableFloatStateOf(screenHeightPx * 0.35f) }
  var isExpanded by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          offsetX = (offsetX + dragAmount.x).coerceIn(16f, screenWidthPx - 200f)
          offsetY = (offsetY + dragAmount.y).coerceIn(60f, screenHeightPx - 250f)
        }
      }
      .testTag("floating_bubble_player")
  ) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = DarkSurface,
      tonalElevation = 16.dp,
      modifier = Modifier
        .shadow(12.dp, RoundedCornerShape(28.dp))
        .border(1.5.dp, SpotifyGreen, RoundedCornerShape(28.dp))
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .padding(horizontal = 8.dp, vertical = 6.dp)
          .clickable { isExpanded = !isExpanded }
      ) {
        // Glowing circular artwork thumbnail
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(Color(song.coverColor1), Color(song.coverColor2))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          if (playbackState.isPlaying) {
            PulsingEqualizerBars()
          } else {
            Text(
              text = song.title.take(1),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Title and controls
        Column(
          modifier = Modifier.width(100.dp)
        ) {
          Text(
            text = song.title,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = song.artist,
            style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Play/Pause button
        IconButton(
          onClick = onTogglePlayPause,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SpotifyGreen)
            .testTag("floating_player_play_pause")
        ) {
          if (playbackState.isBuffering) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              strokeWidth = 2.dp,
              color = Color.Black
            )
          } else {
            Icon(
              imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = "Play/Pause",
              tint = Color.Black,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Skip Next button
        IconButton(
          onClick = onPlayNext,
          modifier = Modifier
            .size(34.dp)
            .testTag("floating_player_skip_next")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Skip Next",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }

        // Return to App Expand button
        IconButton(
          onClick = onOpenApp,
          modifier = Modifier.size(30.dp)
        ) {
          Icon(
            imageVector = Icons.Default.OpenInFull,
            contentDescription = "Open Full App",
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
          )
        }

        // Close/Dismiss bubble
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Dismiss Floating Player",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
