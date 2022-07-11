package io.zenandroid.greenfield.feed

import io.zenandroid.greenfield.data.model.Image

sealed class FeedAction {
    class SearchComplete(val text: String) : FeedAction()
    object ChangeFiltering : FeedAction()
    object DismissFilterDialog : FeedAction()
    object DismissError : FeedAction()
    class SortCriterion(val newCriterion: io.zenandroid.greenfield.feed.SortCriterion) : FeedAction()
    class Browse(val item: Image) : FeedAction()
    class Save(val item: Image) : FeedAction()
    class Share(val item: Image) : FeedAction()
    class Error(val errorMessage: String) : FeedAction()
}