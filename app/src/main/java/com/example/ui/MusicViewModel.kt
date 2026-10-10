package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HistoryItem
import com.example.data.MusicRepository
import com.example.data.SupportTicket
import com.example.model.AiEngine
import com.example.model.AiRecommendation
import com.example.model.AppLanguage
import com.example.model.BillingCycle
import com.example.model.Comment
import com.example.model.Folder
import com.example.model.Playlist
import com.example.model.Song
import com.example.model.SubscriptionTier
import com.example.model.UserProfile
import com.example.player.AudioPlayerManager
import com.example.player.PlaybackState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val label: String, val iconName: String) {
  HOME("Home", "home"),
  LIBRARY("Library", "library_music"),
  ADD("Add", "add_circle"),
  HISTORY("History", "history"),
  YOU("You", "person")
}

data class AiGenerationUiState(
  val isGenerating: Boolean = false,
  val progress: Float = 0f,
  val statusMessage: String = "",
  val lastGeneratedSong: Song? = null
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = MusicRepository()
  val playerManager = AudioPlayerManager(application.applicationContext)

  // Current active navigation tab
  private val _currentTab = MutableStateFlow(NavigationTab.HOME)
  val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

  // Full Screen Player state
  private val _isFullScreenPlayerOpen = MutableStateFlow(false)
  val isFullScreenPlayerOpen: StateFlow<Boolean> = _isFullScreenPlayerOpen.asStateFlow()

  // Comments Sheet state
  private val _commentsSheetSong = MutableStateFlow<Song?>(null)
  val commentsSheetSong: StateFlow<Song?> = _commentsSheetSong.asStateFlow()

  // Google Pay Modal state
  private val _showGooglePayModal = MutableStateFlow(false)
  val showGooglePayModal: StateFlow<Boolean> = _showGooglePayModal.asStateFlow()

  private val _pendingUpgradeTier = MutableStateFlow(SubscriptionTier.ULTRA)
  val pendingUpgradeTier: StateFlow<SubscriptionTier> = _pendingUpgradeTier.asStateFlow()

  private val _selectedBillingCycle = MutableStateFlow(BillingCycle.MONTHLY)
  val selectedBillingCycle: StateFlow<BillingCycle> = _selectedBillingCycle.asStateFlow()

  // Ultra Download Gated Dialog
  private val _showUltraRequiredDialog = MutableStateFlow(false)
  val showUltraRequiredDialog: StateFlow<Boolean> = _showUltraRequiredDialog.asStateFlow()

  // Modals for Playlist / Folder / Support
  private val _showCreatePlaylistDialog = MutableStateFlow(false)
  val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

  private val _showCreateFolderDialog = MutableStateFlow(false)
  val showCreateFolderDialog: StateFlow<Boolean> = _showCreateFolderDialog.asStateFlow()

  private val _showCustomerSupportDialog = MutableStateFlow(false)
  val showCustomerSupportDialog: StateFlow<Boolean> = _showCustomerSupportDialog.asStateFlow()

  // Dark mode setting
  private val _isDarkMode = MutableStateFlow(true)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  // Library state
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _libraryTab = MutableStateFlow(0) // 0: Playlists, 1: Folders, 2: Downloads, 3: Liked
  val libraryTab: StateFlow<Int> = _libraryTab.asStateFlow()

  private val _selectedFolderId = MutableStateFlow<String?>(null)
  val selectedFolderId: StateFlow<String?> = _selectedFolderId.asStateFlow()

  // Home Screen Filter Chip
  private val _homeGenreFilter = MutableStateFlow("All")
  val homeGenreFilter: StateFlow<String> = _homeGenreFilter.asStateFlow()

  // AI Generation State
  private val _aiGenState = MutableStateFlow(AiGenerationUiState())
  val aiGenState: StateFlow<AiGenerationUiState> = _aiGenState.asStateFlow()

  // Repository Flows
  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val songs: StateFlow<List<Song>> = repository.songs
  val playlists: StateFlow<List<Playlist>> = repository.playlists
  val folders: StateFlow<List<Folder>> = repository.folders
  val commentsMap: StateFlow<Map<String, List<Comment>>> = repository.comments
  val historyList: StateFlow<List<HistoryItem>> = repository.history
  val supportTickets: StateFlow<List<SupportTicket>> = repository.supportTickets
  val playbackState: StateFlow<PlaybackState> = playerManager.playbackState

  // Language State
  val appLanguage: StateFlow<AppLanguage> = repository.currentLanguage

  fun setLanguage(language: AppLanguage) {
    repository.setLanguage(language)
  }

  // Floating Pop-up Bubble Player
  private val _isFloatingBubbleVisible = MutableStateFlow(false)
  val isFloatingBubbleVisible: StateFlow<Boolean> = _isFloatingBubbleVisible.asStateFlow()

  fun setFloatingBubbleVisible(visible: Boolean) {
    _isFloatingBubbleVisible.value = visible
  }

  // Lock Screen Player State
  private val _isLockScreenPlayerOpen = MutableStateFlow(false)
  val isLockScreenPlayerOpen: StateFlow<Boolean> = _isLockScreenPlayerOpen.asStateFlow()

  private val _showProLockScreenDialog = MutableStateFlow(false)
  val showProLockScreenDialog: StateFlow<Boolean> = _showProLockScreenDialog.asStateFlow()

  fun openLockScreenPlayer() {
    val tier = userProfile.value.subscriptionTier
    if (tier == SubscriptionTier.PRO || tier == SubscriptionTier.ULTRA) {
      _isLockScreenPlayerOpen.value = true
    } else {
      _showProLockScreenDialog.value = true
    }
  }

  fun closeLockScreenPlayer() {
    _isLockScreenPlayerOpen.value = false
  }

  fun dismissProLockScreenDialog() {
    _showProLockScreenDialog.value = false
  }

  // Uploaded DJ Maluka Tracks
  val uploadedDjMixes: StateFlow<List<Song>> = songs.map { list ->
    list.filter { it.artist == "DJ Maluka" }
  }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  // Filtered Songs in Home
  val filteredHomeSongs: StateFlow<List<Song>> = combine(songs, homeGenreFilter) { list, filter ->
    if (filter == "All") list else list.filter { it.genre.contains(filter, ignoreCase = true) }
  }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  // Filtered Library Songs
  val filteredLibrarySongs: StateFlow<List<Song>> = combine(songs, searchQuery) { list, query ->
    if (query.isBlank()) list else list.filter {
      it.title.contains(query, ignoreCase = true) ||
      it.artist.contains(query, ignoreCase = true) ||
      it.genre.contains(query, ignoreCase = true)
    }
  }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  init {
    playerManager.onSongPlayed = { song ->
      repository.recordHistory(song)
    }
    // Set initial queue
    playerManager.setQueue(repository.songs.value, 0)
    // Keep paused initially
    playerManager.pause()
  }

  fun setTab(tab: NavigationTab) {
    _currentTab.value = tab
  }

  fun setLibraryTab(index: Int) {
    _libraryTab.value = index
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setHomeGenreFilter(filter: String) {
    _homeGenreFilter.value = filter
  }

  fun setSelectedFolderId(folderId: String?) {
    _selectedFolderId.value = folderId
  }

  fun openFullScreenPlayer() {
    _isFullScreenPlayerOpen.value = true
  }

  fun closeFullScreenPlayer() {
    _isFullScreenPlayerOpen.value = false
  }

  fun openCommentsSheet(song: Song) {
    _commentsSheetSong.value = song
  }

  fun closeCommentsSheet() {
    _commentsSheetSong.value = null
  }

  fun toggleDarkMode() {
    _isDarkMode.value = !_isDarkMode.value
  }

  fun toggleSignIn() {
    repository.toggleSignIn()
  }

  fun playSong(song: Song) {
    val currentQueue = songs.value
    val idx = currentQueue.indexOfFirst { it.id == song.id }
    if (idx != -1) {
      playerManager.setQueue(currentQueue, idx)
    } else {
      playerManager.playSong(song)
    }
  }

  fun playPlaylist(playlist: Playlist) {
    val all = songs.value
    val playlistSongs = all.filter { playlist.songIds.contains(it.id) }
    if (playlistSongs.isNotEmpty()) {
      playerManager.setQueue(playlistSongs, 0)
    }
  }

  fun toggleLike(songId: String) {
    repository.toggleLikeSong(songId)
  }

  fun requestDownload(song: Song) {
    if (userProfile.value.subscriptionTier != SubscriptionTier.ULTRA) {
      _showUltraRequiredDialog.value = true
    } else {
      val success = repository.downloadSong(song.id)
      if (!success) {
        _showUltraRequiredDialog.value = true
      }
    }
  }

  fun removeDownload(songId: String) {
    repository.removeDownload(songId)
  }

  fun dismissUltraDialog() {
    _showUltraRequiredDialog.value = false
  }

  fun startGooglePayCheckout(tier: SubscriptionTier) {
    _pendingUpgradeTier.value = tier
    _showUltraRequiredDialog.value = false
    _showGooglePayModal.value = true
  }

  fun dismissGooglePayModal() {
    _showGooglePayModal.value = false
  }

  fun setBillingCycle(cycle: BillingCycle) {
    _selectedBillingCycle.value = cycle
  }

  fun confirmGooglePayment() {
    repository.updateSubscription(_pendingUpgradeTier.value, _selectedBillingCycle.value)
    _showGooglePayModal.value = false
  }

  fun addComment(songId: String, text: String) {
    repository.addComment(songId, text)
  }

  fun createPlaylist(name: String, description: String, folderId: String? = null) {
    repository.createPlaylist(name, description, folderId)
    _showCreatePlaylistDialog.value = false
  }

  fun createFolder(name: String, colorHex: Long) {
    repository.createFolder(name, colorHex)
    _showCreateFolderDialog.value = false
  }

  fun assignPlaylistToFolder(playlistId: String, folderId: String?) {
    repository.assignPlaylistToFolder(playlistId, folderId)
  }

  fun openCreatePlaylistDialog() {
    _showCreatePlaylistDialog.value = true
  }

  fun dismissCreatePlaylistDialog() {
    _showCreatePlaylistDialog.value = false
  }

  fun openCreateFolderDialog() {
    _showCreateFolderDialog.value = true
  }

  fun dismissCreateFolderDialog() {
    _showCreateFolderDialog.value = false
  }

  fun openCustomerSupportDialog() {
    _showCustomerSupportDialog.value = true
  }

  fun dismissCustomerSupportDialog() {
    _showCustomerSupportDialog.value = false
  }

  fun submitSupportTicket(subject: String, message: String) {
    repository.submitSupportTicket(subject, message)
    _showCustomerSupportDialog.value = false
  }

  fun toggleOfflineOnlyMode(enabled: Boolean) {
    repository.setOfflineModeOnly(enabled)
  }

  fun getAiRecommendations(): List<AiRecommendation> {
    return repository.getAiRecommendations()
  }

  fun generateAiMusic(
    prompt: String,
    genre: String,
    mood: String,
    engine: AiEngine,
    customLyrics: String
  ) {
    viewModelScope.launch {
      _aiGenState.value = AiGenerationUiState(
        isGenerating = true,
        progress = 0.1f,
        statusMessage = "Initializing ${engine.title} Neural Models..."
      )

      delay(900)
      _aiGenState.value = _aiGenState.value.copy(
        progress = 0.35f,
        statusMessage = "Analyzing prompt: \"$prompt\" [Genre: $genre, Mood: $mood]..."
      )

      delay(1100)
      _aiGenState.value = _aiGenState.value.copy(
        progress = 0.70f,
        statusMessage = "Synthesizing harmonic waveforms, 808 sub-bass & acoustic timbre..."
      )

      delay(1000)
      _aiGenState.value = _aiGenState.value.copy(
        progress = 0.95f,
        statusMessage = "Mastering 24-bit 48kHz audio stream via ${engine.title}..."
      )

      delay(600)
      val title = if (prompt.length > 25) prompt.take(25) + "..." else prompt.ifBlank { "$genre $mood Fusion" }
      val lyrics = customLyrics.ifBlank {
        "[$genre // $mood]\nSynthesized by ${engine.title}\nEchoes in the dark, rhythm takes control\nVibing to the beat that heals the soul!"
      }

      val newSong = repository.addAiGeneratedSong(
        title = title,
        genre = genre,
        mood = mood,
        engine = engine,
        lyrics = lyrics,
        prompt = prompt
      )

      _aiGenState.value = AiGenerationUiState(
        isGenerating = false,
        progress = 1.0f,
        statusMessage = "Success! Track generated and ready.",
        lastGeneratedSong = newSong
      )

      // Auto play the generated song
      playSong(newSong)
    }
  }

  override fun onCleared() {
    super.onCleared()
    playerManager.release()
  }
}
