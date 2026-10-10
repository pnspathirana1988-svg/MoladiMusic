package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.HistoryItem
import com.example.model.AiRecommendation
import com.example.model.Song
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.HeartRed
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen

@Composable
fun HistoryScreen(
  historyItems: List<HistoryItem>,
  aiRecommendations: List<AiRecommendation>,
  onPlaySong: (Song) -> Unit,
  onToggleLike: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("history_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = null,
          tint = SpotifyGreen,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "Listening History",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
          Text(
            text = "Your playback timeline & AI recommendations",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Listening Stats Summary Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(DarkSurfaceCard)
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          StatItem("384", "Minutes Streamed", SpotifyGreen)
          StatItem("${historyItems.size}", "Tracks Played", ProPurple)
          StatItem("Baila / Acoustic", "Top Genre", Color(0xFF00E5FF))
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // AI Song Recommendations Section
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = SpotifyGreen,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "AI Personalized Recommendations",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
      }
      Text(
        text = "Curated by Gemini & Lyria based on your frequent listening vibes",
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(aiRecommendations, key = { "ai_rec_${it.song.id}" }) { rec ->
      AiRecommendationCard(
        recommendation = rec,
        onPlay = { onPlaySong(rec.song) },
        onToggleLike = { onToggleLike(rec.song.id) }
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // Recently Played History List
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "Recently Played Timeline",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    if (historyItems.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No tracks played yet. Start streaming to build history!",
            color = Color.Gray,
            fontSize = 13.sp
          )
        }
      }
    } else {
      items(historyItems, key = { it.id }) { item ->
        HistoryRowItem(
          item = item,
          onPlay = { onPlaySong(item.song) },
          onToggleLike = { onToggleLike(item.song.id) }
        )
        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
fun StatItem(value: String, label: String, accentColor: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Black,
        color = accentColor
      )
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
    )
  }
}

@Composable
fun AiRecommendationCard(
  recommendation: AiRecommendation,
  onPlay: () -> Unit,
  onToggleLike: () -> Unit
) {
  val song = recommendation.song
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .clickable(onClick = onPlay)
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
              Brush.linearGradient(
                listOf(Color(song.coverColor1), Color(song.coverColor2))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = song.title.take(1),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

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
            text = "${song.artist} • ${song.genre}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Match badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E3A28))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${recommendation.matchPercentage}% Match",
            color = SpotifyGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        IconButton(onClick = onPlay) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = SpotifyGreen)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "💡 ${recommendation.reason}",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color(0xFFB0BEC5),
          lineHeight = 16.sp
        )
      )
    }
  }
}

@Composable
fun HistoryRowItem(
  item: HistoryItem,
  onPlay: () -> Unit,
  onToggleLike: () -> Unit
) {
  val song = item.song
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(DarkSurfaceCard)
      .clickable(onClick = onPlay)
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(
          Brush.linearGradient(
            listOf(Color(song.coverColor1), Color(song.coverColor2))
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = song.title.take(1),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

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
        text = "${song.artist} • ${item.timestampText}",
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    IconButton(onClick = onToggleLike) {
      Icon(
        imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = "Like",
        tint = if (song.isLiked) HeartRed else Color.LightGray,
        modifier = Modifier.size(18.dp)
      )
    }

    IconButton(onClick = onPlay) {
      Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Play again",
        tint = Color.White,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}
