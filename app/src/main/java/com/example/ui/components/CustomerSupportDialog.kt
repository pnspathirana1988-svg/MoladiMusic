package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.SupportTicket
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.SpotifyGreen

@Composable
fun CustomerSupportDialog(
  tickets: List<SupportTicket>,
  onDismiss: () -> Unit,
  onSubmitTicket: (subject: String, message: String) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Help & FAQs, 1: Submit Ticket, 2: My Tickets
  var ticketSubject by remember { mutableStateOf("") }
  var ticketMessage by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth()
      .testTag("customer_support_dialog"),
    containerColor = DarkSurface,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(SpotifyGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SupportAgent,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "24/7 Customer Care",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF00E676))
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Live Support Online",
                fontSize = 11.sp,
                color = Color(0xFF00E676)
              )
            }
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Tab switcher
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceCard)
            .padding(4.dp)
        ) {
          SupportTabItem(
            text = "FAQs",
            isSelected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            modifier = Modifier.weight(1f)
          )
          SupportTabItem(
            text = "New Ticket",
            isSelected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            modifier = Modifier.weight(1f)
          )
          SupportTabItem(
            text = "History (${tickets.size})",
            isSelected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
          0 -> {
            // FAQs
            FaqItem(
              question = "නොබැඳිව සිංදු අහන්නේ (Offline mode) කොහොමද?",
              answer = "Offline caching පහසුකම ඇත්තේ Ultra tier (LKR 5,000/mo) සඳහා පමණි. Ultra සාමාජිකත්වය ගත් පසු ඕනෑම ගීතයක් අසල ඇති Download බොත්තම එබීමෙන් device එකට save කරගත හැක."
            )
            Spacer(modifier = Modifier.height(8.dp))
            FaqItem(
              question = "Google Pay මගින් ගෙවීම් කරන්නේ කෙසේද?",
              answer = "You tab එකට ගොස් Pro (LKR 1,500/mo) හෝ Ultra (LKR 5,000/mo) තෝරා 'Pay with GPay' බොත්තම ඔබන්න. Google Play Billing සහ UPI/Card හරහා ආරක්ෂිතව ගෙවිය හැක."
            )
            Spacer(modifier = Modifier.height(8.dp))
            FaqItem(
              question = "Google Lyria සහ Suno AI මගින් සංගීතය හදන්නේ කෙසේද?",
              answer = "යට මෙනුවේ ඇති 'Add' tab එකට ගොස් Lyria හෝ Suno engine එක තෝරන්න. ඔබට අවශ්ය ශෛලිය (Baila, Synthwave, Lo-Fi) සහ prompt එක ලියා 'Generate Music' ඔබන්න."
            )
            Spacer(modifier = Modifier.height(8.dp))
            FaqItem(
              question = "Folders මගින් Playlists වෙන් කරන්නේ කෙසේද?",
              answer = "Library tab එකේ Folders තෝරා අලුත් folder එකක් (උදා: Gym, Baila, Study) සාදා ඔබේ playlists ඒ යටතට ඇතුළත් කළ හැක."
            )
          }

          1 -> {
            // New Ticket Form
            Column {
              Text(
                text = "Contact Support Representative",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = Color.White
                )
              )
              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = ticketSubject,
                onValueChange = { ticketSubject = it },
                label = { Text("Subject (විෂය)") },
                placeholder = { Text("e.g. Ultra subscription help") },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ticket_subject_input"),
                colors = supportTextFieldColors()
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = ticketMessage,
                onValueChange = { ticketMessage = it },
                label = { Text("Your Message (ඔබේ ගැටලුව)") },
                placeholder = { Text("Describe what you need assistance with...") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ticket_message_input"),
                colors = supportTextFieldColors()
              )

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = {
                  if (ticketSubject.isNotBlank() && ticketMessage.isNotBlank()) {
                    onSubmitTicket(ticketSubject, ticketMessage)
                    ticketSubject = ""
                    ticketMessage = ""
                    selectedTab = 2
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("ticket_submit_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = SpotifyGreen,
                  contentColor = Color.Black
                )
              ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Ticket", fontWeight = FontWeight.Bold)
              }
            }
          }

          2 -> {
            // Tickets List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              if (tickets.isEmpty()) {
                Text(
                  text = "No support tickets submitted yet.",
                  color = Color.Gray,
                  fontSize = 13.sp
                )
              } else {
                tickets.forEach { ticket ->
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(10.dp))
                      .background(DarkSurfaceCard)
                      .padding(12.dp)
                  ) {
                    Column {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(
                          text = ticket.id,
                          style = MaterialTheme.typography.labelSmall.copy(
                            color = SpotifyGreen,
                            fontWeight = FontWeight.Bold
                          )
                        )
                        Text(
                          text = ticket.createdAt,
                          style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = ticket.subject,
                        style = MaterialTheme.typography.bodyMedium.copy(
                          fontWeight = FontWeight.Bold,
                          color = Color.White
                        )
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = ticket.message,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                      )
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = "Status: ${ticket.status}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = Color(0xFF00E676),
                          fontWeight = FontWeight.SemiBold
                        )
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {}
  )
}

@Composable
fun SupportTabItem(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) SpotifyGreen else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = if (isSelected) Color.Black else Color.LightGray
    )
  }
}

@Composable
fun FaqItem(question: String, answer: String) {
  var isExpanded by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(DarkSurfaceCard)
      .clickable { isExpanded = !isExpanded }
      .padding(12.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = question,
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          ),
          modifier = Modifier.weight(1f)
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = Color.LightGray,
          modifier = Modifier.size(20.dp)
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          HorizontalDivider(color = Color(0x22FFFFFF))
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = answer,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color.LightGray,
              lineHeight = 18.sp
            )
          )
        }
      }
    }
  }
}

@Composable
fun supportTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = Color.White,
  unfocusedTextColor = Color.White,
  focusedBorderColor = SpotifyGreen,
  unfocusedBorderColor = Color.Gray,
  focusedContainerColor = DarkSurfaceCard,
  unfocusedContainerColor = DarkSurfaceCard,
  focusedLabelColor = SpotifyGreen,
  unfocusedLabelColor = Color.Gray
)
