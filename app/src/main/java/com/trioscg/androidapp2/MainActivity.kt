package com.trioscg.androidapp2

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ComicBookAdapter
    private val comicList = mutableListOf<ComicBook>()
    private val addEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val isDelete = result.data?.getBooleanExtra("isDelete", false) ?: false

            if (isDelete) {
                val index = result.data?.getIntExtra("index", -1) ?: -1
                if (index >= 0 && index < comicList.size) {
                    comicList.removeAt(index)  // Remove the comic from the list
                    adapter.notifyItemRemoved(index)  // Update the RecyclerView
                    ComicStorage.saveComics(this, comicList)  // Save the updated list
                }
                return@registerForActivityResult
            }

            val title = result.data?.getStringExtra("title") ?: return@registerForActivityResult
            val issueNumber = result.data?.getStringExtra("issueNumber") ?: return@registerForActivityResult
            val publisher = result.data?.getStringExtra("publisher") ?: return@registerForActivityResult
            val year = result.data?.getStringExtra("year") ?: return@registerForActivityResult
            val isEdit = result.data?.getBooleanExtra("isEdit", false) ?: false
            val index = result.data?.getIntExtra("index", -1) ?: -1
            val imageUri = result.data?.getStringExtra("imageUri")

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
    }

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
            // Launch the AddEditComicActivity using the ActivityResultLauncher
            addEditLauncher.launch(intent)
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Floating Action Button to add new comic
        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            val intent = Intent(this, AddEditComicActivity::class.java)
            // Launch the AddEditComicActivity using the ActivityResultLauncher
            addEditLauncher.launch(intent)
        }
    } // onCreate()
} // MainActivity