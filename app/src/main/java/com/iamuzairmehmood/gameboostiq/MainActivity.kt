package com.iamuzairmehmood.gameboostiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iamuzairmehmood.gameboostiq.ui.GameBoostApp
import com.iamuzairmehmood.gameboostiq.ui.theme.GameBoostIQTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      GameBoostApp()
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Game BoostIQ $name", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  GameBoostIQTheme { Greeting("Active") }
}
