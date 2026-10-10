package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Song
import com.example.model.SubscriptionTier
import com.example.model.UserProfile
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.HeartRed
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.UltraGold

@Composable
fun HomeScreen(
  songs: List<Song>,
  userProfile: UserProfile,
  selectedGenreFilter: String,
  onSelectGenreFilter: (String) -> Unit,
  onPlaySong: (Song) -> Unit,
  onToggleLike: (String) -> Unit,
  onRequestDownload: (Song) -> Unit,
  onOpenComments: (Song) -> Unit,
  onUpgradeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val genres = listOf("All", "Sinhala", "Acoustic", "EDM", "Lo-Fi", "Cyberpunk")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Top Bar Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Good day, ${userProfile.name.split(" ").first()}",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          )
          Text(
            text = "Discover Ceylon hits & global rhythms",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
          )
        }

        // Tier Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
              when (userProfile.subscriptionTier) {
                SubscriptionTier.ULTRA -> UltraGold
                SubscriptionTier.PRO -> ProPurple
                SubscriptionTier.FREE -> Color(0xFF333333)
              }
            )
            .clickable(onClick = onUpgradeClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) {
              Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
              text = userProfile.subscriptionTier.badgeText,
              color = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) Color.Black else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Free Tier Ad Banner Simulation (as requested by user: "free version with ads")
    if (userProfile.subscriptionTier == SubscriptionTier.FREE) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF202020))
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "SPONSORED • Upgrade to Ad-Free",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = SpotifyGreen,
                  fontWeight = FontWeight.Bold
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Get Pro (LKR 1,500) or Ultra (LKR 5,000) for zero ads & offline downloads!",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = onUpgradeClick,
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = SpotifyGreen,
                contentColor = Color.Black
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
              Text("Upgrade", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }
    }

    // Hero Live Festival Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceCard)
      ) {
        Image(
          painter = painterResource(id = R.drawable.music_hero_banner),
          contentDescription = "Future Sounds Festival Live",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xCC000000))
              )
            )
            .padding(14.dp),
          contentAlignment = Alignment.BottomStart
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Column {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0x99000000))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("FEATURED SPOTLIGHT", color = SpotifyGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Future Sounds Live Festival",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )
            }
            if (songs.isNotEmpty()) {
              IconButton(
                onClick = { onPlaySong(songs[0]) },
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(SpotifyGreen)
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Genre filter chips row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        genres.forEach { genre ->
          val isSelected = selectedGenreFilter == genre
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) SpotifyGreen else Color(0xFF282828))
              .clickable { onSelectGenreFilter(genre) }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = genre,
              color = if (isSelected) Color.Black else Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // Trending Cards Carousel
    item {
      Text(
        text = "Trending in Sri Lanka",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Spacer(modifier = Modifier.height(10.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(songs.take(4), key = { "trend_${it.id}" }) { song ->
          TrendingSongCard(
            song = song,
            onPlay = { onPlaySong(song) },
            onToggleLike = { onToggleLike(song.id) }
          )
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }

    // Scrollable Song Feed (Like/Comment/Play)
    item {
      Text(
        text = "Top Song Feed & Releases",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    items(songs, key = { it.id }) { song ->
      FeedSongRowItem(
        song = song,
        onPlay = { onPlaySong(song) },
        onToggleLike = { onToggleLike(song.id) },
        onRequestDownload = { onRequestDownload(song) },
        onOpenComments = { onOpenComments(song) }
      )
      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}

@Composable
fun TrendingSongCard(
  song: Song,
  onPlay: () -> Unit,
  onToggleLike: () -> Unit
) {
  Box(
    modifier = Modifier
      .width(155.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(DarkSurfaceCard)
      .clickable(onClick = onPlay)
      .padding(10.dp)
  ) {
    Column {
      // Album cover box
      Box(
        modifier = Modifier
          .size(135.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(
            Brush.linearGradient(
              listOf(Color(song.coverColor1), Color(song.coverColor2))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = song.title.take(1),
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        // Play button bubble
        Box(
          modifier = Modifier
            .padding(8.dp)
            .size(32.dp)
            .clip(CircleShape)
            .background(SpotifyGreen)
            .align(Alignment.BottomEnd),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

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
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun FeedSongRowItem(
  song: Song,
  onPlay: () -> Unit,
  onToggleLike: () -> Unit,
  onRequestDownload: () -> Unit,
  onOpenComments: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .clickable(onClick = onPlay)
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Thumbnail
    Box(
      modifier = Modifier
        .size(52.dp)
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
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    // Title & Info
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

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${song.artist} • ${song.formattedDuration}",
          style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (song.isAiGenerated) {
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "AI Generated",
            tint = ProPurple,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }

    // Like Action with counter
    IconButton(onClick = onToggleLike) {
      Icon(
        imageVector = if (song.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = "Like",
        tint = if (song.isLiked) HeartRed else Color.LightGray,
        modifier = Modifier.size(20.dp)
      )
    }

    // Comment Action with counter
    IconButton(onClick = onOpenComments) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.Chat,
        contentDescription = "Comments",
        tint = Color.LightGray,
        modifier = Modifier.size(20.dp)
      )
    }

    // Offline Download icon (Ultra exclusive)
    IconButton(onClick = onRequestDownload) {
      if (song.isDownloaded) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Offline cached",
          tint = SpotifyGreen,
          modifier = Modifier.size(20.dp)
        )
      } else {
        Icon(
          imageVector = Icons.Default.Download,
          contentDescription = "Download",
          tint = Color.Gray,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
