package eu.kanade.presentation.readingqueue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.manga.components.MangaCover
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.rounded.Close
import mihon.icons.materialsymbols.rounded.DragHandle
import mihon.icons.materialsymbols.rounded.Refresh
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import tachiyomi.domain.readingqueue.model.ReadingQueueItem
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen

@Composable
fun ReadingQueueScreen(
    items: List<ReadingQueueItem>,
    snackbarHostState: SnackbarHostState,
    onClickManga: (Long) -> Unit,
    onMove: (List<Long>) -> Unit,
    onRemove: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(MR.strings.label_reading_queue),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = MaterialSymbols.Rounded.Refresh,
                            contentDescription = stringResource(MR.strings.action_update_library),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        if (items.isEmpty()) {
            EmptyScreen(
                stringRes = MR.strings.information_empty_reading_queue,
                modifier = Modifier.padding(paddingValues),
            )
            return@Scaffold
        }

        ReadingQueueContent(
            items = items,
            paddingValues = paddingValues,
            onClickManga = onClickManga,
            onMove = onMove,
            onRemove = onRemove,
        )
    }
}

@Composable
private fun ReadingQueueContent(
    items: List<ReadingQueueItem>,
    paddingValues: PaddingValues,
    onClickManga: (Long) -> Unit,
    onMove: (List<Long>) -> Unit,
    onRemove: (Long) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val itemsState = remember { items.toMutableStateList() }
    val reorderableState = rememberReorderableLazyListState(lazyListState, paddingValues) { from, to ->
        val item = itemsState.removeAt(from.index)
        itemsState.add(to.index, item)
        onMove(itemsState.map { it.mangaId })
    }

    LaunchedEffect(items) {
        if (!reorderableState.isAnyItemDragging) {
            itemsState.clear()
            itemsState.addAll(items)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        items(
            items = itemsState,
            key = { item -> item.mangaId },
        ) { item ->
            ReorderableItem(reorderableState, item.mangaId) {
                ReadingQueueItemRow(
                    item = item,
                    onClick = { onClickManga(item.mangaId) },
                    onRemove = { onRemove(item.mangaId) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.ReadingQueueItemRow(
    item: ReadingQueueItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = MaterialTheme.padding.medium,
                vertical = MaterialTheme.padding.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = MaterialSymbols.Rounded.DragHandle,
            contentDescription = null,
            modifier = Modifier
                .padding(end = MaterialTheme.padding.small)
                .draggableHandle(),
        )
        MangaCover.Book(
            data = item.thumbnailUrl,
            modifier = Modifier.width(48.dp),
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = MaterialTheme.padding.medium),
        )
        Text(
            text = pluralStringResource(
                MR.plurals.reading_queue_chapters_left,
                count = item.unreadCount.toInt(),
                item.unreadCount,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = MaterialSymbols.Rounded.Close,
                contentDescription = stringResource(MR.strings.action_remove_from_reading_queue),
            )
        }
    }
}
