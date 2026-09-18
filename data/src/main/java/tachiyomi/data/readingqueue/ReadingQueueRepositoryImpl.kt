package tachiyomi.data.readingqueue

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import tachiyomi.data.Database
import tachiyomi.data.subscribeToList
import tachiyomi.domain.readingqueue.model.ReadingQueueItem
import tachiyomi.domain.readingqueue.repository.ReadingQueueRepository

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ReadingQueueRepositoryImpl(
    private val database: Database,
) : ReadingQueueRepository {

    override fun getQueueAsFlow(): Flow<List<ReadingQueueItem>> {
        return database.reading_queueQueries
            .getQueue(::mapItem)
            .subscribeToList()
    }

    override suspend fun getQueue(): List<ReadingQueueItem> {
        return database.reading_queueQueries
            .getQueue(::mapItem)
            .awaitAsList()
    }

    override suspend fun contains(mangaId: Long): Boolean {
        return database.reading_queueQueries
            .contains(mangaId)
            .awaitAsOne() > 0
    }

    override suspend fun add(mangaId: Long) {
        database.reading_queueQueries.insert(mangaId)
    }

    override suspend fun remove(mangaId: Long) {
        database.reading_queueQueries.delete(mangaId)
    }

    override suspend fun reorder(orderedMangaIds: List<Long>) {
        database.transaction {
            orderedMangaIds.forEachIndexed { index, mangaId ->
                database.reading_queueQueries.updatePosition(
                    position = index.toLong(),
                    mangaId = mangaId,
                )
            }
        }
    }

    private fun mapItem(
        mangaId: Long,
        title: String,
        thumbnailUrl: String?,
        favorite: Boolean,
        totalCount: Long,
        readCount: Double,
    ): ReadingQueueItem {
        return ReadingQueueItem(
            mangaId = mangaId,
            title = title,
            thumbnailUrl = thumbnailUrl,
            favorite = favorite,
            totalChapters = totalCount,
            readCount = readCount.toLong(),
        )
    }
}
