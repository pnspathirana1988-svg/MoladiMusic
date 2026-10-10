package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.model.Song
import com.example.player.PlaybackState

/**
 * Backward compatible wrapper for PersistentBottomPlayer
 */
@Composable
fun MiniPlayerBar(
  playbackState: PlaybackState,
  onOpenFullScreen: () -> Unit,
  onTogglePlayPause: () -> Unit,
  onPlayNext: () -> Unit,
  onToggleLike: (String) -> Unit,
  onPlayPrevious: () -> Unit = {},
  onSeekTo: (Long) -> Unit = {},
  fallbackSong: Song? = null,
  modifier: Modifier = Modifier
) {
  PersistentBottomPlayer(
    playbackState = playbackState,
    fallbackSong = fallbackSong,
    onOpenFullScreen = onOpenFullScreen,
    onTogglePlayPause = onTogglePlayPause,
    onPlayNext = onPlayNext,
    onPlayPrevious = onPlayPrevious,
    onSeekTo = onSeekTo,
    onToggleLike = onToggleLike,
    modifier = modifier
  )
}
