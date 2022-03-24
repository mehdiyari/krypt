package ir.mehdiyari.krypt.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePathGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CRYPT_FILES_PREFIX = "krypt_"
    }

    fun generateFilePathForPhotos(photoPath: String): String =
        "${context.cacheDir.path}/$CRYPT_FILES_PREFIX${System.currentTimeMillis()}.${
            photoPath.split(".").lastOrNull() ?: ".jpg"
        }"

    fun generateFilePathForPhotosThumbnail(thumbnailPath: String): String =
        "${context.cacheDir.path}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".jpg"
        }"

    fun getCashDir(): String = context.cacheDir.path

    fun getNameOfFile(path: String): String = path.substring(
        path.lastIndexOf("/") + 1,
        path.lastIndexOf(".")
    )
}
