package ir.mehdiyari.krypt.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
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
        const val KRYPT_EXT = "krp"
    }

    fun generateFilePathForMedia(photoPath: String): String =
        "${getFilesDir()}/$KRYPT_FILES_PREFIX${System.currentTimeMillis()}.${
            photoPath.split(".").lastOrNull() ?: ".${KRYPT_EXT}"
        }"

    fun generateEncryptedFilePathForMediaThumbnail(thumbnailPath: String): String =
        "${getFilesDir()}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".${KRYPT_EXT}"
        }"

    fun generateStableNameFilePathForMediaThumbnail(thumbnailPath: String): String =
        "${getCashDir()}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".${KRYPT_EXT}"
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

    fun generateDecryptedPhotoMediaInKryptFolder(encryptedPhoto: String): String =
        "${Environment.getExternalStorageDirectory().path}/Krypt/Photos/".also {
            File(it).mkdirs()
        }.let {
            "$it${getNameOfFile(encryptedPhoto)}.${
                encryptedPhoto.split(".").lastOrNull() ?: ".jpg"
            }"
        }

    fun generateDecryptedVideoMediaInKryptFolder(encryptedVideo: String): String =
        "${Environment.getExternalStorageDirectory().path}/Krypt/Videos/".also {
            File(it).mkdirs()
        }.let {
            "$it${getNameOfFile(encryptedVideo)}.${
                encryptedVideo.split(".").lastOrNull() ?: ".mp4"
            }"
        }

    fun generateTextFilePath(): String =
        "${getFilesDir()}/${KRYPT_FILES_PREFIX}file_${System.currentTimeMillis()}.${KRYPT_EXT}"

    fun generateTextFileCachePath(): String =
        "${getCashDir()}/${KRYPT_FILES_PREFIX}file_${System.currentTimeMillis()}"

    @SuppressLint("Range")
    fun isPhotoPath(path: String): Boolean {
        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.DATA),
            """${MediaStore.Files.FileColumns.DATA}=?""",
            arrayOf(path), null
        )

        if (cursor?.moveToFirst() == true) {
            val type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE))
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                return true
            } else if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                return false
            }
        }

        cursor?.close()
        return true
    }

    fun deleteCacheDir() {
        File(getCashDir()).deleteRecursively()
    }
}
