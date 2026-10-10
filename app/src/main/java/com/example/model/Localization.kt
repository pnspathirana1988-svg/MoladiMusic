package com.example.model

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
  ENGLISH("en", "English", "🇺🇸"),
  SPANISH("es", "Español", "🇪🇸"),
  CHINESE("zh", "中文", "🇨🇳"),
  SINHALA("si", "සිංහල", "🇱🇰")
}

data class AppStrings(
  val homeTab: String,
  val libraryTab: String,
  val addTab: String,
  val historyTab: String,
  val youTab: String,
  val searchPlaceholder: String,
  val floatingPlayer: String,
  val floatingPlayerDesc: String,
  val lockScreenPlayer: String,
  val lockScreenPlayerDesc: String,
  val languageSetting: String,
  val uploadedMixesTitle: String,
  val uploadedMixesDesc: String,
  val proExclusiveBadge: String,
  val unlockProPrompt: String,
  val slideToUnlock: String,
  val returnToApp: String,
  val youtubeSpotifyCatalog: String,
  val trendingSpotify: String,
  val trendingYouTube: String
)

object LocalizationManager {
  private val englishStrings = AppStrings(
    homeTab = "Home",
    libraryTab = "Library",
    addTab = "Add",
    historyTab = "History",
    youTab = "You",
    searchPlaceholder = "Search 100M+ songs on YouTube & Spotify...",
    floatingPlayer = "Floating Pop-up Player",
    floatingPlayerDesc = "Display draggable music bubble anywhere on phone",
    lockScreenPlayer = "Lock Screen Player",
    lockScreenPlayerDesc = "Full-screen ambient lock display (Pro exclusive)",
    languageSetting = "Display Language",
    uploadedMixesTitle = "Your Uploaded DJ Mixes",
    uploadedMixesDesc = "Custom DJ Maluka party remixes uploaded by you",
    proExclusiveBadge = "PRO ONLY",
    unlockProPrompt = "Lock Screen Player is exclusive to Pro members (LKR 1,500/mo). Upgrade with Google Pay to unlock!",
    slideToUnlock = "Slide to Return to App",
    returnToApp = "Open VibeStream",
    youtubeSpotifyCatalog = "YouTube & Spotify Global Catalog",
    trendingSpotify = "Spotify Top 50 Global",
    trendingYouTube = "YouTube Trending Music"
  )

  private val spanishStrings = AppStrings(
    homeTab = "Inicio",
    libraryTab = "Biblioteca",
    addTab = "Crear",
    historyTab = "Historial",
    youTab = "Perfil",
    searchPlaceholder = "Buscar más de 100M+ canciones en YouTube y Spotify...",
    floatingPlayer = "Reproductor Flotante Emergente",
    floatingPlayerDesc = "Burbuja de música arrastrable en cualquier parte del móvil",
    lockScreenPlayer = "Pantalla de Bloqueo Musical",
    lockScreenPlayerDesc = "Pantalla ambiental de bloqueo (Exclusivo Pro)",
    languageSetting = "Idioma de la Aplicación",
    uploadedMixesTitle = "Tus Mezclas DJ Subidas",
    uploadedMixesDesc = "Remixes exclusivos de DJ Maluka subidos por ti",
    proExclusiveBadge = "SOLO PRO",
    unlockProPrompt = "El reproductor de pantalla de bloqueo es exclusivo para miembros Pro (LKR 1,500/mes). ¡Actualiza con Google Pay!",
    slideToUnlock = "Desliza para volver a la app",
    returnToApp = "Abrir VibeStream",
    youtubeSpotifyCatalog = "Catálogo Global de YouTube y Spotify",
    trendingSpotify = "Top 50 Global Spotify",
    trendingYouTube = "Tendencias en YouTube Música"
  )

  private val chineseStrings = AppStrings(
    homeTab = "首页",
    libraryTab = "音乐库",
    addTab = "创作",
    historyTab = "历史",
    youTab = "我的",
    searchPlaceholder = "在 YouTube 和 Spotify 搜索超过 1 亿首歌曲...",
    floatingPlayer = "悬浮画中画播放器",
    floatingPlayerDesc = "在手机任何界面显示可拖动的音乐气泡",
    lockScreenPlayer = "锁屏音乐播放器",
    lockScreenPlayerDesc = "全屏氛围锁屏界面 (Pro 会员专属)",
    languageSetting = "界面语言",
    uploadedMixesTitle = "您上传的 DJ 混音",
    uploadedMixesDesc = "由您上传的 DJ Maluka 派对现场混音",
    proExclusiveBadge = "PRO 专属",
    unlockProPrompt = "锁屏播放器仅限 Pro 会员使用 (每月 LKR 1,500)。使用 Google Pay 立即开通！",
    slideToUnlock = "滑动返回应用",
    returnToApp = "打开 VibeStream",
    youtubeSpotifyCatalog = "YouTube & Spotify 全球曲库",
    trendingSpotify = "Spotify 全球前 50",
    trendingYouTube = "YouTube 音乐热榜"
  )

  private val sinhalaStrings = AppStrings(
    homeTab = "මුල් පිටුව",
    libraryTab = "පුස්තකාලය",
    addTab = "AI සංගීතය",
    historyTab = "ඉතිහාසය",
    youTab = "ගිණුම",
    searchPlaceholder = "YouTube සහ Spotify හි සිංදු මිලියන 100+ අතර සොයන්න...",
    floatingPlayer = "පාවෙන Pop-up වාදකය (Floating PiP)",
    floatingPlayerDesc = "දුරකථනයේ ඕනෑම තැනක සිටියදී පාවෙන සංගීත බුබුලක් පෙන්වන්න",
    lockScreenPlayer = "Lock Screen සංගීත වාදකය",
    lockScreenPlayerDesc = "තිරය අගුළු දැමූ විට දිස්වන වාදකය (Pro සාමාජිකයින්ට පමණි)",
    languageSetting = "යෙදුමේ භාෂාව (Language)",
    uploadedMixesTitle = "ඔබ උඩුගත කළ DJ ගීත (Uploaded Mixes)",
    uploadedMixesDesc = "DJ Maluka විසින් සකස් කළ ඔබේ විශේෂ Baila remixes",
    proExclusiveBadge = "PRO පමණි",
    unlockProPrompt = "Lock Screen Player පහසුකම ඇත්තේ Pro සාමාජිකයින්ට පමණි (LKR 1,500/මසකට). Google Pay මගින් සක්‍රීය කරන්න!",
    slideToUnlock = "යෙදුමට පිවිසීමට අදින්න (Slide to Unlock)",
    returnToApp = "VibeStream වෙත යන්න",
    youtubeSpotifyCatalog = "YouTube සහ Spotify ගෝලීය සංගීත නාමාවලිය",
    trendingSpotify = "Spotify Global Top 50",
    trendingYouTube = "YouTube Trending සංගීතය"
  )

  fun getStrings(language: AppLanguage): AppStrings {
    return when (language) {
      AppLanguage.ENGLISH -> englishStrings
      AppLanguage.SPANISH -> spanishStrings
      AppLanguage.CHINESE -> chineseStrings
      AppLanguage.SINHALA -> sinhalaStrings
    }
  }
}
