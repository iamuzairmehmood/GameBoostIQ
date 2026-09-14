with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'r') as f:
    content = f.read()

import re

# Add context
content = re.sub(
    r'fun AboutScreen\(\s*deviceReport: DeviceCapabilitiesReport\? = null,\s*onBack: \(\) -> Unit\s*\)\s*\{',
    'fun AboutScreen(\n    deviceReport: DeviceCapabilitiesReport? = null,\n    onBack: () -> Unit\n) {\n    val context = androidx.compose.ui.platform.LocalContext.current',
    content,
    flags=re.MULTILINE
)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'w') as f:
    f.write(content)
