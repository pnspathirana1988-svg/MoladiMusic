package com.example.model

enum class SubscriptionTier(
  val displayName: String,
  val monthlyPriceLkr: Int,
  val yearlyPriceLkr: Int,
  val canDownloadOffline: Boolean,
  val hasAds: Boolean,
  val audioQuality: String,
  val badgeText: String
) {
  FREE(
    displayName = "Free",
    monthlyPriceLkr = 0,
    yearlyPriceLkr = 0,
    canDownloadOffline = false,
    hasAds = true,
    audioQuality = "Standard 160 kbps",
    badgeText = "FREE"
  ),
  PRO(
    displayName = "Pro",
    monthlyPriceLkr = 1500,
    yearlyPriceLkr = 15000,
    canDownloadOffline = false,
    hasAds = false,
    audioQuality = "High Def 320 kbps",
    badgeText = "PRO"
  ),
  ULTRA(
    displayName = "Ultra",
    monthlyPriceLkr = 5000,
    yearlyPriceLkr = 48000,
    canDownloadOffline = true,
    hasAds = false,
    audioQuality = "Lossless FLAC Hi-Fi",
    badgeText = "ULTRA VIP"
  )
}

enum class BillingCycle {
  MONTHLY,
  YEARLY
}

data class UserProfile(
  val id: String = "user_001",
  val name: String = "Pradeep Pathirana",
  val email: String = "pnspathirana1988@gmail.com",
  val avatarUrl: String = "",
  val isSignedIn: Boolean = true,
  val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
  val billingCycle: BillingCycle = BillingCycle.MONTHLY,
  val subscriptionActiveUntil: String = "2026-11-09",
  val isOfflineModeOnly: Boolean = false
)
