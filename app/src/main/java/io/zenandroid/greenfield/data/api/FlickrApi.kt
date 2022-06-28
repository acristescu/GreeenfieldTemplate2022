package io.zenandroid.greenfield.data.api

import io.zenandroid.greenfield.data.model.ImageListResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * created by acristescu
 */

interface FlickrApi {

    @GET("services/feeds/photos_public.gne?format=json&nojsoncallback=1")
    suspend fun getPhotos(@Query("tags") tags: String?): ImageListResponse
}
