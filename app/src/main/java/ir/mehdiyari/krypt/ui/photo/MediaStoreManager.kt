package ir.mehdiyari.krypt.ui.photo

import android.content.Context
import android.media.MediaScannerConnection
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class MediaStoreManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    suspend fun deleteFilesFromExternalStorageAndMediaStore(files: List<String>) {
        val filesFile = files.map { File(it) }
        filesFile.forEach { it.delete() }
        MediaScannerConnection.scanFile(
            applicationContext,
            files.toTypedArray(),
            filesFile.map {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.extension)
            }.toTypedArray(),
            null
        )
    }

}
