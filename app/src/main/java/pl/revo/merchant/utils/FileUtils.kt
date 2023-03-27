package pl.revo.merchant.utils

import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.caverock.androidsvg.SVG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.R
import pl.revo.merchant.api.HttpConfig
import java.io.*
import java.nio.charset.Charset
import kotlin.math.ceil

val Context.photoDir
    get() = File(cacheDir, "photo")

fun getFilePathUri(context: Context?, file: File?): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", file!!)
    else
        Uri.fromFile(file)
}

fun createImage(context: Context, fileName: String): File {
    val storageDir = context.cacheDir
    return File.createTempFile(fileName, ".png", storageDir)
}

fun createImageFile(context: Context, fileName: String): Uri? {
    return getFilePathUri(context, createImage(context, fileName))
}

fun saveBitmapToCash(context: Context, bitmap: Bitmap?, name: String): String {
    return if (bitmap != null) {
        val file = File.createTempFile(name, null, context.cacheDir)
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
        out.flush()
        out.close()
        file.path
    } else {
        ""
    }
}

fun createPart(name: String, bitmapPath: String?): MultipartBody.Part? {
    return if (bitmapPath.isNullOrEmpty()) null
    else {
        val file = File(bitmapPath)
        val request = RequestBody.create("image/png".toMediaTypeOrNull(), file)
        MultipartBody.Part.createFormData(name, bitmapPath, request)
    }
}

fun createPart(imageName: String, imageFile: File?): MultipartBody.Part? {
    return if (imageFile == null) null
    else {
        val request = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        MultipartBody.Part.createFormData(imageName, imageFile.name, request)
    }
}

fun writeResponseBodyToDisk(context: Context, body: ResponseBody?): Uri {
    try {
        val destinationFile = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), HttpConfig.APK_DESTINATION)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)

            var fileSizeDownloaded: Long = 0

            inputStream = body?.byteStream()
            outputStream = FileOutputStream(destinationFile)

            while (true) {
                val read = inputStream?.read(fileReader)

                if (read == -1 || read == null) {
                    break
                }

                outputStream.write(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()
            }

            outputStream.flush()

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.addCompletedDownload(
                    destinationFile.name,
                    destinationFile.name,
                    true,
                    "application/json",
                    destinationFile.absolutePath,
                    destinationFile.length(),
                    true
            )

            return getFilePathUri(context, destinationFile)
        } catch (e: IOException) {
            return Uri.parse("")
        } finally {
            if (inputStream != null) {
                inputStream.close()
            }

            if (outputStream != null) {
                outputStream.close()
            }
        }
    } catch (e: IOException) {
        return Uri.parse("")
    }
}

fun clearGallery(context: Context) {
    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    if (picturesDir.isDirectory) {
        val files = picturesDir.listFiles()
        if (files != null && files.isNotEmpty()) files.forEach { it.delete() }
    }
    val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
    if (dcimDir.isDirectory) {
        val cameraDir = File(dcimDir.absolutePath + "/Camera")
        if (cameraDir.exists()) {
            if (cameraDir.isDirectory) {
                val files = cameraDir.listFiles()
                if (files != null && files.isNotEmpty())
                    files.forEach {
                        if (!it.isDirectory) {
                            deleteImageFromMedia(context, it.absolutePath)
                        }
                    }
            }
        }
    }
}

private fun deleteImageFromMedia(context: Context, filename: String): Boolean {
    val projection = arrayOf(MediaStore.Files.FileColumns._ID)
    val selection = MediaStore.Files.FileColumns.DATA + " = ?"
    val selectionArgs = arrayOf(filename)
    val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val contentResolver = context.contentResolver
    val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)
    return if (c != null && c.moveToFirst()) {
        val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
        val deleteUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
        contentResolver.delete(deleteUri, null, null)
        c.close()
        true
    } else {
        c?.close()
        File(filename).delete()
        false
    }
}

fun Fragment.takePhotoFromCamera(requestId: Int, fileName: String): File? {
    val photoFile = createImage(this.requireContext(), fileName + "_")
    photoFile.let {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFilePathUri(this.context, photoFile))
        this.startActivityForResult(
                Intent.createChooser(
                        cameraIntent,
                        getString(R.string.documents_pick_photo_title)
                ), requestId
        )
    }
    return photoFile
}

fun String?.base64ToImage(): Bitmap? {
    if (this != null) {
        val split = this.split(",")
        if (split.size == 2) {
            val decodedString = Base64.decode(split[1], Base64.DEFAULT)
            val svgString = String(decodedString, Charset.defaultCharset())
            try {
                val svg = SVG.getFromString(svgString)
                if (svg.documentWidth != -1.0f) {
                    val bitmap = Bitmap.createBitmap(
                            ceil(svg.documentWidth.toDouble()).toInt(),
                            ceil(svg.documentHeight.toDouble()).toInt(),
                            Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawRGB(255, 255, 255)
                    svg.renderToCanvas(canvas)
                    return bitmap
                }
            } catch (e: Exception) {

            }
        }
    }
    return null
}