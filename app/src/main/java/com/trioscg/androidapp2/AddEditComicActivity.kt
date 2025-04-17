package com.trioscg.androidapp2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AddEditComicActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var savedImagePath: String? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val uri = data?.data
            uri?.let {
                try {
                    // Open an input stream from the selected image URI
                    val inputStream = contentResolver.openInputStream(it)

                    // Generate a unique file name using the current timestamp for the image
                    val fileName = "comic_${System.currentTimeMillis()}.jpg"
                    val file = File(filesDir, fileName)

                    // Create an output stream to write the image to the app's internal storage
                    val outputStream = file.outputStream()

                    // Copy the image data from the URI to the internal storage file
                    inputStream?.copyTo(outputStream)

                    // Close both input and output streams after copying the image data
                    inputStream?.close()
                    outputStream.close()

                    // Save the absolute path of the saved image for later use
                    savedImagePath = file.absolutePath

                    // Display the saved image in the ImageView
                    imageView.setImageURI(Uri.fromFile(file))
                } catch (e: Exception) {
                    // Log any errors that occur during the image saving process
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_comic)

        imageView = findViewById(R.id.imageView)
        val titleInput = findViewById<EditText>(R.id.input_title)
        val issueInput = findViewById<EditText>(R.id.input_issue)
        val publisherInput = findViewById<EditText>(R.id.input_publisher)
        val yearInput = findViewById<EditText>(R.id.input_year)

        val isEdit = intent.getBooleanExtra("isEdit", false)
        val editIndex = intent.getIntExtra("index", -1)

        val deleteButton = findViewById<Button>(R.id.button_delete)
        val saveButton = findViewById<Button>(R.id.button_save)
        val cancelButton = findViewById<Button>(R.id.button_cancel)

        if (isEdit) {
            // Pre-fill input fields with existing comic data
            titleInput.setText(intent.getStringExtra("title"))
            issueInput.setText(intent.getStringExtra("issueNumber"))
            publisherInput.setText(intent.getStringExtra("publisher"))
            yearInput.setText(intent.getStringExtra("year"))

            // Load existing comic image if available
            val existingImagePath = intent.getStringExtra("imageUri")
            if (!existingImagePath.isNullOrEmpty()) {
                savedImagePath = existingImagePath
                savedImagePath?.let { path ->
                    val file = File(path)
                    imageView.setImageURI(Uri.fromFile(file))
                }
            }

            // Show Delete button when editing
            deleteButton.visibility = View.VISIBLE
        } else {
            // Hide Delete button when adding a new comic
            deleteButton.visibility = View.GONE
        }

        // Delete comic action (for editing)
        deleteButton.setOnClickListener {
            // Delete image file if it exists
            savedImagePath?.let {
                val imageFile = File(it)
                if (imageFile.exists()) {
                    imageFile.delete()  // Remove the image from internal storage
                }
            }

            val resultIntent = Intent().apply {
                putExtra("isDelete", true)  // Mark as delete action
                putExtra("index", editIndex)  // Pass the index of the comic to delete
            }
            setResult(RESULT_OK, resultIntent)
            finish()  // Close the activity after deletion
        }

        // Open image picker when clicking the image view
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)  // Launch the image picker using the new API
        }

        // Save comic action (add or edit)
        saveButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("title", titleInput.text.toString())
                putExtra("issueNumber", issueInput.text.toString())
                putExtra("publisher", publisherInput.text.toString())
                putExtra("year", yearInput.text.toString())
                putExtra("isEdit", isEdit)
                putExtra("index", editIndex)
                putExtra("imageUri", savedImagePath)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Cancel action
        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    } // onCreate()
} // AddEditComicActivity