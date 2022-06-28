package io.zenandroid.greenfield.data.model

import com.squareup.moshi.Json
import java.util.*

data class Image (
    val title: String? = null,
    val link: String? = null,
    val media: MediaLink? = null,
    @Json(name = "date_taken")
    val dateTaken: Date? = null,
    val description: String? = null,
    val published: Date? = null,
    val author: String? = null,
    val authorId: String? = null,
    val tags: String? = null
)
