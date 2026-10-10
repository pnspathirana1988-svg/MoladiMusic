package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.LocalizationManager
import com.example.model.SubscriptionTier
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreateFolderDialog
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.CustomerSupportDialog
import com.example.ui.components.FloatingBubblePlayer
import com.example.ui.components.FullScreenPlayerSheet
import com.example.ui.components.GooglePayBottomSheet
import com.example.ui.components.LockScreenPlayerSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.PersistentBottomPlayer
import com.example.ui.components.ProLockScreenRequiredDialog
import com.example.ui.components.UltraRequiredDialog
import com.example.ui.screens.AddAiMusicScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.YouScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SpotifyGreen

@Composable
fun MainScreen(viewModel: MusicViewModel = viewModel()) {
  val currentTab by viewModel.currentTab.collectAsState()
  val playbackState by viewModel.playbackState.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val isFullScreenOpen by viewModel.isFullScreenPlayerOpen.collectAsState()
  val commentsSong by viewModel.commentsSheetSong.collectAsState()
  val commentsMap by viewModel.commentsMap.collectAsState()
  val showGPayModal by viewModel.showGooglePayModal.collectAsState()
  val pendingUpgradeTier by viewModel.pendingUpgradeTier.collectAsState()
  val selectedCycle by viewModel.selectedBillingCycle.collectAsState()
  val showUltraDialog by viewModel.showUltraRequiredDialog.collectAsState()
  val showCreatePlaylist by viewModel.showCreatePlaylistDialog.collectAsState()
  val showCreateFolder by viewModel.showCreateFolderDialog.collectAsState()
  val showSupportDialog by viewModel.showCustomerSupportDialog.collectAsState()
  val isDarkMode by viewModel.isDarkMode.collectAsState()
  val homeGenreFilter by viewModel.homeGenreFilter.collectAsState()
  val filteredHomeSongs by viewModel.filteredHomeSongs.collectAsState()
  val allSongs by viewModel.songs.collectAsState()
  val playlists by viewModel.playlists.collectAsState()
  val folders by viewModel.folders.collectAsState()
  val libraryTab by viewModel.libraryTab.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val filteredLibrarySongs by viewModel.filteredLibrarySongs.collectAsState()
  val selectedFolderId by viewModel.selectedFolderId.collectAsState()
  val aiGenState by viewModel.aiGenState.collectAsState()
  val historyList by viewModel.historyList.collectAsState()
  val supportTickets by viewModel.supportTickets.collectAsState()

  // New Multilingual, Floating Player, and Lock Screen States
  val appLanguage by viewModel.appLanguage.collectAsState()
  val isFloatingBubbleVisible by viewModel.isFloatingBubbleVisible.collectAsState()
  val isLockScreenPlayerOpen by viewModel.isLockScreenPlayerOpen.collectAsState()
  val showProLockScreenDialog by viewModel.showProLockScreenDialog.collectAsState()
  val uploadedDjMixes by viewModel.uploadedDjMixes.collectAsState()

  val strings = LocalizationManager.getStrings(appLanguage)

  // Handle back button when full screen player is open
  BackHandler(enabled = isFullScreenOpen) {
    viewModel.closeFullScreenPlayer()
  }

  // Handle back button when lock screen player is open
  BackHandler(enabled = isLockScreenPlayerOpen) {
    viewModel.closeLockScreenPlayer()
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      containerColor = DarkBackground,
      bottomBar = {
        Column(modifier = Modifier.navigationBarsPadding()) {
          // Persistent bottom music player control component with play, pause, skip, and progress bar
          if (!isFullScreenOpen && !isLockScreenPlayerOpen) {
            PersistentBottomPlayer(
              playbackState = playbackState,
              fallbackSong = allSongs.firstOrNull(),
              onOpenFullScreen = { viewModel.openFullScreenPlayer() },
              onTogglePlayPause = { viewModel.playerManager.togglePlayPause() },
              onPlayNext = { viewModel.playerManager.playNext() },
              onPlayPrevious = { viewModel.playerManager.playPrevious() },
              onSeekTo = { posMs -> viewModel.playerManager.seekTo(posMs) },
              onToggleLike = { songId -> viewModel.toggleLike(songId) }
            )
          }

          // Horizontal 5-tab Navigation Bar with multi-language labels
          NavigationBar(
            containerColor = DarkSurface,
            contentColor = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.testTag("bottom_navigation_bar")
          ) {
            // Home
            NavigationBarItem(
              selected = currentTab == NavigationTab.HOME,
              onClick = { viewModel.setTab(NavigationTab.HOME) },
              icon = { Icon(Icons.Default.Home, contentDescription = strings.homeTab) },
              label = { Text(strings.homeTab, fontSize = 11.sp) },
              colors = navigationItemColors(),
              modifier = Modifier.testTag("tab_home")
            )

            // Library
            NavigationBarItem(
              selected = currentTab == NavigationTab.LIBRARY,
              onClick = { viewModel.setTab(NavigationTab.LIBRARY) },
              icon = { Icon(Icons.Default.LibraryMusic, contentDescription = strings.libraryTab) },
              label = { Text(strings.libraryTab, fontSize = 11.sp) },
              colors = navigationItemColors(),
              modifier = Modifier.testTag("tab_library")
            )

            // Add (AI Music Studio)
            NavigationBarItem(
              selected = currentTab == NavigationTab.ADD,
              onClick = { viewModel.setTab(NavigationTab.ADD) },
              icon = { Icon(Icons.Default.AddCircle, contentDescription = strings.addTab) },
              label = { Text(strings.addTab, fontSize = 11.sp) },
              colors = navigationItemColors(),
              modifier = Modifier.testTag("tab_add")
            )

            // History
            NavigationBarItem(
              selected = currentTab == NavigationTab.HISTORY,
              onClick = { viewModel.setTab(NavigationTab.HISTORY) },
              icon = { Icon(Icons.Default.History, contentDescription = strings.historyTab) },
              label = { Text(strings.historyTab, fontSize = 11.sp) },
              colors = navigationItemColors(),
              modifier = Modifier.testTag("tab_history")
            )

            // You
            NavigationBarItem(
              selected = currentTab == NavigationTab.YOU,
              onClick = { viewModel.setTab(NavigationTab.YOU) },
              icon = { Icon(Icons.Default.Person, contentDescription = strings.youTab) },
              label = { Text(strings.youTab, fontSize = 11.sp) },
              colors = navigationItemColors(),
              modifier = Modifier.testTag("tab_you")
            )
          }
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (currentTab) {
          NavigationTab.HOME -> {
            HomeScreen(
              songs = filteredHomeSongs,
              userProfile = userProfile,
              selectedGenreFilter = homeGenreFilter,
              onSelectGenreFilter = { viewModel.setHomeGenreFilter(it) },
              onPlaySong = { viewModel.playSong(it) },
              onToggleLike = { viewModel.toggleLike(it) },
              onRequestDownload = { viewModel.requestDownload(it) },
              onOpenComments = { viewModel.openCommentsSheet(it) },
              onUpgradeClick = { viewModel.startGooglePayCheckout(SubscriptionTier.ULTRA) }
            )
          }

          NavigationTab.LIBRARY -> {
            LibraryScreen(
              searchQuery = searchQuery,
              onSearchQueryChange = { viewModel.setSearchQuery(it) },
              selectedTab = libraryTab,
              onSelectTab = { viewModel.setLibraryTab(it) },
              filteredSearchSongs = filteredLibrarySongs,
              allSongs = allSongs,
              playlists = playlists,
              folders = folders,
              selectedFolderId = selectedFolderId,
              onSelectFolderId = { viewModel.setSelectedFolderId(it) },
              userProfile = userProfile,
              onPlaySong = { viewModel.playSong(it) },
              onPlayPlaylist = { viewModel.playPlaylist(it) },
              onToggleLike = { viewModel.toggleLike(it) },
              onRequestDownload = { viewModel.requestDownload(it) },
              onOpenCreatePlaylist = { viewModel.openCreatePlaylistDialog() },
              onOpenCreateFolder = { viewModel.openCreateFolderDialog() },
              onUpgradeToUltra = { viewModel.startGooglePayCheckout(SubscriptionTier.ULTRA) },
              appLanguage = appLanguage
            )
          }

          NavigationTab.ADD -> {
            AddAiMusicScreen(
              aiGenState = aiGenState,
              onGenerateMusic = { prompt, genre, mood, engine, lyrics ->
                viewModel.generateAiMusic(prompt, genre, mood, engine, lyrics)
              },
              onPlaySong = { viewModel.playSong(it) }
            )
          }

          NavigationTab.HISTORY -> {
            HistoryScreen(
              historyItems = historyList,
              aiRecommendations = viewModel.getAiRecommendations(),
              onPlaySong = { viewModel.playSong(it) },
              onToggleLike = { viewModel.toggleLike(it) }
            )
          }

          NavigationTab.YOU -> {
            YouScreen(
              userProfile = userProfile,
              appLanguage = appLanguage,
              onSelectLanguage = { viewModel.setLanguage(it) },
              isFloatingBubbleEnabled = isFloatingBubbleVisible,
              onToggleFloatingBubble = { viewModel.setFloatingBubbleVisible(it) },
              onOpenLockScreenPlayer = { viewModel.openLockScreenPlayer() },
              uploadedDjMixes = uploadedDjMixes,
              onPlaySong = { viewModel.playSong(it) },
              onToggleLike = { viewModel.toggleLike(it) },
              isDarkMode = isDarkMode,
              onToggleDarkMode = { viewModel.toggleDarkMode() },
              onToggleSignIn = { viewModel.toggleSignIn() },
              onStartGooglePayCheckout = { viewModel.startGooglePayCheckout(it) },
              onToggleOfflineOnly = { viewModel.toggleOfflineOnlyMode(it) },
              onOpenCustomerSupport = { viewModel.openCustomerSupportDialog() }
            )
          }
        }
      }
    }

    // Draggable Floating Pop-up Bubble Player (anywhere on phone)
    if (isFloatingBubbleVisible && !isFullScreenOpen && !isLockScreenPlayerOpen) {
      FloatingBubblePlayer(
        playbackState = playbackState,
        onOpenApp = { viewModel.openFullScreenPlayer() },
        onTogglePlayPause = { viewModel.playerManager.togglePlayPause() },
        onPlayNext = { viewModel.playerManager.playNext() },
        onDismiss = { viewModel.setFloatingBubbleVisible(false) }
      )
    }

    // Full Screen Music Player
    AnimatedVisibility(
      visible = isFullScreenOpen,
      enter = slideInVertically(initialOffsetY = { it }),
      exit = slideOutVertically(targetOffsetY = { it })
    ) {
      FullScreenPlayerSheet(
        playbackState = playbackState,
        onCollapse = { viewModel.closeFullScreenPlayer() },
        onTogglePlayPause = { viewModel.playerManager.togglePlayPause() },
        onPlayNext = { viewModel.playerManager.playNext() },
        onPlayPrevious = { viewModel.playerManager.playPrevious() },
        onRewind10 = { viewModel.playerManager.rewind10s() },
        onForward10 = { viewModel.playerManager.skipForward10s() },
        onSeekTo = { viewModel.playerManager.seekTo(it) },
        onToggleShuffle = { viewModel.playerManager.toggleShuffle() },
        onCycleRepeat = { viewModel.playerManager.cycleRepeatMode() },
        onToggleLike = { viewModel.toggleLike(it) },
        onRequestDownload = { viewModel.requestDownload(it) },
        onOpenComments = { viewModel.openCommentsSheet(it) }
      )
    }

    // Pro-Exclusive Lock Screen Player Ambient Overlay
    AnimatedVisibility(
      visible = isLockScreenPlayerOpen,
      enter = slideInVertically(initialOffsetY = { -it }),
      exit = slideOutVertically(targetOffsetY = { -it })
    ) {
      LockScreenPlayerSheet(
        playbackState = playbackState,
        appLanguage = appLanguage,
        onUnlockScreen = { viewModel.closeLockScreenPlayer() },
        onTogglePlayPause = { viewModel.playerManager.togglePlayPause() },
        onPlayNext = { viewModel.playerManager.playNext() },
        onPlayPrevious = { viewModel.playerManager.playPrevious() },
        onSeekTo = { posMs -> viewModel.playerManager.seekTo(posMs) }
      )
    }
  }

  // Comments Bottom Sheet
  commentsSong?.let { song ->
    val songComments = commentsMap[song.id] ?: emptyList()
    CommentsBottomSheet(
      song = song,
      comments = songComments,
      onDismiss = { viewModel.closeCommentsSheet() },
      onAddComment = { text -> viewModel.addComment(song.id, text) }
    )
  }

  // Google Pay Bottom Sheet
  if (showGPayModal) {
    GooglePayBottomSheet(
      tier = pendingUpgradeTier,
      selectedCycle = selectedCycle,
      onCycleSelected = { viewModel.setBillingCycle(it) },
      onDismiss = { viewModel.dismissGooglePayModal() },
      onPaymentSuccess = { viewModel.confirmGooglePayment() }
    )
  }

  // Ultra Required Dialog
  if (showUltraDialog) {
    UltraRequiredDialog(
      onDismiss = { viewModel.dismissUltraDialog() },
      onUpgradeToUltra = { viewModel.startGooglePayCheckout(SubscriptionTier.ULTRA) }
    )
  }

  // Pro Lock Screen Required Dialog
  if (showProLockScreenDialog) {
    ProLockScreenRequiredDialog(
      appLanguage = appLanguage,
      onDismiss = { viewModel.dismissProLockScreenDialog() },
      onUpgradeToPro = { viewModel.startGooglePayCheckout(SubscriptionTier.PRO) }
    )
  }

  // Create Playlist Dialog
  if (showCreatePlaylist) {
    CreatePlaylistDialog(
      folders = folders,
      onDismiss = { viewModel.dismissCreatePlaylistDialog() },
      onCreatePlaylist = { name, desc, fId ->
        viewModel.createPlaylist(name, desc, fId)
      }
    )
  }

  // Create Folder Dialog
  if (showCreateFolder) {
    CreateFolderDialog(
      onDismiss = { viewModel.dismissCreateFolderDialog() },
      onCreateFolder = { name, colorHex ->
        viewModel.createFolder(name, colorHex)
      }
    )
  }

  // Customer Support Dialog
  if (showSupportDialog) {
    CustomerSupportDialog(
      tickets = supportTickets,
      onDismiss = { viewModel.dismissCustomerSupportDialog() },
      onSubmitTicket = { subject, msg ->
        viewModel.submitSupportTicket(subject, msg)
      }
    )
  }
}

@Composable
fun navigationItemColors() = NavigationBarItemDefaults.colors(
  selectedIconColor = SpotifyGreen,
  selectedTextColor = SpotifyGreen,
  unselectedIconColor = Color.LightGray,
  unselectedTextColor = Color.LightGray,
  indicatorColor = Color(0x331DB954)
)
