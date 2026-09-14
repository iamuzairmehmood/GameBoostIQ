import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'r') as f:
    content = f.read()

pattern = r'drawerContent = \{.*?\n        \}\n        Scaffold'

drawer_content = """drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Game BoostIQ",
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Game Library") },
                        selected = currentRoute == "game_library",
                        onClick = { navController.navigate("game_library"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.VideogameAsset, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Sessions & Stats") },
                        selected = currentRoute == "session_stats",
                        onClick = { navController.navigate("session_stats"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = currentRoute == "settings",
                        onClick = { navController.navigate("settings"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Theme") },
                        selected = currentRoute == "theme",
                        onClick = { navController.navigate("theme"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Palette, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Privacy & Security") },
                        selected = currentRoute == "privacy",
                        onClick = { navController.navigate("privacy"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Security, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("About") },
                        selected = currentRoute == "about",
                        onClick = { navController.navigate("about"); scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    Column(modifier = Modifier.padding(28.dp)) {
                        Text("Game BoostIQ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.clickable {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://iamuzairmehmood.github.io/"))
                            intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }) {
                            Text("Developed by ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("iamuzairmehmood", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                        }
                    }
                }
            }
        }
        Scaffold"""

new_content = re.sub(pattern, drawer_content, content, flags=re.DOTALL)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'w') as f:
    f.write(new_content)
