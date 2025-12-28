package com.example.bookstore.network.model

import com.google.gson.annotations.SerializedName

data class OpenLibrarySearchResponse(
    @SerializedName("numFound") val numFound: Int,
    @SerializedName("docs") val docs: List<OpenLibraryBook>
)

data class OpenLibraryBook(
    @SerializedName("key") val key: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("author_name") val authorName: List<String>?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    @SerializedName("isbn") val isbn: List<String>?,
    @SerializedName("publisher") val publisher: List<String>?,
    @SerializedName("language") val language: List<String>?,
    @SerializedName("cover_i") val coverId: Long?,
    @SerializedName("number_of_pages_median") val numberOfPagesMedian: Int?,
    @SerializedName("subject") val subjects: List<String>? = emptyList()
)

data class OpenLibraryWorkResponse(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("covers") val covers: List<Long>?,
    @SerializedName("first_publish_date") val firstPublishDate: String?
)