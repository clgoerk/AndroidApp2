package com.trioscg.androidapp2

data class ComicBook(
    val title: String,
    val issueNumber: String,
    val publisher: String,
    val year: String,
    val imageUri: String? = null
)