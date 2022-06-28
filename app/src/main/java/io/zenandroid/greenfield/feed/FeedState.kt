package io.zenandroid.greenfield.feed

import io.zenandroid.greenfield.data.model.Image

data class FeedState(
    val loading: Boolean = true,
    val images: List<Image>? = null,
    val tags: String? = null,
    val searchText: String = "",
    val criterion: SortCriterion = SortCriterion.PUBLISHED,
    val sortDialogVisible: Boolean = false,
    val errorMessage: String? = null
)

enum class SortCriterion {
    PUBLISHED, TAKEN
}