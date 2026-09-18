package tachiyomi.domain.readingqueue.model

data class ReadingQueueItem(
    val mangaId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val favorite: Boolean,
    val totalChapters: Long,
    val readCount: Long,
) {
    val unreadCount: Long
        get() = totalChapters - readCount
}
