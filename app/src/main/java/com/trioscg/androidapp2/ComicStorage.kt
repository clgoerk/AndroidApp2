package com.trioscg.androidapp2

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object ComicStorage {
    private const val PREFS_NAME = "ComicPrefs"
    private const val COMIC_LIST_KEY = "comicList"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    } // getPrefs()

    fun saveComics(context: Context, comics: List<ComicBook>) {
        val json = Gson().toJson(comics)
        getPrefs(context).edit() { putString(COMIC_LIST_KEY, json) }
    } // saveComics()

    fun loadComics(context: Context): List<ComicBook> {
        val json = getPrefs(context).getString(COMIC_LIST_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<ComicBook>>() {}.type
        return Gson().fromJson(json, type)
    } // loadComics()
} // ComicStorage