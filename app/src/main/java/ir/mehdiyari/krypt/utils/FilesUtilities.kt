package ir.mehdiyari.krypt.utils

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesUtilities @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val KRYPT_FILES_PREFIX = "krypt_"
        const val KRYPT_THUMBS_FILES_PREFIX = "thumb_"
    }

    fun generateFilePathForPhotos(photoPath: String): String =
        "${getFilesDir()}/$KRYPT_FILES_PREFIX${System.currentTimeMillis()}.${
            photoPath.split(".").lastOrNull() ?: ".jpg"
        }"

    fun generateEncryptedFilePathForPhotosThumbnail(thumbnailPath: String): String =
        "${getFilesDir()}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".jpg"
        }"

    fun generateStableNameFilePathForPhotosThumbnail(thumbnailPath: String): String =
        "${getCashDir()}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".jpg"
        }"

    fun createThumbnailPath(path: String): String =
        "${getCashDir()}/${KRYPT_THUMBS_FILES_PREFIX}${
            getNameOfFile(path)
        }.jpg"

    fun getCashDir(): String = context.cacheDir.path

    fun getFilesDir(): String = context.filesDir.path

    fun getNameOfFile(path: String): String = try {
        path.substring(
            path.lastIndexOf("/") + 1,
            path.lastIndexOf(".")
        )
    } catch (t: Throwable) {
        File(path).nameWithoutExtension
    }

    fun generateDecryptedPhotoPathInKryptFolder(encryptedPhoto: String): String =
        "${Environment.getExternalStorageDirectory().path}/Krypt/Photos/".also {
            File(it).mkdirs()
        }.let {
            "$it${getNameOfFile(encryptedPhoto)}.${
                encryptedPhoto.split(".").lastOrNull() ?: ".jpg"
            }"
        }
}
