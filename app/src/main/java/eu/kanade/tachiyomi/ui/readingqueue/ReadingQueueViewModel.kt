package eu.kanade.tachiyomi.ui.readingqueue

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.domain.readingqueue.interactor.EditReadingQueue
import tachiyomi.domain.readingqueue.interactor.GetReadingQueue
import tachiyomi.domain.readingqueue.model.ReadingQueueItem
import kotlin.time.Duration.Companion.seconds

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class, binding = binding<ViewModel>())
class ReadingQueueViewModel(
    private val getReadingQueue: GetReadingQueue,
    private val editReadingQueue: EditReadingQueue,
) : ViewModel() {

    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    val state: StateFlow<State> = getReadingQueue.subscribe()
        .map { State(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = State(),
        )

    fun remove(mangaId: Long) {
        viewModelScope.launchIO {
            editReadingQueue.remove(mangaId)
        }
    }

    fun move(orderedMangaIds: List<Long>) {
        viewModelScope.launchIO {
            editReadingQueue.reorder(orderedMangaIds)
        }
    }

    @Immutable
    data class State(
        val items: List<ReadingQueueItem> = emptyList(),
    )
}
