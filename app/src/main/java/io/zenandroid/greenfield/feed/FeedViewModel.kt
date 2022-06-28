package io.zenandroid.greenfield.feed

import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

    private val loading = mutableStateOf(false)
    private val sortVisible = mutableStateOf(false)
    private val searchText = mutableStateOf("")
    private val tags = mutableStateOf<String?>(null)
    private val sortCriterion = mutableStateOf(SortCriterion.PUBLISHED)
    private val errorMessage = mutableStateOf<String?>(null)

    val state = moleculeScope.launchMolecule {
        val images by loadImageListUseCase.flow.collectAsState(initial = emptyList())
        FeedState(
            loading = loading.value,
            images = images,
            tags = tags.value,
            searchText = searchText.value,
            criterion = sortCriterion.value,
            sortDialogVisible = sortVisible.value,
            errorMessage = errorMessage.value,
        )
    }

    init {
        fetchImages(null)
    }

    private fun fetchImages(tags: String?) {
        viewModelScope.launch {
            loading.value = true
            loadImageListUseCase.fetchImages(tags, sortCriterion.value)
            loading.value = false
        }
    }

    fun onEvent(event: FeedAction) {
        when(event) {
            is SearchTextChanged -> searchText.value = event.text
            SearchComplete -> {
                if(searchText.value.isNotBlank()) {
                    fetchImages(searchText.value.replace("\\s+".toRegex(), ","))
                    tags.value = searchText.value
                }
            }
            ChangeFiltering -> sortVisible.value = true
            DismissFilterDialog -> sortVisible.value = false
            is FeedAction.SortCriterion -> {
                if(event.newCriterion != sortCriterion.value) {
                    fetchImages(searchText.value.replace("\\s+".toRegex(), ","))
                }
                sortVisible.value = false
                sortCriterion.value = event.newCriterion
            }
            is Browse, is Save, is Share -> { } // This is handled by the activity
            is Error -> errorMessage.value = event.errorMessage
            DismissError -> errorMessage.value = null
        }
    }
}

// Note: this is a workaround for https://github.com/cashapp/molecule/issues/63
private object ImmediateMonotonicFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
        return onFrame(System.nanoTime())
    }
}