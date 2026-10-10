# Global Music Catalog, Floating PiP Player, Multi-Language & Pro Lock Screen Player

Expand VibeStream into a worldwide streaming application featuring an extensive online catalog with YouTube & Spotify search, a draggable floating Picture-in-Picture (PiP) background bubble player, 4-language localization (English, Spanish, Chinese, Sinhala), a Pro-exclusive Lock Screen Player, and dedicated integration of the user's uploaded DJ Maluka Sinhala remix tracks in Settings.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Key choices confirmed in Phase 1 clarification:
> - **Floating Background Player**: Floating draggable bubble with on-screen player controls (Play, Pause, Skip, Waveform visualizer, and Return to App button).
> - **Default App Language**: English by default, with an instant multi-language switcher across English, Spanish (Español), Chinese (中文), and Sinhala (සිංහල).
> - **Uploaded DJ Maluka Tracks**: Featured in the **Settings / You** tab under a dedicated "Your Uploaded DJ Mixes" section with instant playback and queue capabilities.
> - **Pro-Gated Lock Screen Player**: Accessible only to **Pro (LKR 1,500)** and **Ultra (LKR 5,000)** subscribers, displaying stylized artwork, glowing time/date, lyrics snippet, and quick audio controls. Free users attempting to enable it receive an upgrade prompt.

---

### 1. Overview & Core Concept

- **What It Does**:
  1. **YouTube & Spotify Global Catalog Search**: Access and stream an extensive library across trending charts, Sinhala remixes, Latin/Spanish reggaeton, C-Pop (Chinese), and US/UK Billboard hits.
  2. **Draggable Floating Background PiP Player**: When users navigate away or enable PiP mode, a stylish floating bubble hovers over the screen with drag-to-position physics, live rotating cover art, play/pause, next track, and one-tap return to the full app.
  3. **Multi-Language Architecture**: Full UI localization in English, Spanish, Chinese, and Sinhala. Instant language switching updates navigation labels, headings, buttons, and badges in real time.
  4. **Pro-Exclusive Lock Screen Player**: A stylized simulated lock screen ambient player with time, battery, glowing album cover, synchronized audio visualizer, and scrub controls, gated specifically for Pro and Ultra members.
  5. **Uploaded Audio Tracks (DJ Maluka Remixes)**: Incorporates the two user-provided high-energy tracks (*Adala Hina Wela Remix* and *Rathu Pata Pushparaga Remix*) with dedicated playback in the Settings/You hub.

- **Target Audience**: Music lovers and Sinhala baila/party enthusiasts who want a modern music player with floating background controls, global search, multilingual support, and premium lock screen playback.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Global YouTube & Spotify Music Search Flow**:
   - In the **Library** or dedicated **Explore / Search** bar, the user types any keyword (e.g., "DJ Maluka", "Bad Bunny", "Jay Chou", "Taylor Swift", "Sinhala Baila").
   - Filter chips allow instant toggling between **All**, **Spotify Top 50**, **YouTube Trending**, **Sinhala Hits**, **Spanish (Latin)**, and **C-Pop (Chinese)**.
   - Tap any track to stream instantly with real-time waveform visualization.

2. **Floating Draggable Background Bubble Flow**:
   - The user taps the "Pop-up Floating Player" button on the persistent bottom bar or Settings.
   - A circular neon-accented mini widget floats over the screen with drag-and-snap gesture support.
   - Expanding the bubble reveals play/pause, skip forward/back, and a "Return to App" button.

3. **Multi-Language Switcher Flow**:
   - Inside **You / Settings**, a "Display Language" picker allows selecting between:
     - 🇺🇸 English (Default)
     - 🇪🇸 Español (Spanish)
     - 🇨🇳 中文 (Chinese)
     - 🇱🇰 සිංහල (Sinhala)
   - Selecting any language instantly re-renders the app's strings and tab titles without restarting.

4. **Pro Lock Screen Player Flow**:
   - In the player or Settings, the user taps "Lock Screen Player Mode".
   - If user is on the **Free** tier, a sleek upgrade dialog appears explaining that the Lock Screen Player is a **Pro & Ultra** exclusive perk (LKR 1,500/mo) with a one-tap Google Pay upgrade.
   - If **Pro** or **Ultra**, the ambient lock screen player activates: displaying digital clock, date, glowing album art, music controls, and quick unlock slider.

5. **Uploaded DJ Mixes Flow**:
   - In **You / Settings**, a special "Your Uploaded DJ Mixes" section features:
     - *DJ Maluka - Adala Hina Wela (Party Baila Remix)*
     - *DJ Maluka - Rathu Pata Pushparaga (Live Remix)*
   - Tap to stream, loop, like, or add to any custom playlist.

#### Visual Theme & Aesthetic
- **Atmosphere**: Pure Spotify dark theme (`#121212`, `#181818`, `#242424`) with emerald neon (`#1DB954`), electric cyan (`#00E5FF`), and royal gold (`#FFD700`).
- **Floating Bubble Widget**: Translucent glassmorphism with subtle glow, rounded 24dp pill or 56dp disc, and smooth dragging physics.
- **Lock Screen Player**: Full-screen AMOLED black canvas with atmospheric ambient light blur matching the current track's album artwork colors.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Floating Overlay Experience**:
  - *Chosen Approach*: In-app floating draggable PiP bubble component using Compose pointer input drag gestures with snap-to-edge animation, plus simulated background pop-up toggle.
  - *Why*: Provides fluid, crash-proof draggable floating player controls without requiring sensitive system-level `SYSTEM_ALERT_WINDOW` Android permission overrides.
- **Decision 2: Localization Architecture**:
  - *Chosen Approach*: Reactive string localization state manager (`LocaleManager` / `LanguageState`) supporting English, Spanish, Chinese, and Sinhala.
  - *Why*: Allows instant in-app switching without needing device OS language changes or activity recreation.
- **Decision 3: Lock Screen Player Pro Gating**:
  - *Chosen Approach*: Gate the Lock Screen Player feature for Pro and Ultra subscribers. Free users receive an informative Pro upgrade modal with Google Pay activation.
  - *Why*: Fulfills the exact user requirement ("එය ගත හැක්කේ pro member ලාට පමනි") and creates strong value for the LKR 1,500 Pro tier.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          MAIN APPLICATION                              │
│                                                                        │
│  ┌────────────────────────┐  ┌──────────────────────────────────────┐  │
│  │   LanguageState        │  │     UserProfile & Subscription       │  │
│  │ (EN, ES, ZH, SI Strings│  │  (FREE, PRO: LKR 1500, ULTRA: 5000)  │  │
│  └───────────┬────────────┘  └──────────────────┬───────────────────┘  │
│              │                                  │                      │
│              ▼                                  ▼                      │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                         UI SCREENS                               │  │
│  │  - Home (Global & Sinhala Trending Feed)                         │  │
│  │  - Library (YouTube / Spotify Multi-Million Catalog Search)      │  │
│  │  - Add (Google Lyria & Suno AI Music Generator)                  │  │
│  │  - History (Recently Played & AI Recommendations)                │  │
│  │  - You / Settings (DJ Maluka Tracks, Language, Lock Player)      │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
│                                     │                                  │
│              ┌──────────────────────┴──────────────────────┐           │
│              ▼                                             ▼           │
│  ┌─────────────────────────┐               ┌────────────────────────┐  │
│  │  Floating PiP Bubble    │               │  Lock Screen Player    │  │
│  │  - Draggable & Snapping │               │  - Ambient AMOLED Glow │  │
│  │  - Play / Skip controls │               │  - Pro / Ultra Gated   │  │
│  └─────────────────────────┘               └────────────────────────┘  │
│                                                                        │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      AUDIO PLAYBACK ENGINE                       │  │
│  │  - AudioPlayerManager (MediaPlayer + Synthetic Audio Fallback)   │  │
│  │  - Real-time Position Tracking, Scrubbing, Skip Prev/Next        │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Component & State Details
- `LocaleManager`: Holds current `AppLanguage` (`ENGLISH`, `SPANISH`, `CHINESE`, `SINHALA`) and supplies translated string maps for UI components.
- `FloatingBubblePlayer`: Draggable Compose overlay with touch gesture drag detection, snap-to-screen-boundary animation, miniature album cover, Play/Pause, and Skip Next.
- `LockScreenPlayerSheet`: Fullscreen modal simulating an AMOLED lock screen with live clock, music equalizer, battery icon, playback controls, and unlock slider. Checks `subscriptionTier >= PRO`.
- `MusicRepository`: Extends search index to support extensive YouTube & Spotify query matching across genres, and adds the uploaded DJ Maluka remixes into the playback catalog.
