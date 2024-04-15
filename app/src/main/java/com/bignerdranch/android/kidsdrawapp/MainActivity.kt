package com.bignerdranch.android.kidsdrawapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var buttonAdd: Button
    private lateinit var buttonSave: Button
    private lateinit var buttonBlack: Button
    private lateinit var buttonBlue: Button
    private lateinit var buttonRed: Button
    private lateinit var buttonGreen: Button
    private lateinit var buttonSend: Button
    private lateinit var viewModel: DrawViewModel
    private lateinit var editText: EditText
    private var sizePaint : Array<Float> = arrayOf<Float>(20f, 10f, 5f)
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawView)
        buttonAdd = findViewById(R.id.buttonAddPhoto)
        buttonSave = findViewById(R.id.buttonSave)
        editText = findViewById(R.id.editTextText)

        buttonBlack = findViewById(R.id.buttonBlack)
        buttonBlue = findViewById(R.id.buttonBlue)
        buttonRed = findViewById(R.id.buttonRed)
        buttonGreen = findViewById(R.id.buttonGreen)

        buttonSend = findViewById(R.id.buttonShare)

        val arrayAdapter = ArrayAdapter<Float>(this, android.R.layout.simple_spinner_item, sizePaint)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner = findViewById(R.id.spinner)
        spinner.adapter = arrayAdapter

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val size = parent.getItemAtPosition(position).toString().toFloat()

                viewModel.setBrushSize(size)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[DrawViewModel::class.java]

        viewModel.setDrawingView(drawingView)

        buttonAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(intent, 1)
        }

        buttonSave.setOnClickListener {
            val bitmap = viewModel.getBitMap()

            if (editText.text.toString() != "") {
                if (bitmap != null) {
                    saveTheImageLegacyStyle(bitmap, editText.text.toString())
                }
            }
        }

        buttonSend.setOnClickListener() {
            sendImage(viewModel.getBitMap())
        }

        buttonBlack.setOnClickListener() {
            viewModel.setColor(Color.BLACK)
        }

        buttonRed.setOnClickListener() {
            viewModel.setColor(Color.RED)
        }

        buttonBlue.setOnClickListener() {
            viewModel.setColor(Color.BLUE)
        }

        buttonGreen.setOnClickListener() {
            viewModel.setColor(Color.GREEN)
        }

        viewModel.drawingView.observe(this, Observer {
            drawingView = it
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            viewModel.setBackGround(bitmap)
        }
    }

    fun saveTheImageLegacyStyle(bitmap: Bitmap, fileName: String){
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        if (!folder.exists()) {
            folder.mkdir()
        }

        val cachePath = File(folder, "$fileName.png")

        try {
            val ostream = FileOutputStream(cachePath)
            bitmap.compress(CompressFormat.PNG, 100, ostream)
            ostream.close()

            Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun sendImage(bitmap: Bitmap?) {
        val filePath = "${externalCacheDir?.absolutePath}/temp_image.png"

        try {
            val fileOutputStream = FileOutputStream(filePath)
            bitmap?.compress(CompressFormat.PNG, 100, fileOutputStream)

            val file = File(filePath)
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Поделиться рисунком"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}



