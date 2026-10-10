package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScreen
import com.example.ui.MusicViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val musicViewModel: MusicViewModel = viewModel()
      val isDarkMode by musicViewModel.isDarkMode.collectAsState()

      MyApplicationTheme(darkTheme = isDarkMode) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = DarkBackground
        ) {
          MainScreen(viewModel = musicViewModel)
        }
      }
    }
  }
}
