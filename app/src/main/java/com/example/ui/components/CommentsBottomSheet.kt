package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.sp
import com.example.model.Comment
import com.example.model.Song
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.HeartRed
import com.example.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
  song: Song,
  comments: List<Comment>,
  onDismiss: () -> Unit,
  onAddComment: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var commentInput by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkSurface,
    modifier = Modifier.testTag("comments_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 24.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Comments (${comments.size})",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
          Text(
            text = song.title,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Comments list
      if (comments.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No comments yet. Be the first to share your thoughts!",
            color = Color.Gray,
            fontSize = 14.sp
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
        ) {
          items(comments, key = { it.id }) { comment ->
            CommentItemRow(comment = comment)
            Spacer(modifier = Modifier.height(8.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // New Comment Input
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = commentInput,
          onValueChange = { commentInput = it },
          placeholder = { Text("Add a comment...", color = Color.Gray) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = SpotifyGreen,
            unfocusedBorderColor = Color.DarkGray,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("comment_input_field"),
          singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = {
            if (commentInput.isNotBlank()) {
              onAddComment(commentInput)
              commentInput = ""
            }
          },
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(SpotifyGreen)
            .testTag("comment_send_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = Color.Black
          )
        }
      }
    }
  }
}

@Composable
fun CommentItemRow(comment: Comment) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceCard)
      .padding(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    // Avatar Letter
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(SpotifyGreen),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = comment.authorAvatarLetter,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = comment.authorName,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = comment.timestamp,
          style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = comment.text,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
      )
    }

    if (comment.likes > 0) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = null,
          tint = HeartRed,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "${comment.likes}", color = Color.Gray, fontSize = 11.sp)
      }
    }
  }
}
