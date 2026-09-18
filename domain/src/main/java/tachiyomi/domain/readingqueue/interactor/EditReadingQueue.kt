package tachiyomi.domain.readingqueue.interactor

import dev.zacsweers.metro.Inject
import tachiyomi.domain.readingqueue.repository.ReadingQueueRepository

@Inject
class EditReadingQueue(
    private val repository: ReadingQueueRepository,
) {

    suspend fun contains(mangaId: Long): Boolean {
        return repository.contains(mangaId)
    }

    suspend fun add(mangaId: Long) {
        repository.add(mangaId)
    }

    suspend fun remove(mangaId: Long) {
        repository.remove(mangaId)
    }

    suspend fun reorder(orderedMangaIds: List<Long>) {
        repository.reorder(orderedMangaIds)
    }
}
