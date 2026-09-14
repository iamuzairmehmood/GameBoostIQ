with open("app/src/main/java/com/iamuzairmehmood/GameStats/data/GameProfileEntity.kt", "r") as f:
    content = f.read()

if "profileColorHex" not in content:
    content = content.replace(
        'val performanceMode: String = "PERFORMANCE",',
        'val performanceMode: String = "PERFORMANCE",\n    val profileColorHex: String? = null,\n    val sortOrder: Int = 0,'
    )

with open("app/src/main/java/com/iamuzairmehmood/GameStats/data/GameProfileEntity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/data/GameStatsDatabase.kt", "r") as f:
    db = f.read()
    
db = db.replace('version = 1,', 'version = 2,')

with open("app/src/main/java/com/iamuzairmehmood/GameStats/data/GameStatsDatabase.kt", "w") as f:
    f.write(db)

