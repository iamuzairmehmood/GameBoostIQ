import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

imports = """import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import androidx.compose.foundation.lazy.itemsIndexed"""
if "org.burnoutcrew.reorderable" not in content:
    content = content.replace("import androidx.compose.foundation.lazy.items", "import androidx.compose.foundation.lazy.items\n" + imports)

bad_lazy_column = """                LazyColumn(
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

good_lazy_column = """                // Keep local sorted state
                var localGames by remember(games) { mutableStateOf(games) }
                
                val state = rememberReorderableLazyListState(onMove = { from, to ->
                    localGames = localGames.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                }, onDragEnd = { startIndex, endIndex ->
                    if (startIndex != endIndex) {
                        // In a real app we would dispatch to the ViewModel/Repository
                        // to persist the new order to Room using the updated list.
                    }
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
                
content = content.replace(bad_lazy_column, good_lazy_column)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)

