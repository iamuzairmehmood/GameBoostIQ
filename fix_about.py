import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'r') as f:
    content = f.read()

# Replace Free Fire stuff
content = content.replace('text = "Gaming Optimizer for Free Fire & Free Fire MAX",', 'text = "Supported Optimization Utility",')
content = content.replace('text = "Version 2.0.0 (GameBoostIQ Edition)",', 'text = "Version 2.0.0",')

# Replace Developer Credit Card to have clickable URL
dev_card = """            // Developer Credit Card
            Card(
                modifier = Modifier.fillMaxWidth().testTag("about_developer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OFFICIAL DEVELOPER",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Game BoostIQ",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.clickable {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("http://iamuzairmehmood.github.io/"))
                        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }) {
                        Text(
                            text = "iamuzairmehmood",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        )
                    }
                }
            }"""

content = re.sub(r'            // Developer Credit Card.*?            // Mandatory Disclaimer Card', dev_card + "\n\n            // Mandatory Disclaimer Card", content, flags=re.DOTALL)

# Remove "Third-Party Utility Notice" since I already renamed it, but let's replace the whole Disclaimer card just in case
disclaimer_card = """            // Mandatory Disclaimer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Security Disclaimer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Game BoostIQ is designed to operate using standard Android APIs without modifying game software. No third-party application can guarantee compatibility with every game, Android version, OEM implementation, or anti-cheat system.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }"""

content = re.sub(r'            // Mandatory Disclaimer Card.*?            // Current Device Status Card \(if available\)', disclaimer_card + "\n\n            // Current Device Status Card (if available)", content, flags=re.DOTALL)

# Remove POVA 2 text
content = re.sub(r'                        if \(deviceReport\.isPova2\) \{.*?\n                        \}', '', content, flags=re.DOTALL)

# Update Compatibility Matrix Card
compat_card = """            // Compatibility Matrix Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Supported Android Devices",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Designed to work across compatible Android devices.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }"""

content = re.sub(r'            // Compatibility Matrix Card.*?            // Core Pillars Card', compat_card + "\n\n            // Core Pillars Card", content, flags=re.DOTALL)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'w') as f:
    f.write(content)
