package pl.revo.merchant.utils.camerax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ExifInterface.ORIENTATION_TRANSVERSE
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import pl.revo.merchant.utils.catchAll
import pl.revo.merchant.utils.save
import pl.revo.merchant.utils.scaleToMaxSize
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

object ImagexSaver {
    const val photodir = "camerax"

    fun onTakePicture(context: Context,
                      iFile: File,
                      onFileReady: (File) -> Unit,
                      onBitmapReady: (Bitmap) -> Unit,
                      onProgressChanged: ((Boolean) -> Unit)? = null,
                      onError: ((Throwable) -> Unit)? = null
    ) {
        onProgressChanged?.invoke(true)
        Single.just(iFile)
                .map {
                    val path = rotate(context, it.absolutePath) ?: throw Exception()
                    val result = File(path)
                    val bitmap = BitmapFactory.decodeFile(path)
                            .scaleToMaxSize()
                    bitmap.save(result)

                    onFileReady(result)
                    bitmap
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    onProgressChanged?.invoke(false)
                    onBitmapReady(it)
                }
                .doOnError {
                    it.printStackTrace()
                    onProgressChanged?.invoke(false)
                    onError?.invoke(it ?: Exception(""))
                }
                .subscribe()

    }

    private fun rotate(context: Context, localPath: String?): String? {
        if (localPath == null) return null

        val file = File(localPath)
        var fileStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        var bitmap: Bitmap? = null
        var path = file.absolutePath

        try {
            if (file.exists()) {
                fileStream = file.inputStream()

                val orientation = ImageHeaderParser(fileStream).orientation
                val imageOrientation = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    ORIENTATION_TRANSVERSE -> 270
                    else -> 0
                }.toFloat()

                if (orientation >= 0) {
                    val orientationMatrix = Matrix()
                    orientationMatrix.postRotate(imageOrientation)

                    val directory = File(context.cacheDir, photodir)
                    val rotatedFile = File(directory, UUID.randomUUID().toString().substring(0, 5) + ".jpg")
                    bitmap = BitmapFactory.decodeFile(localPath)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, orientationMatrix, false)
                    fileOutputStream = FileOutputStream(rotatedFile)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

                    path = rotatedFile.absolutePath
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            catchAll(action = { bitmap?.recycle() })
            catchAll(action = { fileStream?.close() })
            catchAll(action = { fileOutputStream?.close() })
        }
        return path
    }


}