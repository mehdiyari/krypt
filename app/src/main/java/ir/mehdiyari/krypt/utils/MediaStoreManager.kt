package ir.mehdiyari.krypt.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class MediaStoreManager @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {

    fun deleteFilesFromExternalStorageAndMediaStore(files: List<String>) {
        val filesFile = files.map { File(it) }
        filesFile.forEach { it.delete() }
        internalScan(filesFile)
    }

    private fun internalScan(files: List<File>) {
        MediaScannerConnection.scanFile(
            applicationContext,
            files.map { it.path }.toTypedArray(),
            files.map {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.extension)
            }.toTypedArray(),
            null
        )
    }

    fun scanAddedMedia(medias: List<String>) {
        internalScan(medias.map { File(it) })
    }

}
