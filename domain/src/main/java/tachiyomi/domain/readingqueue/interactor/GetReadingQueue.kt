package tachiyomi.domain.readingqueue.interactor

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.readingqueue.model.ReadingQueueItem
import tachiyomi.domain.readingqueue.repository.ReadingQueueRepository

@Inject
class GetReadingQueue(
    private val repository: ReadingQueueRepository,
) {

    fun subscribe(): Flow<List<ReadingQueueItem>> {
        return repository.getQueueAsFlow()
    }

    suspend fun await(): List<ReadingQueueItem> {
        return repository.getQueue()
    }
}
