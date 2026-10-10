package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AiEngine
import com.example.model.Song
import com.example.ui.AiGenerationUiState
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ProPurple
import com.example.ui.theme.SpotifyGreen

@Composable
fun AddAiMusicScreen(
  aiGenState: AiGenerationUiState,
  onGenerateMusic: (prompt: String, genre: String, mood: String, engine: AiEngine, lyrics: String) -> Unit,
  onPlaySong: (Song) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedEngine by remember { mutableStateOf(AiEngine.GOOGLE_LYRIA) }
  var promptInput by remember { mutableStateOf("Sri Lankan baila acoustic vibe with flute & conga") }
  var selectedGenre by remember { mutableStateOf("Sinhala Baila") }
  var selectedMood by remember { mutableStateOf("Energetic") }
  var customLyrics by remember { mutableStateOf("") }
  var showLyricsInput by remember { mutableStateOf(false) }

  val promptPresets = listOf(
    "Sri Lankan baila acoustic vibe with flute & conga",
    "Lo-Fi rainy night chill beats with saxophone",
    "128 BPM cyberpunk synthwave phonk with 808 bass",
    "Sinhala romantic acoustic guitar ballad with soft strings",
    "Tropical island EDM festival drop with live drums"
  )

  val genres = listOf("Sinhala Baila", "Lo-Fi Chill", "Cyber Phonk", "EDM Festival", "Acoustic Pop", "Classical Fusion")
  val moods = listOf("Energetic", "Relaxed", "Romantic", "Melancholic", "Uplifting")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("add_ai_music_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = SpotifyGreen,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "AI Music Creator Studio",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
          Text(
            text = "Powered by Google Lyria & Suno Audio Models",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // AI Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(DarkSurfaceCard)
      ) {
        Image(
          painter = painterResource(id = R.drawable.ai_studio_banner),
          contentDescription = "AI Studio Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xD9000000))
              )
            )
            .padding(12.dp),
          contentAlignment = Alignment.BottomStart
        ) {
          Text(
            text = "Deep Neural Audio Synthesis Lab",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // Engine Selection Switcher
    item {
      Text(
        text = "SELECT AI AUDIO ENGINE",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color.LightGray,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Google Lyria Card
        EngineCard(
          engine = AiEngine.GOOGLE_LYRIA,
          isSelected = selectedEngine == AiEngine.GOOGLE_LYRIA,
          accentColor = Color(0xFF00E5FF),
          onClick = { selectedEngine = AiEngine.GOOGLE_LYRIA },
          modifier = Modifier.weight(1f)
        )

        // Suno Card
        EngineCard(
          engine = AiEngine.SUNO_AI,
          isSelected = selectedEngine == AiEngine.SUNO_AI,
          accentColor = ProPurple,
          onClick = { selectedEngine = AiEngine.SUNO_AI },
          modifier = Modifier.weight(1f)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Prompt Input
    item {
      Text(
        text = "MUSIC PROMPT (විස්තරය)",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color.LightGray,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(6.dp))

      OutlinedTextField(
        value = promptInput,
        onValueChange = { promptInput = it },
        placeholder = { Text("e.g. Sri Lankan baila live beats with brass section...", color = Color.Gray) },
        minLines = 2,
        maxLines = 4,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = SpotifyGreen,
          unfocusedBorderColor = Color.DarkGray,
          focusedContainerColor = DarkSurfaceCard,
          unfocusedContainerColor = DarkSurfaceCard
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_prompt_input")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Preset chips
      Text(
        text = "Quick Inspiration Presets:",
        fontSize = 11.sp,
        color = Color.Gray
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        promptPresets.forEach { preset ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF242424))
              .clickable { promptInput = preset }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(text = preset, color = Color.LightGray, fontSize = 11.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
    }

    // Genre Selector
    item {
      Text(
        text = "GENRE & STYLE",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color.LightGray,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        genres.forEach { genre ->
          val isSelected = selectedGenre == genre
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) SpotifyGreen else Color(0xFF282828))
              .clickable { selectedGenre = genre }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = genre,
              color = if (isSelected) Color.Black else Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Mood Selector
    item {
      Text(
        text = "MOOD & VIBE",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color.LightGray,
          letterSpacing = 1.sp,
          fontWeight = FontWeight.Bold
        )
      )
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        moods.forEach { mood ->
          val isSelected = selectedMood == mood
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) ProPurple else Color(0xFF282828))
              .clickable { selectedMood = mood }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = mood,
              color = Color.White,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
    }

    // Custom Lyrics toggle
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showLyricsInput = !showLyricsInput },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (showLyricsInput) "[-] Hide Custom Lyrics" else "[+] Add Custom Lyrics / Sinhala Verses",
          color = SpotifyGreen,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      AnimatedVisibility(visible = showLyricsInput) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          OutlinedTextField(
            value = customLyrics,
            onValueChange = { customLyrics = it },
            placeholder = { Text("Enter Sinhala or English lyrics for the AI to vocalize...", color = Color.Gray) },
            minLines = 3,
            maxLines = 6,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedBorderColor = SpotifyGreen,
              unfocusedBorderColor = Color.DarkGray,
              focusedContainerColor = DarkSurfaceCard,
              unfocusedContainerColor = DarkSurfaceCard
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
      Spacer(modifier = Modifier.height(18.dp))
    }

    // Synthesis Generation Button
    item {
      Button(
        onClick = {
          if (!aiGenState.isGenerating && promptInput.isNotBlank()) {
            onGenerateMusic(promptInput, selectedGenre, selectedMood, selectedEngine, customLyrics)
          }
        },
        enabled = !aiGenState.isGenerating,
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("ai_generate_button"),
        shape = RoundedCornerShape(27.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = SpotifyGreen,
          contentColor = Color.Black
        )
      ) {
        if (aiGenState.isGenerating) {
          CircularProgressIndicator(
            color = Color.Black,
            strokeWidth = 2.5.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text("Synthesizing Audio...", fontWeight = FontWeight.Bold)
        } else {
          Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Synthesize Track with ${selectedEngine.title}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
    }

    // Live AI Generation Progress
    if (aiGenState.isGenerating) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceCard)
            .padding(14.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = aiGenState.statusMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.weight(1f)
              )
              Text(
                text = "${(aiGenState.progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = SpotifyGreen,
                  fontWeight = FontWeight.Bold
                )
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { aiGenState.progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = SpotifyGreen,
              trackColor = Color(0x33FFFFFF)
            )
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }
    }

    // Last Generated Track Showcase
    aiGenState.lastGeneratedSong?.let { song ->
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E2A20))
            .border(1.dp, SpotifyGreen, RoundedCornerShape(14.dp))
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                  Brush.linearGradient(
                    listOf(Color(song.coverColor1), Color(song.coverColor2))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Track Generated!",
                  color = SpotifyGreen,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SpotifyGreen, modifier = Modifier.size(14.dp))
              }
              Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                ),
                maxLines = 1
              )
              Text(
                text = "${song.artist} • ${song.genre}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
              )
            }

            IconButton(
              onClick = { onPlaySong(song) },
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SpotifyGreen)
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = "Play Generated Song", tint = Color.Black)
            }
          }
        }
      }
    }
  }
}

@Composable
fun EngineCard(
  engine: AiEngine,
  isSelected: Boolean,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) accentColor else Color(0x33FFFFFF),
        shape = RoundedCornerShape(12.dp)
      )
      .clickable(onClick = onClick)
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = engine.title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
        if (isSelected) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = engine.badge,
        style = MaterialTheme.typography.labelSmall.copy(color = accentColor)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = engine.description,
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp),
        maxLines = 2
      )
    }
  }
}
