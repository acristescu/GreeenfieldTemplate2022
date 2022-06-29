package io.zenandroid.greenfield.feed

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.launchMolecule
import io.zenandroid.greenfield.domain.LoadImageListUseCase
import io.zenandroid.greenfield.feed.FeedAction.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedViewModel(
        private val loadImageListUseCase: LoadImageListUseCase,
) : ViewModel() {

    private val moleculeScope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.Main + ImmediateMonotonicFrameClock)

    private var loading by mutableStateOf(false)
    private var sortVisible by mutableStateOf(false)
    private var searchText by mutableStateOf("")
    private var tags by mutableStateOf<String?>(null)
    private var sortCriterion by mutableStateOf(SortCriterion.PUBLISHED)
    private var errorMessage by mutableStateOf<String?>(null)

    val state = moleculeScope.launchMolecule {
        val images by loadImageListUseCase.flow.collectAsState(initial = emptyList())
        FeedState(
            loading = loading,
            images = images,
            tags = tags,
            searchText = searchText,
            criterion = sortCriterion,
            sortDialogVisible = sortVisible,
            errorMessage = errorMessage,
        )
    }

    init {
        fetchImages(null)
    }

    private fun fetchImages(tags: String?) {
        viewModelScope.launch {
            loading = true
            loadImageListUseCase.fetchImages(tags, sortCriterion)
            loading = false
        }
    }

    fun onEvent(event: FeedAction) {
        when(event) {
            is SearchTextChanged -> searchText = event.text
            SearchComplete -> {
                if(searchText.isNotBlank()) {
                    fetchImages(searchText.replace("\\s+".toRegex(), ","))
                    tags = searchText
                }
            }
            ChangeFiltering -> sortVisible = true
            DismissFilterDialog -> sortVisible = false
            is FeedAction.SortCriterion -> {
                if(event.newCriterion != sortCriterion) {
                    fetchImages(searchText.replace("\\s+".toRegex(), ","))
                }
                sortVisible = false
                sortCriterion = event.newCriterion
            }
            is Browse, is Save, is Share -> { } // This is handled by the activity
            is Error -> errorMessage = event.errorMessage
            DismissError -> errorMessage = null
        }
    }
}

// Note: this is a workaround for https://github.com/cashapp/molecule/issues/63
private object ImmediateMonotonicFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
        return onFrame(System.nanoTime())
    }
}