package com.trioscg.androidapp2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AddEditComicActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var savedImagePath: String? = null
    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_REQUEST = 1001

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
                imageView.setImageURI(Uri.fromFile(File(savedImagePath)))
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
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                try {
                    //  Open an input stream from the selected image URI (from gallery)
                    val inputStream = contentResolver.openInputStream(uri)

                    //  Generate a unique file name using timestamp
                    val fileName = "comic_${System.currentTimeMillis()}.jpg"

                    //  Create a file in the app's internal storage directory (filesDir)
                    val file = File(filesDir, fileName)

                    //  Open an output stream to the internal file
                    val outputStream = file.outputStream()

                    //  Copy the image data from the gallery to internal storage
                    inputStream?.copyTo(outputStream)

                    //  Close both input and output streams
                    inputStream?.close()
                    outputStream.close()

                    //  Save the local path of the stored image for later use (e.g. preview or saving to comic)
                    savedImagePath = file.absolutePath

                    //  Show a preview of the saved image in the ImageView
                    imageView.setImageURI(Uri.fromFile(file))
                } catch (e: Exception) {
                    e.printStackTrace()  // Log any errors that occur during image saving
                }
            }
        }
    } // onActivityResult()
} // AddEditComicActivity