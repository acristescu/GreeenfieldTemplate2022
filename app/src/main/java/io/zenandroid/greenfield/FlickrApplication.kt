package io.zenandroid.greenfield

import android.app.Application
import io.zenandroid.greenfield.di.allKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlickrApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FlickrApplication)

            modules(allKoinModules)
        }
    }
}