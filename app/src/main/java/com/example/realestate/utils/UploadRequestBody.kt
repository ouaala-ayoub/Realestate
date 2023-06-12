package com.example.realestate.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UploadRequestBody(
    private val file: File,
    private val contentType: String,
//    private val callback: UploadCallback
) : RequestBody() {

    override fun contentType() = "$contentType/*".toMediaTypeOrNull()

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {
                handler.post(ProgressUpdater(uploaded, length))
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }

    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
    }

    inner class ProgressUpdater(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
//            callback.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

suspend fun getImageRequestBody(
    uri: Uri,
    context: Context,
): FileInfo? {

    Log.d(TAG, "getImageRequestBody uri: $uri")

    val parcelFileDescriptor =
        context.contentResolver.openFileDescriptor(uri, "r", null) ?: return null

    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
    val file = File(context.cacheDir, context.contentResolver.getFileName(uri))
    val outputStream = withContext(Dispatchers.IO) {
        FileOutputStream(file)
    }
    inputStream.copyTo(outputStream)
    Log.d(TAG, "file size : ${file.fileSize()}KB")


//    val compressedImageFile = Compressor.compress(context, file, Dispatchers.Main)
//    Log.d(TAG, "compressedImageFile size : ${compressedImageFile.fileSize()}KB")

//    progress_bar.progress = 0
    val body = UploadRequestBody(file, "image")
    parcelFileDescriptor.close()

    return FileInfo(
        file.name,
        body
    )
}

fun File.fileSize(): Int {
    return java.lang.String.valueOf(this.length() / 1024).toInt()
}

class FileInfo(val fileName: String, val fileReqBody: RequestBody)