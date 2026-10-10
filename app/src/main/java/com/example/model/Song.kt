package com.example.model

data class Song(
  val id: String,
  val title: String,
  val artist: String,
  val album: String,
  val durationSeconds: Int,
  val streamUrl: String,
  val coverColor1: Long,
  val coverColor2: Long,
  val genre: String,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val commentsCount: Int = 0,
  val isDownloaded: Boolean = false,
  val isOfflineCached: Boolean = false,
  val isAiGenerated: Boolean = false,
  val aiEngine: String = "",
  val lyrics: String = "",
  val playsCount: Int = 0
) {
  val formattedDuration: String
    get() {
      val minutes = durationSeconds / 60
      val seconds = durationSeconds % 60
      return "%d:%02d".format(minutes, seconds)
    }
}
