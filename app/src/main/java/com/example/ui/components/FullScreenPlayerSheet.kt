package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.Song
import com.example.player.PlaybackState
import com.example.player.RepeatMode
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.HeartRed
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.UltraGold

@Composable
fun FullScreenPlayerSheet(
  playbackState: PlaybackState,
  onCollapse: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onPlayPrevious: () -> Unit,
  onRewind10: () -> Unit,
  onForward10: () -> Unit,
  onSeekTo: (Long) -> Unit,
  onToggleShuffle: () -> Unit,
  onCycleRepeat: () -> Unit,
  onToggleLike: (String) -> Unit,
  onRequestDownload: (Song) -> Unit,
  onOpenComments: (Song) -> Unit,
  modifier: Modifier = Modifier
) {
  val song = playbackState.currentSong ?: return
  var isLyricsTabOpen by remember { mutableStateOf(false) }

  // Rotating vinyl animation
  val infiniteTransition = rememberInfiniteTransition(label = "disc_rot")
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 12000, easing = LinearEasing),
      repeatMode = AnimRepeatMode.Restart
    ),
    label = "rotation"
  )

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("full_screen_player"),
    color = DarkBackground
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(song.coverColor1).copy(alpha = 0.55f),
              DarkBackground.copy(alpha = 0.95f),
              DarkBackground
            )
          )
        )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .padding(horizontal = 24.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(
            onClick = onCollapse,
            modifier = Modifier.testTag("player_collapse_button")
          ) {
            Icon(
              imageVector = Icons.Default.ExpandMore,
              contentDescription = "Collapse Player",
              tint = Color.White,
              modifier = Modifier.size(32.dp)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "PLAYING FROM PLAYLIST",
              style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
              )
            )
            Text(
              text = song.album,
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Ultra Offline Download Button
          IconButton(
            onClick = { onRequestDownload(song) },
            modifier = Modifier.testTag("player_download_button")
          ) {
            if (song.isDownloaded) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Downloaded Offline",
                tint = SpotifyGreen,
                modifier = Modifier.size(26.dp)
              )
            } else {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download Song",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Artwork / Vinyl Disc
        Box(
          modifier = Modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(Color(0xFF111111))
            .border(4.dp, Color(0x33FFFFFF), CircleShape)
            .rotate(if (playbackState.isPlaying) rotationAngle else 0f),
          contentAlignment = Alignment.Center
        ) {
          // Disc concentric grooves
          Box(
            modifier = Modifier
              .size(230.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(
                    Color(song.coverColor1),
                    Color(song.coverColor2),
                    Color(0xFF191414)
                  )
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            // Center spindle
            Box(
              modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .border(2.dp, Color.White, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = SpotifyGreen,
                modifier = Modifier.size(32.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tags & Badges
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x33FFFFFF))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = song.genre,
              style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
            )
          }

          if (song.isAiGenerated) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(ProPurple)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = song.aiEngine.ifBlank { "Google Lyria" },
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title & Artist with Like & Comments
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = song.title,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = song.artist,
              style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.LightGray
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Comments button
          IconButton(
            onClick = { onOpenComments(song) },
            modifier = Modifier.testTag("player_comments_button")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Comments",
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
              )
              if (song.commentsCount > 0) {
                Text(
                  text = " ${song.commentsCount}",
                  color = Color.LightGray,
                  fontSize = 12.sp
                )
              }
            }
          }

          // Like Button
          IconButton(
            onClick = { onToggleLike(song.id) },
            modifier = Modifier.testTag("player_like_button")
          ) {
            Icon(
              imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Like",
              tint = if (song.isLiked) HeartRed else Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrubber / Seek Bar
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
          modifier = Modifier.testTag("player_seek_slider")
        )

        // Time labels
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = playbackState.currentPositionFormatted,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
          )
          Text(
            text = playbackState.totalDurationFormatted,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Playback Controls Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Shuffle
          IconButton(
            onClick = onToggleShuffle,
            modifier = Modifier.testTag("player_shuffle_button")
          ) {
            Icon(
              imageVector = Icons.Default.Shuffle,
              contentDescription = "Shuffle",
              tint = if (playbackState.isShuffleEnabled) SpotifyGreen else Color.LightGray,
              modifier = Modifier.size(24.dp)
            )
          }

          // Previous
          IconButton(
            onClick = onPlayPrevious,
            modifier = Modifier.testTag("player_prev_button")
          ) {
            Icon(
              imageVector = Icons.Default.SkipPrevious,
              contentDescription = "Previous",
              tint = Color.White,
              modifier = Modifier.size(34.dp)
            )
          }

          // Rewind 10s
          IconButton(
            onClick = onRewind10,
            modifier = Modifier.testTag("player_rewind10_button")
          ) {
            Icon(
              imageVector = Icons.Default.Replay10,
              contentDescription = "Rewind 10 seconds",
              tint = Color.LightGray,
              modifier = Modifier.size(28.dp)
            )
          }

          // Main Play / Pause Button
          IconButton(
            onClick = onTogglePlayPause,
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(SpotifyGreen)
              .testTag("player_play_pause_fab")
          ) {
            if (playbackState.isBuffering) {
              CircularProgressIndicator(
                color = Color.Black,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
              )
            } else {
              Icon(
                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier.size(38.dp)
              )
            }
          }

          // Forward 10s
          IconButton(
            onClick = onForward10,
            modifier = Modifier.testTag("player_forward10_button")
          ) {
            Icon(
              imageVector = Icons.Default.Forward10,
              contentDescription = "Forward 10 seconds",
              tint = Color.LightGray,
              modifier = Modifier.size(28.dp)
            )
          }

          // Next
          IconButton(
            onClick = onPlayNext,
            modifier = Modifier.testTag("player_next_button")
          ) {
            Icon(
              imageVector = Icons.Default.SkipNext,
              contentDescription = "Next",
              tint = Color.White,
              modifier = Modifier.size(34.dp)
            )
          }

          // Repeat Mode
          IconButton(
            onClick = onCycleRepeat,
            modifier = Modifier.testTag("player_repeat_button")
          ) {
            val icon = when (playbackState.repeatMode) {
              RepeatMode.ONE -> Icons.Default.RepeatOne
              else -> Icons.Default.Repeat
            }
            val tint = when (playbackState.repeatMode) {
              RepeatMode.OFF -> Color.LightGray
              else -> SpotifyGreen
            }
            Icon(
              imageVector = icon,
              contentDescription = "Repeat",
              tint = tint,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lyrics / Waveform toggle card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { isLyricsTabOpen = !isLyricsTabOpen }
            .padding(16.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "LYRICS & AUDIO VISUALIZER",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                  color = SpotifyGreen
                )
              )
              Text(
                text = if (isLyricsTabOpen) "Hide" else "Show",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
              )
            }

            AnimatedVisibility(visible = isLyricsTabOpen) {
              Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                  text = song.lyrics.ifBlank { "No synchronized lyrics available for this track." },
                  style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    lineHeight = 24.sp
                  ),
                  textAlign = TextAlign.Start
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }
}
