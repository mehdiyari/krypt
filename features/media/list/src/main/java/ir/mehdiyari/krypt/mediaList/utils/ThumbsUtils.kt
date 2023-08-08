@file:JvmName("ThumbsUtils")

package ir.mehdiyari.krypt.mediaList.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject


internal class ThumbsUtils @Inject constructor() {

    private companion object {
        const val THUMB_WIDTH = 1024
    }

    fun createThumbnailFromPath(
        path: String,
        thumbPath: String
    ) {
        FileInputStream(path).use { fileInputStream ->
            val dimension = getPhotoDimension(path)
            val desiredWidth = if (dimension.first < THUMB_WIDTH) dimension.first else THUMB_WIDTH
            val imageStream = BitmapFactory.decodeStream(fileInputStream)
            val thumbBitmap = Bitmap.createScaledBitmap(
                imageStream,
                desiredWidth,
                (dimension.second / (dimension.first / desiredWidth)),
                false
            )
            FileOutputStream(thumbPath).use { fileOutputStream ->
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            }
        }
    }

    fun getPhotoDimension(path: String): Pair<Int, Int> = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, this)
    }.let {
        it.outWidth to it.outHeight
    }

    fun createVideoThumbnail(mediaPath: String, thumbnailPath: String) {
        ThumbnailUtils.createVideoThumbnail(
            mediaPath,
            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND
        ).apply {
            if (this != null) {
                try {
                    FileOutputStream(thumbnailPath).also { output ->
                        this.compress(Bitmap.CompressFormat.JPEG, 100, output)
                    }
                } catch (ignored: Throwable) {
                    ignored.printStackTrace()
                }
            }
        }
    }
}
