package io.zenandroid.greenfield.di

import io.zenandroid.greenfield.data.network.buildFlickrApi
import io.zenandroid.greenfield.data.network.buildMoshi
import io.zenandroid.greenfield.data.network.buildOKHttpConnection
import io.zenandroid.greenfield.domain.LoadImageListUseCase
import io.zenandroid.greenfield.feed.FeedViewModel
import io.zenandroid.greenfield.repository.FlickrRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


private val serverConnectionModule = module {

    single { buildMoshi() }
    single { buildOKHttpConnection() }
    single { buildFlickrApi(get(), get()) }

}

private val feedFeatureModule = module {
    single { FlickrRepository(get()) }
    factory { LoadImageListUseCase(get()) }
    viewModel { FeedViewModel(get()) }
}

val allKoinModules = listOf(
    serverConnectionModule,
    feedFeatureModule
)