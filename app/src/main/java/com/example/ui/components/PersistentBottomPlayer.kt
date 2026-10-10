package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Song
import com.example.player.PlaybackState
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.HeartRed
import com.example.ui.theme.SpotifyGreen

/**
 * A persistent bottom music player control component featuring:
 * - Play/Pause toggling with buffering indicator
 * - Skip Next and Skip Previous controls
 * - Interactive seeking & progress bar with elapsed and remaining timestamps
 * - Song metadata, album artwork, animated audio equalizer
 * - Full-screen expansion click handler
 * - Like/favorite toggle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersistentBottomPlayer(
  playbackState: PlaybackState,
  fallbackSong: Song?,
  onOpenFullScreen: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onPlayPrevious: () -> Unit,
  onSeekTo: (Long) -> Unit,
  onToggleLike: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val song = playbackState.currentSong ?: fallbackSong ?: return

  var isUserDragging by remember { mutableStateOf(false) }
  var dragFraction by remember { mutableFloatStateOf(0f) }

  val effectiveFraction = if (isUserDragging) dragFraction else playbackState.progressFraction
  val totalMs = playbackState.totalDurationMs.coerceAtLeast((song.durationSeconds * 1000).toLong())
  val currentMs = if (isUserDragging) (dragFraction * totalMs).toLong() else playbackState.currentPositionMs

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
      .background(DarkSurfaceHighlight)
      .testTag("persistent_bottom_player"),
    color = Color.Transparent,
    tonalElevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(song.coverColor1).copy(alpha = 0.25f),
              DarkSurfaceHighlight,
              DarkSurfaceCard
            )
          )
        )
    ) {
      // Top interactive progress scrubber row
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 12.dp, end = 12.dp, top = 4.dp)
      ) {
        Slider(
          value = effectiveFraction.coerceIn(0f, 1f),
          onValueChange = { frac ->
            isUserDragging = true
            dragFraction = frac
          },
          onValueChangeFinished = {
            val targetMs = (dragFraction * totalMs).toLong()
            onSeekTo(targetMs)
            isUserDragging = false
          },
          colors = SliderDefaults.colors(
            thumbColor = SpotifyGreen,
            activeTrackColor = SpotifyGreen,
            inactiveTrackColor = Color(0x33FFFFFF)
          ),
          thumb = {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color.White)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .testTag("bottom_player_progress_slider")
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = formatSeconds(currentMs / 1000),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.LightGray,
              fontSize = 10.sp
            )
          )
          Text(
            text = formatSeconds(totalMs / 1000),
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color.Gray,
              fontSize = 10.sp
            )
          )
        }
      }

      // Middle Player Control Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Thumbnail & Animated Soundwave
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
              Brush.linearGradient(
                listOf(Color(song.coverColor1), Color(song.coverColor2))
              )
            )
            .clickable(onClick = onOpenFullScreen),
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

          if (song.isDownloaded) {
            Box(
              modifier = Modifier
                .padding(2.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(SpotifyGreen)
                .align(Alignment.BottomEnd),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(10.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Title & Artist (clickable to expand)
        Column(
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onOpenFullScreen)
        ) {
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
            text = song.artist,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color.LightGray
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Like Button
        IconButton(
          onClick = { onToggleLike(song.id) },
          modifier = Modifier
            .size(40.dp)
            .testTag("bottom_player_like_button")
        ) {
          Icon(
            imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like Song",
            tint = if (song.isLiked) HeartRed else Color.LightGray,
            modifier = Modifier.size(20.dp)
          )
        }

        // Skip Previous
        IconButton(
          onClick = onPlayPrevious,
          modifier = Modifier
            .size(40.dp)
            .testTag("bottom_player_skip_prev")
        ) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = "Previous Track",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }

        // Play / Pause Button
        IconButton(
          onClick = onTogglePlayPause,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("bottom_player_play_pause")
        ) {
          if (playbackState.isBuffering) {
            CircularProgressIndicator(
              modifier = Modifier.size(22.dp),
              strokeWidth = 2.5.dp,
              color = Color.Black
            )
          } else {
            Icon(
              imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
              tint = Color.Black,
              modifier = Modifier.size(26.dp)
            )
          }
        }

        // Skip Next
        IconButton(
          onClick = onPlayNext,
          modifier = Modifier
            .size(40.dp)
            .testTag("bottom_player_skip_next")
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Next Track",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
          )
        }

        // Expand Button
        IconButton(
          onClick = onOpenFullScreen,
          modifier = Modifier
            .size(36.dp)
            .testTag("bottom_player_expand_button")
        ) {
          Icon(
            imageVector = Icons.Default.OpenInFull,
            contentDescription = "Expand Full Player",
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun PulsingEqualizerBars() {
  val transition = rememberInfiniteTransition(label = "pulse_eq")

  val h1 by transition.animateFloat(
    initialValue = 6f,
    targetValue = 22f,
    animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
    label = "h1"
  )
  val h2 by transition.animateFloat(
    initialValue = 18f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
    label = "h2"
  )
  val h3 by transition.animateFloat(
    initialValue = 8f,
    targetValue = 24f,
    animationSpec = infiniteRepeatable(tween(300, easing = LinearEasing), RepeatMode.Reverse),
    label = "h3"
  )
  val h4 by transition.animateFloat(
    initialValue = 16f,
    targetValue = 10f,
    animationSpec = infiniteRepeatable(tween(480, easing = LinearEasing), RepeatMode.Reverse),
    label = "h4"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ) {
    listOf(h1, h2, h3, h4).forEach { heightVal ->
      Box(
        modifier = Modifier
          .padding(horizontal = 1.5.dp)
          .width(3.dp)
          .height(heightVal.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(Color.White)
      )
    }
  }
}

private fun formatSeconds(seconds: Long): String {
  val m = seconds / 60
  val s = seconds % 60
  return "%02d:%02d".format(m, s)
}
