import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'r') as f:
    content = f.read()

bad_str = """                    composable("capability_scanner") {
                        CapabilityScannerScreen(
                            report = deviceReport,
                            onBack = { navController.popBackStack() },
                            onRescan = {
                                Toast.makeText(context, "Audit refreshed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                        )
                    }
                    composable("tools") {"""

good_str = """                    composable("capability_scanner") {
                        CapabilityScannerScreen(
                            report = deviceReport,
                            onBack = { navController.popBackStack() },
                            onRescan = {
                                Toast.makeText(context, "Audit refreshed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    composable("tools") {"""

content = content.replace(bad_str, good_str)
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'w') as f:
    f.write(content)
