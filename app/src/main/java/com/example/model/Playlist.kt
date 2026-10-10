package com.example.model

data class Folder(
  val id: String,
  val name: String,
  val iconName: String = "folder",
  val colorHex: Long = 0xFF1DB954
)

data class Playlist(
  val id: String,
  val name: String,
  val description: String,
  val folderId: String? = null,
  val songIds: List<String> = emptyList(),
  val coverColor1: Long = 0xFF1DB954,
  val coverColor2: Long = 0xFF0D47A1,
  val isCustom: Boolean = true
)
