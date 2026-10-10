package com.example.model

data class Comment(
  val id: String,
  val songId: String,
  val authorName: String,
  val authorAvatarLetter: String,
  val text: String,
  val timestamp: String,
  val likes: Int = 0,
  val isLiked: Boolean = false
)
