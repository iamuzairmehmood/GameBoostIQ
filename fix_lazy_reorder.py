import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# Add shfit-foundation imports for reorderable list if they don't exist
imports = """import sh.calaba.org.burnoutcrew.reorderable.ReorderableItem
import sh.calaba.org.burnoutcrew.reorderable.detectReorderAfterLongPress
import sh.calaba.org.burnoutcrew.reorderable.rememberReorderableLazyListState
import sh.calaba.org.burnoutcrew.reorderable.reorderable"""
if "detectReorderAfterLongPress" not in content:
    content = content.replace("import androidx.compose.foundation.lazy.items", "import androidx.compose.foundation.lazy.items\nimport androidx.compose.foundation.lazy.itemsIndexed\n" + imports)


# Find the LazyColumn block
lazy_column_target = """                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(games, key = { it.packageName }) { game ->
                        GameItemCard(
                            game = game,
                            onLaunch = { onLaunchGame(game) },
                            onEdit = { editingGame = game },
                            onDelete = { onDeleteGame(game.packageName) }
                        )
                    }
                }"""

new_lazy_column = """                
                // Keep local sorted state
                var localGames by remember(games) { mutableStateOf(games) }
                
                val state = rememberReorderableLazyListState(onMove = { from, to ->
                    localGames = localGames.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                }, onDragEnd = { startIndex, endIndex ->
                    // Optionally save custom order to database here
                })
                
                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .weight(1f)
                        .reorderable(state),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(localGames, key = { it.packageName }) { game ->
                        ReorderableItem(state, key = game.packageName) { isDragging ->
                            val elevation = if (isDragging) 8.dp else 0.dp
                            Box(modifier = Modifier.detectReorderAfterLongPress(state)) {
                                GameItemCard(
                                    game = game,
                                    onLaunch = { onLaunchGame(game) },
                                    onEdit = { editingGame = game },
                                    onDelete = { onDeleteGame(game.packageName) }
                                )
                            }
                        }
                    }
                }"""

content = content.replace(lazy_column_target, new_lazy_column)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
