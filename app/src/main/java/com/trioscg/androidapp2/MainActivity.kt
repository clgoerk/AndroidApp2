package com.trioscg.androidapp2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ComicBookAdapter
    private val comicList = mutableListOf<ComicBook>()
    private val ADD_EDIT_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load comics from storage
        comicList.addAll(ComicStorage.loadComics(this))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Initialize the adapter with click actions
        adapter = ComicBookAdapter(
            comicList
        ) { comic, index ->
            val intent = Intent(this, AddEditComicActivity::class.java).apply {
                putExtra("isEdit", true)
                putExtra("title", comic.title)
                putExtra("issueNumber", comic.issueNumber)
                putExtra("publisher", comic.publisher)
                putExtra("year", comic.year)
                putExtra("index", index)
                putExtra("imageUri", comic.imageUri)
            }
            startActivityForResult(intent, ADD_EDIT_REQUEST)
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Floating Action Button to add new comic
        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            val intent = Intent(this, AddEditComicActivity::class.java)
            startActivityForResult(intent, ADD_EDIT_REQUEST)
        }
    } // onCreate()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_EDIT_REQUEST && resultCode == RESULT_OK && data != null) {
            val isDelete = data.getBooleanExtra("isDelete", false)

            if (isDelete) {
                val index = data.getIntExtra("index", -1)
                if (index >= 0 && index < comicList.size) {
                    comicList.removeAt(index)  // Remove the comic from the list
                    adapter.notifyItemRemoved(index)  // Update the RecyclerView
                    ComicStorage.saveComics(this, comicList)  // Save the updated list
                }
                return
            }

            val title = data.getStringExtra("title") ?: return
            val issueNumber = data.getStringExtra("issueNumber") ?: return
            val publisher = data.getStringExtra("publisher") ?: return
            val year = data.getStringExtra("year") ?: return
            val isEdit = data.getBooleanExtra("isEdit", false)
            val index = data.getIntExtra("index", -1)
            val imageUri = data.getStringExtra("imageUri")

            val comic = ComicBook(title, issueNumber, publisher, year, imageUri)

            if (isEdit && index >= 0) {
                comicList[index] = comic  // Edit the existing comic
                adapter.notifyItemChanged(index)  // Update the RecyclerView
            } else {
                comicList.add(comic)  // Add a new comic
                adapter.notifyItemInserted(comicList.size - 1)  // Update the RecyclerView
            }

            ComicStorage.saveComics(this, comicList)  // Save the updated list
        }
    } // onActivityResult()
} // MainActivity