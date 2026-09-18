package eu.kanade.tachiyomi.ui.readingqueue

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.zacsweers.metrox.viewmodel.metroViewModel
import eu.kanade.presentation.readingqueue.ReadingQueueScreen
import eu.kanade.presentation.util.Tab
import eu.kanade.tachiyomi.data.library.LibraryUpdateJob
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import eu.kanade.tachiyomi.util.system.workManager
import kotlinx.coroutines.launch
import mihon.icons.materialsymbols.MaterialSymbols
import mihon.icons.materialsymbols.rounded.FormatListNumbered
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

data object ReadingQueueTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            return TabOptions(
                index = 1u,
                title = stringResource(MR.strings.label_reading_queue),
                icon = rememberVectorPainter(MaterialSymbols.Rounded.FormatListNumbered),
            )
        }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val viewModel = metroViewModel<ReadingQueueViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ReadingQueueScreen(
            items = state.items,
            snackbarHostState = viewModel.snackbarHostState,
            onClickManga = { navigator.push(MangaScreen(it)) },
            onMove = viewModel::move,
            onRemove = viewModel::remove,
            onRefresh = {
                if (state.items.isEmpty()) {
                    scope.launch {
                        viewModel.snackbarHostState.showSnackbar(
                            context.stringResource(MR.strings.information_empty_reading_queue),
                        )
                    }
                } else {
                    val mangaIds = state.items.map { it.mangaId }.toLongArray()
                    val started = LibraryUpdateJob.startNow(context.workManager, mangaIds = mangaIds)
                    scope.launch {
                        val msg = if (started) {
                            MR.strings.updating_library
                        } else {
                            MR.strings.update_already_running
                        }
                        viewModel.snackbarHostState.showSnackbar(context.stringResource(msg))
                    }
                }
            },
        )

        LaunchedEffect(Unit) {
            (context as? MainActivity)?.ready = true
        }
    }
}
