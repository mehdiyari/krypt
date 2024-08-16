package ir.mehdiyari.krypt.files.logic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesUtilities @Inject constructor(
    @ApplicationContext private val context: Context,
    @DispatchersType(dispatcher = DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    companion object {
        const val KRYPT_FILES_PREFIX = "krypt_"
        const val KRYPT_THUMBS_FILES_PREFIX = "thumb_"
        const val KRYPT_EXT = "krp"

        const val DEFAULT_PHOTO_EXT = "jpg"
        const val DEFAULT_VIDEO_EXT = "mp4"
        const val VIDEO_CACHE_FOLDER = "3xP"
        const val AUDIO_CACHE_FOLDER = "4xP"
        const val AUDIO_EXT = "amr"

        val photoExtensions = listOf(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "bmp",
            "webp",
            "tiff",
            "raw",
            "svg"
        )

        val videoExtensions = listOf(
            "mp4",
            "mov",
            "avi",
            "wmv",
            "mkv",
            "flv",
            "webm",
            "m4v",
            "mpeg",
            "3gp",
            "ogv"
        )
    }

    fun generateFilePathForMedia(
        mediaPath: String,
        isPhoto: Boolean = true
    ): String =
        "${getFilesDir()}/$KRYPT_FILES_PREFIX${System.nanoTime()}.${
            mediaPath.getExtension().let {
                it.ifBlank {
                    if (isPhoto) DEFAULT_PHOTO_EXT else DEFAULT_VIDEO_EXT
                }
            }
        }"

    fun generateEncryptedFilePathForMediaThumbnail(thumbnailPath: String): String =
        "${getFilesDir()}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".$KRYPT_EXT"
        }"

    fun generateStableNameFilePathForMediaThumbnail(thumbnailPath: String): String =
        "${getCashDir()}/${getNameOfFile(thumbnailPath)}.${
            thumbnailPath.split(".").lastOrNull() ?: ".$KRYPT_EXT"
        }"

    fun createThumbnailPath(path: String): String =
        "${getCashDir()}/$KRYPT_THUMBS_FILES_PREFIX${
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

    fun getNameOfFileWithExtension(path: String): String = try {
        path.substring(
            path.lastIndexOf("/") + 1
        )
    } catch (t: Throwable) {
        File(path).name
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

    fun generateBackupFileInKryptFolder(): String =
        "${Environment.getExternalStorageDirectory().path}/Krypt/Backups/".also {
            File(it).mkdirs()
        }

    fun generateTextFilePath(): String =
        "${getFilesDir()}/${KRYPT_FILES_PREFIX}file_${System.currentTimeMillis()}.$KRYPT_EXT"

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
            return type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        }

        cursor?.close()
        return photoExtensions.contains(File(path).extension)
    }

    @SuppressLint("Range")
    fun isVideoPath(path: String): Boolean {
        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.DATA),
            """${MediaStore.Files.FileColumns.DATA}=?""",
            arrayOf(path), null
        )

        if (cursor?.moveToFirst() == true) {
            val type = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE))
            return type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
        }

        cursor?.close()
        return videoExtensions.contains(File(path).extension)
    }

    fun getPathFromUri(uri: Uri): String? = context.getRealPathBasedOnUri(uri)

    fun deleteCacheDir() {
        File(getCashDir()).deleteRecursively()
    }

    fun generateBackupFilePath(accountName: String): String =
        "${getFilesDir()}/krypt_backup_${accountName}_${System.currentTimeMillis()}_${Random().nextInt()}.$KRYPT_EXT"

    fun generateBackupFilePath(directoryPath: String, accountName: String): String =
        "${directoryPath}/krypt_backup_${accountName}_${System.currentTimeMillis()}_${Random().nextInt()}.$KRYPT_EXT"
    fun generateRestoreFilePath(): String =
        "${getFilesDir()}/krypt_restored_${System.currentTimeMillis()}_${Random().nextInt()}.$KRYPT_EXT"

    fun x(path: String) {
        val file = DocumentFile.fromFile(File(path))

    }

    fun copyBackupFileToKryptFolder(backupFilePath: String): String {
        return File(backupFilePath).let {
            if (!it.exists()) {
                throw FileNotFoundException("copyBackupFileToKryptFolder: Backup file not found")
            }

            val newBackup = File(
                generateBackupFileInKryptFolder(), it.name
            )

            it.copyTo(
                newBackup, overwrite = true
            )

            newBackup.path
        }
    }

    fun generateCacheVideoPath(name: String): String =
        "${getFilesDir()}/$VIDEO_CACHE_FOLDER/".apply {
            File(this).mkdirs()
        } + name

    fun deleteCachedVideoDIR() {
        try {
            File("${getFilesDir()}/$VIDEO_CACHE_FOLDER").deleteRecursively()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun deleteCachedAudioDIR() {
        try {
            File("${getFilesDir()}/$AUDIO_CACHE_FOLDER").deleteRecursively()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun String.getExtension(): String = try {
        if (this.isNotBlank()) {
            this.substring(this.lastIndexOf('.') + 1, this.length)
        } else {
            ""
        }
    } catch (t: Throwable) {
        ""
    }

    fun getStableEncryptedThumbPathForDecryptedThumb(fileName: String): String =
        "${getFilesDir()}/$fileName"

    fun getFilePathForVoiceRecord(): String = "${getCashDir()}/v_${System.nanoTime()}.enc"

    fun getRealFilePathForRecordedVoice(): String =
        "${getFilesDir()}/v_${System.nanoTime()}.$AUDIO_EXT"

    fun generateCacheAudioPath(name: String): String =
        "${getFilesDir()}/$AUDIO_CACHE_FOLDER/".apply {
            File(this).mkdirs()
        } + name

    suspend fun deleteFiles(vararg path: String) {
        withContext(ioDispatcher) {
            for (filePath in path) {
                File(filePath).delete()
            }
        }
    }
}
