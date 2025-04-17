package com.trioscg.androidapp2

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ComicBookAdapter(
    private val comics: MutableList<ComicBook>,
    private val onItemClick: (ComicBook, Int) -> Unit
) : RecyclerView.Adapter<ComicBookAdapter.ComicViewHolder>() {

    // ViewHolder class for each comic item
    inner class ComicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val issueText: TextView = itemView.findViewById(R.id.issueText)
        val publisherText: TextView = itemView.findViewById(R.id.publisherText)
        val yearText: TextView = itemView.findViewById(R.id.yearText)
        val comicImage: ImageView = itemView.findViewById(R.id.comicImage)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(comics[position], position)
                }
            }
        }
    } // ComicViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicViewHolder {
        // Inflate item layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comic, parent, false)
        return ComicViewHolder(view)
    } // onCreateViewHolder()

    override fun onBindViewHolder(holder: ComicViewHolder, position: Int) {
        val comic = comics[position]
        holder.titleText.text = comic.title
        holder.issueText.text = buildString {
        append("Issue #")
        append(comic.issueNumber)
    }
        holder.publisherText.text = comic.publisher
        holder.yearText.text = comic.year

        // Load comic image from file if available
        if (!comic.imageUri.isNullOrEmpty()) {
            val imageFile = File(comic.imageUri)
            if (imageFile.exists()) {
                holder.comicImage.setImageURI(Uri.fromFile(imageFile))
            } else {
                holder.comicImage.setImageResource(R.drawable.baseline_image_24)
            }
        } else {
            holder.comicImage.setImageResource(R.drawable.baseline_image_24)
        }
    } // onBindViewHolder()

    override fun getItemCount(): Int = comics.size
} // ComicBookAdapter