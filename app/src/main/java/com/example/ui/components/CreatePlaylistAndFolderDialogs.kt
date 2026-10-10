package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Folder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
  folders: List<Folder>,
  onDismiss: () -> Unit,
  onCreatePlaylist: (name: String, description: String, folderId: String?) -> Unit
) {
  var playlistName by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedFolderId by remember { mutableStateOf<String?>(null) }
  var folderDropdownExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.QueueMusic,
          contentDescription = null,
          tint = SpotifyGreen
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Create New Playlist",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = playlistName,
          onValueChange = { playlistName = it },
          label = { Text("Playlist Name") },
          placeholder = { Text("e.g. My Baila Favorites") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("playlist_name_input"),
          colors = supportTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          placeholder = { Text("e.g. High energy acoustic vibes") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("playlist_desc_input"),
          colors = supportTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Assign to Folder dropdown
        Text(
          text = "Assign to Folder (Optional)",
          style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
        )
        Spacer(modifier = Modifier.height(4.dp))

        val currentFolderName = folders.firstOrNull { it.id == selectedFolderId }?.name ?: "None (Unassigned)"

        ExposedDropdownMenuBox(
          expanded = folderDropdownExpanded,
          onExpandedChange = { folderDropdownExpanded = !folderDropdownExpanded }
        ) {
          OutlinedTextField(
            value = currentFolderName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = folderDropdownExpanded) },
            colors = supportTextFieldColors(),
            modifier = Modifier
              .menuAnchor(MenuAnchorType.PrimaryNotEditable)
              .fillMaxWidth()
          )

          ExposedDropdownMenu(
            expanded = folderDropdownExpanded,
            onDismissRequest = { folderDropdownExpanded = false },
            modifier = Modifier.background(DarkSurface)
          ) {
            DropdownMenuItem(
              text = { Text("None (Unassigned)", color = Color.White) },
              onClick = {
                selectedFolderId = null
                folderDropdownExpanded = false
              }
            )
            folders.forEach { f ->
              DropdownMenuItem(
                text = { Text(f.name, color = Color.White) },
                onClick = {
                  selectedFolderId = f.id
                  folderDropdownExpanded = false
                }
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (playlistName.isNotBlank()) {
            onCreatePlaylist(playlistName, description, selectedFolderId)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = SpotifyGreen,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("playlist_create_submit_button")
      ) {
        Text("Create", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Cancel", color = Color.LightGray)
      }
    },
    modifier = Modifier.testTag("create_playlist_dialog")
  )
}

@Composable
fun CreateFolderDialog(
  onDismiss: () -> Unit,
  onCreateFolder: (name: String, colorHex: Long) -> Unit
) {
  var folderName by remember { mutableStateOf("") }
  val colorPalette = listOf(
    0xFF1DB954L, // Spotify Green
    0xFFFF5722L, // Deep Orange
    0xFF00E5FFL, // Cyan
    0xFFA855F7L, // Purple
    0xFFFFD700L, // Ultra Gold
    0xFFE91E63L  // Pink
  )
  var selectedColor by remember { mutableLongStateOf(colorPalette[0]) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = DarkSurface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.CreateNewFolder,
          contentDescription = null,
          tint = SpotifyGreen
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Create Custom Folder",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Organize your playlists by custom moods, workouts, or genres.",
          style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = folderName,
          onValueChange = { folderName = it },
          label = { Text("Folder Name (ෆෝල්ඩරයේ නම)") },
          placeholder = { Text("e.g. Gym Workout, Sinhala Classics") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("folder_name_input"),
          colors = supportTextFieldColors()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Pick Folder Color Tag",
          style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          colorPalette.forEach { c ->
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(c))
                .clickable { selectedColor = c }
                .padding(2.dp)
            ) {
              if (selectedColor == c) {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.Center)
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (folderName.isNotBlank()) {
            onCreateFolder(folderName, selectedColor)
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = SpotifyGreen,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("folder_create_submit_button")
      ) {
        Text("Create Folder", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Cancel", color = Color.LightGray)
      }
    },
    modifier = Modifier.testTag("create_folder_dialog")
  )
}
