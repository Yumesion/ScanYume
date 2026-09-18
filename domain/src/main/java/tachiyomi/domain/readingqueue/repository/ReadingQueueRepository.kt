package tachiyomi.domain.readingqueue.repository

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.readingqueue.model.ReadingQueueItem

interface ReadingQueueRepository {

    fun getQueueAsFlow(): Flow<List<ReadingQueueItem>>

    suspend fun getQueue(): List<ReadingQueueItem>

    suspend fun contains(mangaId: Long): Boolean

    suspend fun add(mangaId: Long)

    suspend fun remove(mangaId: Long)

    suspend fun reorder(orderedMangaIds: List<Long>)
}
