@file:JvmName("ThumbsUtils")

package ir.mehdiyari.krypt.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject


class ThumbsUtils @Inject constructor() {

    fun createThumbnailFromPath(
        path: String,
        thumbPath: String
    ) {
        FileInputStream(path).use { fileInputStream ->
            val dimension = getPhotoDimension(path)
            val imageStream = BitmapFactory.decodeStream(fileInputStream)
            val thumbBitmap = Bitmap.createScaledBitmap(
                imageStream,
                356,
                (dimension.second / (dimension.first / 356)),
                false
            )
            FileOutputStream(thumbPath).use { fileOutputStream ->
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)
            }
        }
    }

    fun getPhotoDimension(path: String): Pair<Int, Int> = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, this)
    }.let {
        it.outWidth to it.outHeight
    }
}
