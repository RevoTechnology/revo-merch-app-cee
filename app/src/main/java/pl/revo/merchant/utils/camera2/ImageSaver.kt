package pl.revo.merchant.utils.camera2

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Saves a JPEG [Image] into the specified [File].
 */
internal class ImageSaver(
        /**
         * The JPEG image
         */
        private val image: Image,

        /**
         * The file we save the image into.
         */
        private val file: File,

        private val orientation: Int,

        private val onListener: OnListener<Boolean>
) : Runnable {

    override fun run() {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        val height = bitmap.height
        val width = bitmap.width
        var scale = if (height > width) height.toDouble() / 1920.0 else width.toDouble() / 1920.0
        if (scale < 2.0) scale = 2.0
        val scaledHeight = (height.toDouble() / scale).toInt()
        val scaledWidth = (width.toDouble() / scale).toInt()

        try {
            val sx = scaledWidth.toFloat() / width.toFloat()
            val sy = scaledHeight.toFloat() / height.toFloat()
            val matrix = Matrix()
            matrix.setScale(sx, sy)

            if (width > height && orientation == Configuration.ORIENTATION_PORTRAIT) {
                matrix.postRotate(90f)
            } else if (width < height && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                matrix.postRotate(90f)
            }

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file).apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
            onListener.on(false)
        } finally {
            onListener.on(true)
            image.close()
            output?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
        }
    }

    companion object {
        /**
         * Tag for the [Log].
         */
        private val TAG = "ImageSaver"
    }
}

interface OnListener<T> {
    fun on(arg: T)
}