package com.example

import com.example.data.MusicRepository
import com.example.model.AiEngine
import com.example.model.AppLanguage
import com.example.model.BillingCycle
import com.example.model.LocalizationManager
import com.example.model.SubscriptionTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testSubscriptionTierPerks() {
    assertFalse(SubscriptionTier.FREE.canDownloadOffline)
    assertTrue(SubscriptionTier.FREE.hasAds)

    assertFalse(SubscriptionTier.PRO.canDownloadOffline)
    assertFalse(SubscriptionTier.PRO.hasAds)
    assertEquals(1500, SubscriptionTier.PRO.monthlyPriceLkr)

    assertTrue(SubscriptionTier.ULTRA.canDownloadOffline)
    assertFalse(SubscriptionTier.ULTRA.hasAds)
    assertEquals(5000, SubscriptionTier.ULTRA.monthlyPriceLkr)
    assertEquals(48000, SubscriptionTier.ULTRA.yearlyPriceLkr)
  }

  @Test
  fun testUltraDownloadGating() {
    val repo = MusicRepository()
    // By default, user is Free tier, download should fail
    val freeDownloadSuccess = repo.downloadSong("s1")
    assertFalse(freeDownloadSuccess)

    // Upgrade to Ultra
    repo.updateSubscription(SubscriptionTier.ULTRA, BillingCycle.MONTHLY)
    val ultraDownloadSuccess = repo.downloadSong("s1")
    assertTrue(ultraDownloadSuccess)

    val song = repo.songs.value.find { it.id == "s1" }
    assertNotNull(song)
    assertTrue(song!!.isDownloaded)
  }

  @Test
  fun testAiGeneratedSongCreation() {
    val repo = MusicRepository()
    val aiSong = repo.addAiGeneratedSong(
      title = "Cyber Baila 2026",
      genre = "Sinhala Baila",
      mood = "Energetic",
      engine = AiEngine.GOOGLE_LYRIA,
      lyrics = "Baila rhythm synthesized...",
      prompt = "Generate fast Sinhala baila acoustic fusion"
    )

    assertTrue(aiSong.isAiGenerated)
    assertEquals("Google Lyria", aiSong.aiEngine)
    assertTrue(repo.songs.value.any { it.id == aiSong.id })
  }

  @Test
  fun testPlaylistAndFolderOrganization() {
    val repo = MusicRepository()
    val folder = repo.createFolder("Sinhala Baila Club", 0xFF1DB954L)
    val playlist = repo.createPlaylist("Friday Night Hits", "Top Baila hits", folder.id)

    assertEquals(folder.id, playlist.folderId)
    assertTrue(repo.playlists.value.contains(playlist))
  }

  @Test
  fun testPlaybackStateAndProgress() {
    val repo = MusicRepository()
    val song = repo.songs.value.first()
    val state = com.example.player.PlaybackState(
      currentSong = song,
      isPlaying = true,
      currentPositionMs = 60000L,
      totalDurationMs = 245000L
    )

    assertTrue(state.isPlaying)
    assertEquals("01:00", state.currentPositionFormatted)
    assertEquals("04:05", state.totalDurationFormatted)
    assertTrue(state.progressFraction > 0.2f && state.progressFraction < 0.3f)
  }

  @Test
  fun testLocalizationSupport() {
    val en = LocalizationManager.getStrings(AppLanguage.ENGLISH)
    val es = LocalizationManager.getStrings(AppLanguage.SPANISH)
    val zh = LocalizationManager.getStrings(AppLanguage.CHINESE)
    val si = LocalizationManager.getStrings(AppLanguage.SINHALA)

    assertEquals("Home", en.homeTab)
    assertEquals("Inicio", es.homeTab)
    assertEquals("首页", zh.homeTab)
    assertEquals("මුල් පිටුව", si.homeTab)

    assertEquals("Lock Screen Player", en.lockScreenPlayer)
    assertEquals("Lock Screen සංගීත වාදකය", si.lockScreenPlayer)
  }

  @Test
  fun testDjMalukaUploadedTracksAndGlobalCatalog() {
    val repo = MusicRepository()
    val songs = repo.songs.value

    // Check DJ Maluka tracks
    val djSongs = songs.filter { it.artist == "DJ Maluka" }
    assertEquals(2, djSongs.size)
    assertTrue(djSongs.any { it.title.contains("Adala Hina Wela") })
    assertTrue(djSongs.any { it.title.contains("Rathu Pata Pushparaga") })

    // Check YouTube and Spotify global catalog presence
    assertTrue(songs.any { it.id == "sp_1" && it.title.contains("Espresso") })
    assertTrue(songs.any { it.id == "lat_1" && it.genre.contains("Reggaeton") })
    assertTrue(songs.any { it.id == "zh_1" && it.title.contains("Qilixiang") })
  }
}
