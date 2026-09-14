package com.iamuzairmehmood.gamestats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Security", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "GameStats is designed to operate using standard Android APIs without modifying game software. No third-party application can guarantee compatibility with every game, Android version, OEM implementation, or anti-cheat system.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Data Processing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "GameStats utilizes local-first processing. Session data is stored locally on your device. We do not collect unnecessary personal data. The app does not read or store game credentials, passwords, or game-account information.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Game Safety", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "GameStats does not inject code into games, modify game memory or files, manipulate gameplay, bypass anti-cheat systems, or modify game network packets.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Permissions Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "• Overlay: Used to display the gaming HUD over active games.\n" +
                "• Notifications: Used to provide essential alerts.\n" +
                "• Foreground Service: Required to keep the app active while gaming.\n" +
                "• DND Access: Used to automate Do Not Disturb settings while in-game.\n" +
                "• Usage Access: Used to detect when a game is launched or closed.\n" +
                "• Network Access: Used to monitor ping and network connectivity.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text("Open Source", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "GameStats is open source and its source code is available under the applicable project license.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
