package io.zenandroid.greenfield.domain

import io.zenandroid.greenfield.data.model.Image
import io.zenandroid.greenfield.feed.SortCriterion
import io.zenandroid.greenfield.repository.FlickrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

class LoadImageListUseCase(
    private val repository: FlickrRepository
) {
    private val _flow = MutableSharedFlow<List<Image>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val flow: Flow<List<Image>> = _flow

    suspend fun fetchImages(tags: String?, sortCriterion: SortCriterion) {
        withContext(Dispatchers.IO) {
            val unsorted = repository.getImageList(tags).items ?: emptyList()
            _flow.emit(unsorted.sortedBy {
                when (sortCriterion) {
                    SortCriterion.TAKEN -> it.dateTaken
                    SortCriterion.PUBLISHED -> it.published
                }
            })
        }
    }
}