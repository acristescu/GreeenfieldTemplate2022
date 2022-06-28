package io.zenandroid.greenfield.data.model

import java.util.*

data class ImageListResponse (
    private val title: String? = null,
    private val link: String? = null,
    private val description: String? = null,
    private val modified: Date? = null,
    private val generator: String? = null,
    var items: List<Image>? = null
)
