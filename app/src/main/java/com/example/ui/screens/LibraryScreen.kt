package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.model.Folder
import com.example.model.Playlist
import com.example.model.Song
import com.example.model.SubscriptionTier
import com.example.model.UserProfile
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.HeartRed
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.UltraGold

@Composable
fun LibraryScreen(
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  filteredSearchSongs: List<Song>,
  allSongs: List<Song>,
  playlists: List<Playlist>,
  folders: List<Folder>,
  selectedFolderId: String?,
  onSelectFolderId: (String?) -> Unit,
  userProfile: UserProfile,
  onPlaySong: (Song) -> Unit,
  onPlayPlaylist: (Playlist) -> Unit,
  onToggleLike: (String) -> Unit,
  onRequestDownload: (Song) -> Unit,
  onOpenCreatePlaylist: () -> Unit,
  onOpenCreateFolder: () -> Unit,
  onUpgradeToUltra: () -> Unit,
  appLanguage: com.example.model.AppLanguage = com.example.model.AppLanguage.ENGLISH,
  modifier: Modifier = Modifier
) {
  val strings = com.example.model.LocalizationManager.getStrings(appLanguage)
  val tabs = listOf(strings.libraryTab, "Folders", "Downloads", "Liked")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("library_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header & Search Bar
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = strings.libraryTab,
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
        // YouTube & Spotify catalog pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E281E))
            .border(1.dp, SpotifyGreen, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("100M+ YT & Spotify", color = SpotifyGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text(strings.searchPlaceholder, color = Color.Gray, fontSize = 13.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = SpotifyGreen)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray)
            }
          }
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = SpotifyGreen,
          unfocusedBorderColor = Color.DarkGray,
          focusedContainerColor = DarkSurfaceCard,
          unfocusedContainerColor = DarkSurfaceCard
        ),
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("library_search_input")
      )

      Spacer(modifier = Modifier.height(14.dp))
    }

    // If searching, show search results
    if (searchQuery.isNotBlank()) {
      item {
        Text(
          text = "Search Results (${filteredSearchSongs.size})",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      if (filteredSearchSongs.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No songs matching \"$searchQuery\"",
              color = Color.Gray,
              fontSize = 14.sp
            )
          }
        }
      } else {
        items(filteredSearchSongs, key = { "search_${it.id}" }) { song ->
          LibrarySongRow(
            song = song,
            onPlay = { onPlaySong(song) },
            onToggleLike = { onToggleLike(song.id) },
            onRequestDownload = { onRequestDownload(song) }
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    } else {
      // Normal Library Tabs
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = DarkSurface,
          contentColor = SpotifyGreen,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = SpotifyGreen
            )
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { onSelectTab(index) },
              text = {
                Text(
                  text = title,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == index) SpotifyGreen else Color.LightGray
                )
              }
            )
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }

      when (selectedTab) {
        0 -> {
          // Playlists Tab
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Playlists (${playlists.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )

              Button(
                onClick = onOpenCreatePlaylist,
                colors = ButtonDefaults.buttonColors(
                  containerColor = SpotifyGreen,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("new_playlist_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Playlist", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          items(playlists, key = { it.id }) { playlist ->
            val folderName = folders.firstOrNull { it.id == playlist.folderId }?.name
            PlaylistItemCard(
              playlist = playlist,
              folderName = folderName,
              onPlay = { onPlayPlaylist(playlist) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }

        1 -> {
          // Folders Tab (user request: "තමන්ට කැමති අයුරින් ෆෝල්ඩර් වර්ග කරන්නත් පහසුකම තිබිය යුතුයි")
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Custom Folders (${folders.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              )

              Button(
                onClick = onOpenCreateFolder,
                colors = ButtonDefaults.buttonColors(
                  containerColor = SpotifyGreen,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("new_folder_button")
              ) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Folder", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }

          items(folders, key = { it.id }) { folder ->
            val containedPlaylists = playlists.filter { it.folderId == folder.id }
            FolderItemCard(
              folder = folder,
              playlistCount = containedPlaylists.size,
              isSelected = selectedFolderId == folder.id,
              onClick = {
                onSelectFolderId(if (selectedFolderId == folder.id) null else folder.id)
              }
            )

            // If expanded folder, show playlists inside it
            if (selectedFolderId == folder.id) {
              Column(modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)) {
                if (containedPlaylists.isEmpty()) {
                  Text(
                    text = "No playlists assigned to this folder yet.",
                    color = Color.Gray,
                    fontSize = 12.sp
                  )
                } else {
                  containedPlaylists.forEach { pl ->
                    PlaylistItemCard(
                      playlist = pl,
                      folderName = null,
                      onPlay = { onPlayPlaylist(pl) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          }
        }

        2 -> {
          // Downloads (Offline Mode)
          val downloadedSongs = allSongs.filter { it.isDownloaded }

          item {
            // Ultra Offline Status Card
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) Color(0xFF1E281E) else Color(0xFF261D05))
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) Icons.Default.DownloadDone else Icons.Default.WorkspacePremium,
                  contentDescription = null,
                  tint = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) SpotifyGreen else UltraGold,
                  modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA) "Ultra Offline Cache Active" else "Ultra Tier Offline Caching",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  )
                  Text(
                    text = if (userProfile.subscriptionTier == SubscriptionTier.ULTRA)
                      "${downloadedSongs.size} tracks cached locally for offline listening."
                    else
                      "Downloads are exclusive to Ultra subscribers (LKR 5,000/mo).",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                  )
                }
                if (userProfile.subscriptionTier != SubscriptionTier.ULTRA) {
                  Button(
                    onClick = onUpgradeToUltra,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = UltraGold,
                      contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                  ) {
                    Text("Unlock", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "Downloaded Tracks (${downloadedSongs.size})",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          if (downloadedSongs.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(140.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No tracks downloaded yet. Tap download icon on songs to listen offline!",
                  color = Color.Gray,
                  fontSize = 13.sp
                )
              }
            }
          } else {
            items(downloadedSongs, key = { "dl_${it.id}" }) { song ->
              LibrarySongRow(
                song = song,
                onPlay = { onPlaySong(song) },
                onToggleLike = { onToggleLike(song.id) },
                onRequestDownload = { onRequestDownload(song) }
              )
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }

        3 -> {
          // Liked Songs
          val likedSongs = allSongs.filter { it.isLiked }
          item {
            Text(
              text = "Liked Songs (${likedSongs.size})",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          if (likedSongs.isEmpty()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(140.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No liked songs yet. Tap the heart icon on any song!",
                  color = Color.Gray,
                  fontSize = 13.sp
                )
              }
            }
          } else {
            items(likedSongs, key = { "liked_${it.id}" }) { song ->
              LibrarySongRow(
                song = song,
                onPlay = { onPlaySong(song) },
                onToggleLike = { onToggleLike(song.id) },
                onRequestDownload = { onRequestDownload(song) }
              )
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
fun PlaylistItemCard(
  playlist: Playlist,
  folderName: String?,
  onPlay: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .clickable(onClick = onPlay)
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(
          Brush.linearGradient(
            listOf(Color(playlist.coverColor1), Color(playlist.coverColor2))
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.QueueMusic,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = playlist.name,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Text(
        text = "${playlist.songIds.size} songs" + if (folderName != null) " • Folder: $folderName" else "",
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    IconButton(
      onClick = onPlay,
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(SpotifyGreen)
    ) {
      Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Play Playlist",
        tint = Color.Black,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun FolderItemCard(
  folder: Folder,
  playlistCount: Int,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .clickable(onClick = onClick)
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(folder.colorHex)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Folder,
        contentDescription = null,
        tint = Color.Black,
        modifier = Modifier.size(26.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = folder.name,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      )
      Text(
        text = "$playlistCount Playlists",
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
      )
    }

    Text(
      text = if (isSelected) "Collapse" else "View",
      color = SpotifyGreen,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun LibrarySongRow(
  song: Song,
  onPlay: () -> Unit,
  onToggleLike: () -> Unit,
  onRequestDownload: () -> Unit
) {
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
        .size(44.dp)
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
        text = "${song.artist} • ${song.formattedDuration}",
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

    IconButton(onClick = onRequestDownload) {
      if (song.isDownloaded) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Downloaded",
          tint = SpotifyGreen,
          modifier = Modifier.size(18.dp)
        )
      } else {
        Icon(
          imageVector = Icons.Default.DownloadDone,
          contentDescription = "Download",
          tint = Color.Gray,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
