package io.zenandroid.greenfield.feed

import com.nhaarman.mockitokotlin2.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.zenandroid.greenfield.data.model.ImageListResponse
import io.zenandroid.greenfield.domain.LoadImageListUseCase
import io.zenandroid.greenfield.feed.FeedAction.*
import io.zenandroid.greenfield.repository.FlickrRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class FeedViewModelTest {

    private lateinit var viewModel: FeedViewModel
    private lateinit var repository: FlickrRepository
    private lateinit var loadImageListUseCase: LoadImageListUseCase

    private val dispatcher = TestCoroutineDispatcher()
    private val moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    private val fakeResponse: ImageListResponse = moshi.adapter(ImageListResponse::class.java)
        .fromJson(javaClass.classLoader!!.getResourceAsStream("mock_data.json").bufferedReader().readText())!!

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mock {
            onBlocking { getImageList(anyOrNull()) } doReturn fakeResponse
        }
        loadImageListUseCase = LoadImageListUseCase(repository)

        viewModel = FeedViewModel(loadImageListUseCase)
    }

    @Test
    fun whenStarting_ImagesAreLoaded() = runTest {
        verify(repository).getImageList(null)
        delay(2000)
        assertEquals(viewModel.state.value.images?.size, 19)
    }

    @Test
    fun whenSearch_ImagesAreLoaded() = runTest {
        viewModel.onEvent(SearchTextChanged("test search"))
        assertEquals(viewModel.state.value.searchText, "test search")
        viewModel.onEvent(SearchComplete)
        verify(repository).getImageList("test,search")
    }
}