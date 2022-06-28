package io.zenandroid.greenfield.repository

import io.zenandroid.greenfield.data.api.FlickrApi
import io.zenandroid.greenfield.data.model.ImageListResponse

class FlickrRepository(
        private val flickrApi: FlickrApi
) {
    suspend fun getImageList(tags: String?): ImageListResponse =
            flickrApi.getPhotos(tags)
}