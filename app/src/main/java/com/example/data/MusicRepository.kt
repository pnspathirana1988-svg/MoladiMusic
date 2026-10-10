package com.example.data

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class HistoryItem(
  val id: String,
  val song: Song,
  val timestampText: String,
  val playedAtMillis: Long
)

data class SupportTicket(
  val id: String,
  val subject: String,
  val message: String,
  val status: String,
  val createdAt: String
)

class MusicRepository {

  // Current User Profile State
  private val _userProfile = MutableStateFlow(
    UserProfile(
      id = "user_001",
      name = "Pradeep Pathirana",
      email = "pnspathirana1988@gmail.com",
      isSignedIn = true,
      subscriptionTier = SubscriptionTier.FREE,
      billingCycle = BillingCycle.MONTHLY,
      isOfflineModeOnly = false
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  // Master Songs List
  private val _songs = MutableStateFlow<List<Song>>(createInitialSongs())
  val songs: StateFlow<List<Song>> = _songs.asStateFlow()

  // Folders
  private val _folders = MutableStateFlow<List<Folder>>(createInitialFolders())
  val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

  // Playlists
  private val _playlists = MutableStateFlow<List<Playlist>>(createInitialPlaylists())
  val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

  // Comments keyed by SongId
  private val _comments = MutableStateFlow<Map<String, List<Comment>>>(createInitialComments())
  val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

  // Listening History
  private val _history = MutableStateFlow<List<HistoryItem>>(createInitialHistory())
  val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

  // Support Tickets
  private val _supportTickets = MutableStateFlow<List<SupportTicket>>(createInitialTickets())
  val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

  // Multi-Language State (English, Spanish, Chinese, Sinhala)
  private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  fun setLanguage(language: AppLanguage) {
    _currentLanguage.value = language
  }

  fun toggleLikeSong(songId: String) {
    val currentSongs = _songs.value.toMutableList()
    val index = currentSongs.indexOfFirst { it.id == songId }
    if (index != -1) {
      val song = currentSongs[index]
      val newIsLiked = !song.isLiked
      val newLikesCount = if (newIsLiked) song.likesCount + 1 else (song.likesCount - 1).coerceAtLeast(0)
      currentSongs[index] = song.copy(
        isLiked = newIsLiked,
        likesCount = newLikesCount
      )
      _songs.value = currentSongs
    }
  }

  fun downloadSong(songId: String): Boolean {
    // Ultra tier is strictly required for offline downloads
    if (_userProfile.value.subscriptionTier != SubscriptionTier.ULTRA) {
      return false
    }

    val currentSongs = _songs.value.toMutableList()
    val index = currentSongs.indexOfFirst { it.id == songId }
    if (index != -1) {
      val song = currentSongs[index]
      currentSongs[index] = song.copy(
        isDownloaded = true,
        isOfflineCached = true
      )
      _songs.value = currentSongs
      return true
    }
    return false
  }

  fun removeDownload(songId: String) {
    val currentSongs = _songs.value.toMutableList()
    val index = currentSongs.indexOfFirst { it.id == songId }
    if (index != -1) {
      val song = currentSongs[index]
      currentSongs[index] = song.copy(
        isDownloaded = false,
        isOfflineCached = false
      )
      _songs.value = currentSongs
    }
  }

  fun addComment(songId: String, text: String) {
    if (text.isBlank()) return
    val user = _userProfile.value
    val newComment = Comment(
      id = UUID.randomUUID().toString(),
      songId = songId,
      authorName = user.name,
      authorAvatarLetter = user.name.firstOrNull()?.uppercase() ?: "U",
      text = text.trim(),
      timestamp = "Just now",
      likes = 0
    )

    val currentMap = _comments.value.toMutableMap()
    val list = currentMap[songId]?.toMutableList() ?: mutableListOf()
    list.add(0, newComment)
    currentMap[songId] = list
    _comments.value = currentMap

    // Update comments count on song
    val songList = _songs.value.toMutableList()
    val sIndex = songList.indexOfFirst { it.id == songId }
    if (sIndex != -1) {
      songList[sIndex] = songList[sIndex].copy(commentsCount = songList[sIndex].commentsCount + 1)
      _songs.value = songList
    }
  }

  fun recordHistory(song: Song) {
    val currentHistory = _history.value.toMutableList()
    val newItem = HistoryItem(
      id = UUID.randomUUID().toString(),
      song = song,
      timestampText = "Just now",
      playedAtMillis = System.currentTimeMillis()
    )
    currentHistory.add(0, newItem)
    // keep max 50 items
    _history.value = currentHistory.take(50)
  }

  fun createPlaylist(name: String, description: String, folderId: String? = null): Playlist {
    val colors = listOf(
      0xFF1DB954L to 0xFF191414L,
      0xFFA855F7L to 0xFF3B82F6L,
      0xFFFF5722L to 0xFFFF9800L,
      0xFF00E5FFL to 0xFF1E88E5L,
      0xFFFFD700L to 0xFFE91E63L
    ).random()

    val newPlaylist = Playlist(
      id = "pl_${System.currentTimeMillis()}",
      name = name.ifBlank { "My New Playlist" },
      description = description,
      folderId = folderId,
      songIds = emptyList(),
      coverColor1 = colors.first,
      coverColor2 = colors.second,
      isCustom = true
    )
    _playlists.value = listOf(newPlaylist) + _playlists.value
    return newPlaylist
  }

  fun addSongToPlaylist(playlistId: String, songId: String) {
    val list = _playlists.value.toMutableList()
    val idx = list.indexOfFirst { it.id == playlistId }
    if (idx != -1) {
      val pl = list[idx]
      if (!pl.songIds.contains(songId)) {
        list[idx] = pl.copy(songIds = pl.songIds + songId)
        _playlists.value = list
      }
    }
  }

  fun createFolder(name: String, colorHex: Long = 0xFF1DB954L): Folder {
    val newFolder = Folder(
      id = "fld_${System.currentTimeMillis()}",
      name = name.ifBlank { "New Folder" },
      colorHex = colorHex
    )
    _folders.value = _folders.value + newFolder
    return newFolder
  }

  fun assignPlaylistToFolder(playlistId: String, folderId: String?) {
    val list = _playlists.value.toMutableList()
    val idx = list.indexOfFirst { it.id == playlistId }
    if (idx != -1) {
      list[idx] = list[idx].copy(folderId = folderId)
      _playlists.value = list
    }
  }

  fun addAiGeneratedSong(
    title: String,
    genre: String,
    mood: String,
    engine: AiEngine,
    lyrics: String,
    prompt: String
  ): Song {
    val colors = when (engine) {
      AiEngine.GOOGLE_LYRIA -> 0xFF00E5FFL to 0xFF1DB954L
      AiEngine.SUNO_AI -> 0xFFA855F7L to 0xFFFF3366L
    }

    val newSong = Song(
      id = "ai_${System.currentTimeMillis()}",
      title = title.ifBlank { "AI Composition #%d".format((100..999).random()) },
      artist = "${engine.title} Studio",
      album = "AI Generated Soundtracks",
      durationSeconds = 195,
      streamUrl = "https://actions.google.com/sounds/v1/science_fiction/scifi_synthesizer_melody.ogg",
      coverColor1 = colors.first,
      coverColor2 = colors.second,
      genre = genre,
      likesCount = 1,
      isLiked = true,
      commentsCount = 1,
      isDownloaded = _userProfile.value.subscriptionTier == SubscriptionTier.ULTRA,
      isOfflineCached = _userProfile.value.subscriptionTier == SubscriptionTier.ULTRA,
      isAiGenerated = true,
      aiEngine = engine.title,
      lyrics = lyrics.ifBlank { "[$genre // $mood]\n(Synthesized by ${engine.title})\nBeat drops with harmonic resonance..." }
    )

    _songs.value = listOf(newSong) + _songs.value
    // Add default praise comment
    addComment(newSong.id, "Generated with ${engine.title}: \"$prompt\". Sounds breathtaking!")
    return newSong
  }

  fun updateSubscription(tier: SubscriptionTier, billingCycle: BillingCycle) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val nextYearOrMonth = System.currentTimeMillis() + if (billingCycle == BillingCycle.YEARLY) 365L * 24 * 3600 * 1000 else 30L * 24 * 3600 * 1000
    val expiryDate = sdf.format(Date(nextYearOrMonth))

    _userProfile.value = _userProfile.value.copy(
      subscriptionTier = tier,
      billingCycle = billingCycle,
      subscriptionActiveUntil = expiryDate
    )
  }

  fun setOfflineModeOnly(enabled: Boolean) {
    _userProfile.value = _userProfile.value.copy(isOfflineModeOnly = enabled)
  }

  fun toggleSignIn() {
    val current = _userProfile.value
    _userProfile.value = current.copy(
      isSignedIn = !current.isSignedIn,
      name = if (!current.isSignedIn) "Pradeep Pathirana" else "Guest Listener",
      email = if (!current.isSignedIn) "pnspathirana1988@gmail.com" else "guest@vibestream.io"
    )
  }

  fun submitSupportTicket(subject: String, message: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val ticket = SupportTicket(
      id = "TICKET-#%d".format((1000..9999).random()),
      subject = subject,
      message = message,
      status = "In Review (24/7 Support)",
      createdAt = sdf.format(Date())
    )
    _supportTickets.value = listOf(ticket) + _supportTickets.value
  }

  fun getAiRecommendations(): List<AiRecommendation> {
    val allSongs = _songs.value
    return listOf(
      AiRecommendation(
        song = allSongs.firstOrNull { it.id == "s1" } ?: allSongs[0],
        matchPercentage = 99,
        reason = "Matched from your frequent Sinhala Baila listening patterns."
      ),
      AiRecommendation(
        song = allSongs.firstOrNull { it.id == "s2" } ?: allSongs[1],
        matchPercentage = 95,
        reason = "Top trending viral track in Sri Lanka this week."
      ),
      AiRecommendation(
        song = allSongs.firstOrNull { it.id == "s5" } ?: allSongs[2],
        matchPercentage = 91,
        reason = "Google Lyria AI detected high acoustic resonance affinity."
      ),
      AiRecommendation(
        song = allSongs.firstOrNull { it.id == "s3" } ?: allSongs[3],
        matchPercentage = 88,
        reason = "Recommended for your nighttime chill sessions."
      )
    )
  }

  // --- Initial Data Seeds ---
  private fun createInitialSongs(): List<Song> {
    return listOf(
      Song(
        id = "s1",
        title = "Baila Nonstop Live Mashup",
        artist = "Ceylon Sunshine Band",
        album = "Baila Vibes Colombo",
        durationSeconds = 245,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        coverColor1 = 0xFF1DB954L,
        coverColor2 = 0xFFFF9800L,
        genre = "Sinhala Baila",
        likesCount = 1420,
        isLiked = true,
        commentsCount = 28,
        isDownloaded = true,
        isOfflineCached = true,
        lyrics = "සුන්දර රැයක පාරාදීසේ නැටුම් නටමු රෑ පුරා...\nබයිලා තාලෙට පාද තබාලා සතුටින් උතුරයි සිත් අපගේ!\n(Chorus)\nලංකා දූපතෙ නාදය ඇසේවා\nසංගීතයෙ රිද්මය ගලා යා!"
      ),
      Song(
        id = "s2",
        title = "Manike Mage Hithe (Acoustic Remix)",
        artist = "Yohani & Chamath",
        album = "Colombo Acoustic Sessions",
        durationSeconds = 208,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        coverColor1 = 0xFFE91E63L,
        coverColor2 = 0xFF9C27B0L,
        genre = "Acoustic Pop",
        likesCount = 3890,
        isLiked = true,
        commentsCount = 64,
        isDownloaded = false,
        lyrics = "මැණිකේ මගේ හිතේ මුදුවේ නුරා හැඟුම්ියාවී\nරුව නාරී මනහාරී සුකුමාලී නුඹ තමා...\nහිත ඇතුළෙම පෙම් කවි ලියවෙනවා\nනුඹ ළඟ ඉන්නා හැම මොහොතකදීම!"
      ),
      Song(
        id = "s3",
        title = "Cyberpunk Overdrive 2026",
        artist = "Neon Phonk Syndicate",
        album = "Future Tokyo Night",
        durationSeconds = 178,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        coverColor1 = 0xFF00E5FFL,
        coverColor2 = 0xFF7C4DFFL,
        genre = "Cyberpunk Phonk",
        likesCount = 840,
        isLiked = false,
        commentsCount = 14,
        isDownloaded = false,
        lyrics = "[Instrumental Phonk / Heavy 808 Bass]\nFast acceleration on the highway...\nNeon reflections across the visor..."
      ),
      Song(
        id = "s4",
        title = "Sandawatha Ahasa Pura",
        artist = "Kasun Kalhara & Nadini",
        album = "Romantic Classics",
        durationSeconds = 265,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        coverColor1 = 0xFF3F51B5L,
        coverColor2 = 0xFF00BCD4L,
        genre = "Sinhala Classics",
        likesCount = 2100,
        isLiked = true,
        commentsCount = 42,
        isDownloaded = true,
        isOfflineCached = true,
        lyrics = "සඳවට අහස පුරා තරු පිරිලා බබළනවා...\nනුඹෙ නෙත් සඟලේ ආදර බැල්මෙන් මුළු ලොව පිබිදෙනවා!\nසදාකාලිකයි අපේ මේ ආදර කතාව..."
      ),
      Song(
        id = "s5",
        title = "Midnight Lofi Rainy Cafe",
        artist = "ChilledCow Studio",
        album = "Rainy Colombo Beats",
        durationSeconds = 192,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        coverColor1 = 0xFF4CAF50L,
        coverColor2 = 0xFF2E7D32L,
        genre = "Lo-Fi Chill",
        likesCount = 950,
        isLiked = false,
        commentsCount = 9,
        isDownloaded = false,
        lyrics = "(Cozy rain sounds on window glass)\n(Gentle jazz piano chords)\nStudy, relax, breathe in deep..."
      ),
      Song(
        id = "s6",
        title = "Neon Nights (VIP Festival Mix)",
        artist = "Cyberphonk x Lyria AI",
        album = "Festival Live Soundwaves",
        durationSeconds = 212,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        coverColor1 = 0xFFFFD700L,
        coverColor2 = 0xFFFF5722L,
        genre = "EDM Festival",
        likesCount = 4320,
        isLiked = true,
        commentsCount = 89,
        isDownloaded = false,
        lyrics = "3, 2, 1, Drop the Bass!\nElectric energy pulsating through the crowd...\nHands up in the neon sky!"
      ),
      Song(
        id = "s7",
        title = "Rosa Malak Wage",
        artist = "Sanuka & Wayo",
        album = "Island Rhythms",
        durationSeconds = 230,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        coverColor1 = 0xFFE91E63L,
        coverColor2 = 0xFFFF80ABL,
        genre = "Sinhala Pop",
        likesCount = 1840,
        isLiked = false,
        commentsCount = 19,
        isDownloaded = false,
        lyrics = "රෝස මලක් වගේ ලස්සන ඔබේ වත දකින විට...\nමගෙ හිතේ පිරෙන්නේ සතුටයි ආදරෙයි!\nකවදාවත් නොමැකෙන සුන්දර මතක..."
      ),
      Song(
        id = "s8",
        title = "Deep Space Lyria Meditation",
        artist = "Google Lyria Audio Lab",
        album = "Harmonic Dimensions",
        durationSeconds = 300,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        coverColor1 = 0xFF7C4DFFL,
        coverColor2 = 0xFF18FFFFL,
        genre = "Ambient AI",
        likesCount = 670,
        isLiked = false,
        commentsCount = 5,
        isDownloaded = false,
        isAiGenerated = true,
        aiEngine = "Google Lyria",
        lyrics = "[Synthesized 432Hz Healing Frequency]\nDeep neural resonances for relaxation and deep sleep."
      ),
      // --- Uploaded DJ Maluka Tracks (User Mixes) ---
      Song(
        id = "dj_1",
        title = "Adala Hina Wela (Party Baila Mix)",
        artist = "DJ Maluka",
        album = "Uploaded Mixes - DJ Maluka",
        durationSeconds = 154,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
        coverColor1 = 0xFFFF007FL,
        coverColor2 = 0xFF00E5FFL,
        genre = "Sinhala Baila Party",
        likesCount = 5240,
        isLiked = true,
        commentsCount = 112,
        isDownloaded = true,
        isOfflineCached = true,
        lyrics = "අඬලා හිනාවෙලා යමු අපි ජෝඩුවෙලා...\nදඟලාලා හිනාවෙලා යමු අපි යාළුවෙලා...\nඔබ මා වශී කළා...\nමට ඔයයි ඕනේ ඔය හදවතයි ඕනේ!\nDJ Maluka Nonstop Party Baila Live!"
      ),
      Song(
        id = "dj_2",
        title = "Rathu Pata Pushparaga (Dance Remix)",
        artist = "DJ Maluka",
        album = "Uploaded Mixes - DJ Maluka",
        durationSeconds = 147,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
        coverColor1 = 0xFFFF3366L,
        coverColor2 = 0xFFFFD700L,
        genre = "Sinhala Remix",
        likesCount = 4690,
        isLiked = true,
        commentsCount = 87,
        isDownloaded = true,
        isOfflineCached = true,
        lyrics = "රතු පාට පුෂ්පරාගෙ පොට්ටු තිවුනා...\nපැත්තෙ ගාට තෙත්තු රෝසි කිට්ටු මා වුනා...\nසුන්දර වූ රෑ සුන්දර වූ ආලේ..."
      ),
      // --- YouTube & Spotify Multi-Million Global Catalog ---
      Song(
        id = "sp_1",
        title = "Espresso (Spotify Global #1)",
        artist = "Sabrina Carpenter",
        album = "Spotify Top 50 Hits",
        durationSeconds = 175,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
        coverColor1 = 0xFF1DB954L,
        coverColor2 = 0xFF0D47A1L,
        genre = "Spotify Global Pop",
        likesCount = 12900,
        isLiked = false,
        commentsCount = 230,
        isDownloaded = false,
        lyrics = "Now he's thinkin' 'bout me every night, oh\nIs it that sweet? I guess so\nSay you can't sleep, baby, I know\nThat's that me, espresso!"
      ),
      Song(
        id = "sp_2",
        title = "Blinding Lights (Spotify 4B+ Streams)",
        artist = "The Weeknd",
        album = "Spotify Greatest Hits",
        durationSeconds = 200,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
        coverColor1 = 0xFFFF5722L,
        coverColor2 = 0xFF3F51B5L,
        genre = "Synthwave Pop",
        likesCount = 28400,
        isLiked = true,
        commentsCount = 450,
        isDownloaded = false,
        lyrics = "I said, ooh, I'm blinded by the lights\nNo, I can't sleep until I feel your touch!"
      ),
      Song(
        id = "yt_1",
        title = "Shape of You (YouTube Trending 6B+)",
        artist = "Ed Sheeran",
        album = "YouTube Diamond Records",
        durationSeconds = 233,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
        coverColor1 = 0xFF00BCD4L,
        coverColor2 = 0xFF009688L,
        genre = "YouTube Acoustic Pop",
        likesCount = 31200,
        isLiked = false,
        commentsCount = 512,
        isDownloaded = false,
        lyrics = "The club isn't the best place to find a lover\nSo the bar is where I go\nMe and my friends at the table doing shots..."
      ),
      Song(
        id = "lat_1",
        title = "Dákiti (Latin Billboard #1)",
        artist = "Bad Bunny x Jhayco",
        album = "Latin Spotify Top Hits",
        durationSeconds = 205,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
        coverColor1 = 0xFFFF9800L,
        coverColor2 = 0xFFE91E63L,
        genre = "Spanish Reggaeton",
        likesCount = 18700,
        isLiked = false,
        commentsCount = 190,
        isDownloaded = false,
        lyrics = "Baby, ya yo me enteré, se nota cuando me ve'\nAhí donde no has llega'o sabes que yo te llevaré!"
      ),
      Song(
        id = "lat_2",
        title = "Despacito (YouTube 8B+ Classic)",
        artist = "Luis Fonsi ft. Daddy Yankee",
        album = "Spanish Global Latin",
        durationSeconds = 228,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3",
        coverColor1 = 0xFFFFC107L,
        coverColor2 = 0xFFD81B60L,
        genre = "Spanish Pop",
        likesCount = 42000,
        isLiked = true,
        commentsCount = 890,
        isDownloaded = false,
        lyrics = "Despacito...\nQuiero respirar tu cuello despacito\nDeja que te diga cosas al oído!"
      ),
      Song(
        id = "zh_1",
        title = "Qilixiang (七里香 - Chinese C-Pop)",
        artist = "Jay Chou (周杰伦)",
        album = "C-Pop Evergreen Classics",
        durationSeconds = 299,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3",
        coverColor1 = 0xFF009688L,
        coverColor2 = 0xFF3F51B5L,
        genre = "Chinese C-Pop",
        likesCount = 15300,
        isLiked = false,
        commentsCount = 210,
        isDownloaded = false,
        lyrics = "雨下整夜 我的爱溢出就像雨水\n院子落叶 跟我的思念厚厚一叠\n几句话 怎么能够概括我爱过的岁月..."
      ),
      Song(
        id = "zh_2",
        title = "Gu Yong Zhe (孤勇者 - C-Pop Rock)",
        artist = "Eason Chan (陈奕迅)",
        album = "Chinese Trending Hits",
        durationSeconds = 256,
        streamUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        coverColor1 = 0xFF673AB7L,
        coverColor2 = 0xFF00E5FFL,
        genre = "Chinese Rock",
        likesCount = 22100,
        isLiked = false,
        commentsCount = 340,
        isDownloaded = false,
        lyrics = "爱你孤身走暗巷\n爱你不跪的模样\n爱你对峙过绝望\n不肯哭一场\n去吗？去啊！以最卑微的梦！"
      )
    )
  }

  private fun createInitialFolders(): List<Folder> {
    return listOf(
      Folder(id = "fld_1", name = "Sinhala Baila & Classics", iconName = "music_note", colorHex = 0xFF1DB954L),
      Folder(id = "fld_2", name = "Gym & Workout Energy", iconName = "fitness_center", colorHex = 0xFFFF5722L),
      Folder(id = "fld_3", name = "Study & Lo-Fi Chill", iconName = "nightlife", colorHex = 0xFF00E5FFL),
      Folder(id = "fld_4", name = "AI Generated Tracks", iconName = "auto_awesome", colorHex = 0xFFA855F7L)
    )
  }

  private fun createInitialPlaylists(): List<Playlist> {
    return listOf(
      Playlist(
        id = "pl_1",
        name = "Top Sinhala Hits 2026",
        description = "The most streamed Sinhala tracks, Baila rhythms and acoustic melodies.",
        folderId = "fld_1",
        songIds = listOf("s1", "s2", "s4", "s7"),
        coverColor1 = 0xFF1DB954L,
        coverColor2 = 0xFF004D40L
      ),
      Playlist(
        id = "pl_2",
        name = "Workout Beast Mode",
        description = "High energy EDM, Cyberphonk and heavy bass drops to crush your sets.",
        folderId = "fld_2",
        songIds = listOf("s3", "s6"),
        coverColor1 = 0xFFFF5722L,
        coverColor2 = 0xFFD50000L
      ),
      Playlist(
        id = "pl_3",
        name = "Late Night Rain Beats",
        description = "Lo-fi nostalgia and smooth jazz chords for rainy evenings.",
        folderId = "fld_3",
        songIds = listOf("s5", "s2"),
        coverColor1 = 0xFF3F51B5L,
        coverColor2 = 0xFF1A237EL
      ),
      Playlist(
        id = "pl_4",
        name = "Google Lyria & Suno AI Creations",
        description = "Tracks synthesized using deep neural audio models.",
        folderId = "fld_4",
        songIds = listOf("s8", "s6"),
        coverColor1 = 0xFFA855F7L,
        coverColor2 = 0xFF00E5FFL
      )
    )
  }

  private fun createInitialComments(): Map<String, List<Comment>> {
    return mapOf(
      "s1" to listOf(
        Comment("c1", "s1", "Nuwan Perera", "N", "මේ බයිලා එක නම් පිස්සුවක්! අහලම එපා වෙන්නේ නෑ 🔥", "2 hours ago", 14),
        Comment("c2", "s1", "Dilshan Silva", "D", "Best party track ever in Sri Lanka. Bass is insane!", "5 hours ago", 8),
        Comment("c3", "s1", "Amaya Fernando", "A", "Ultra subscription offline mode එකෙන් බස් එකේ යනකොට අහන්න දාගත්තා 👌", "Yesterday", 22)
      ),
      "s2" to listOf(
        Comment("c4", "s2", "Kavindu Jay", "K", "Acoustic version is pure magic! Yohani's voice touches the soul.", "1 hour ago", 31),
        Comment("c5", "s2", "Samanthi K", "S", "Loved the guitar chords here. So soothing.", "3 hours ago", 12)
      ),
      "s6" to listOf(
        Comment("c6", "s6", "DJ Rex", "R", "Google Lyria AI generated elements are mind blowing here!", "4 hours ago", 45)
      )
    )
  }

  private fun createInitialHistory(): List<HistoryItem> {
    val songs = createInitialSongs()
    return listOf(
      HistoryItem("h1", songs[0], "10 minutes ago", System.currentTimeMillis() - 600000),
      HistoryItem("h2", songs[1], "45 minutes ago", System.currentTimeMillis() - 2700000),
      HistoryItem("h3", songs[5], "2 hours ago", System.currentTimeMillis() - 7200000),
      HistoryItem("h4", songs[3], "Yesterday", System.currentTimeMillis() - 86400000)
    )
  }

  private fun createInitialTickets(): List<SupportTicket> {
    return listOf(
      SupportTicket(
        id = "TICKET-#8821",
        subject = "Google Pay Ultra Subscription Inquiry",
        message = "Can I switch from Monthly LKR 5,000 to Yearly LKR 48,000 seamlessly?",
        status = "Resolved - 20% discount applied",
        createdAt = "2026-10-08 14:20"
      ),
      SupportTicket(
        id = "TICKET-#8822",
        subject = "Offline Cache Storage",
        message = "How much device storage is used by 320kbps offline downloads?",
        status = "Answered - Approx 8MB per track with Lossless FLAC option",
        createdAt = "2026-10-09 09:15"
      )
    )
  }
}
