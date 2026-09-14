with open("app/src/main/java/com/iamuzairmehmood/GameStats/GameStatsApplication.kt", "r") as f:
    content = f.read()

content = content.replace(
    "import com.iamuzairmehmood.GameStats.data.GameStatsRepository",
    "import com.iamuzairmehmood.GameStats.data.GameStatsRepository\nimport com.iamuzairmehmood.GameStats.data.SettingsRepository"
)

content = content.replace(
    "lateinit var repository: GameStatsRepository\n        private set",
    "lateinit var repository: GameStatsRepository\n        private set\n    lateinit var settingsRepository: SettingsRepository\n        private set"
)

content = content.replace(
    "database = GameStatsDatabase.getInstance(this)",
    "database = GameStatsDatabase.getInstance(this)\n        settingsRepository = SettingsRepository(this)"
)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/GameStatsApplication.kt", "w") as f:
    f.write(content)
