package com.mylittleproject.love42.tools

import android.content.Context
import android.net.Uri
import android.util.Log
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileOutputStream

class ImageCompressor(private val context: Context) {

    suspend fun compressImage(fileUri: Uri): File? {
        val file = getFileFromUri(fileUri)
        return if (file != null) {
            Compressor.compress(context, file, Dispatchers.Default)
        } else {
            null
        }
    }


    private fun getFileFromUri(fileUri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        return try {
            val file = File(context.cacheDir, "cacheFileAppeal.srl")
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                if (inputStream != null) {
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                }
                output.flush()
                file
            }
        } catch (e: Exception) {
            Log.w(NAME_TAG, "File from uri failed", e)
            null
        } finally {
            inputStream?.close()
        }
    }
}