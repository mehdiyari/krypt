package ir.mehdiyari.krypt.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.net.URLDecoder


val isExternalStorageDoc: ((authority: String) -> Boolean) =
    { authority -> "com.android.externalstorage.documents" == authority }
val isDownloadsDoc: ((authority: String) -> Boolean) =
    { authority -> "com.android.providers.downloads.documents" == authority }
val isMediaDoc: ((authority: String) -> Boolean) =
    { authority -> "com.android.providers.media.documents" == authority }

/**
 * get realPath from mediaStore for [uri].
 */
fun Context.getRealPathBasedOnUri(uri: Uri): String? =
    getRealUriIfUriFromPhotosContentProvider(uri).let { newUri ->
        try {
            when {
                DocumentsContract.isDocumentUri(this, newUri) && newUri.authority != null -> {
                    when {
                        isExternalStorageDoc(newUri.authority!!) -> {
                            DocumentsContract.getDocumentId(newUri).split(":").let { split ->
                                if ("primary" == split.firstOrNull() && split.getOrNull(1) != null)
                                    "${Environment.getExternalStorageDirectory()}/${split[1]}"
                                else
                                    null
                            }
                        }
                        isDownloadsDoc(newUri.authority!!) -> this.contentResolver.getPathOfFileInMediaStore(
                            uri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                DocumentsContract.getDocumentId(newUri).toLong()
                            )
                        )
                        isMediaDoc(newUri.authority!!) -> DocumentsContract.getDocumentId(newUri)
                            .split(":").let { split ->
                            this.contentResolver.getPathOfFileInMediaStore(
                                uri = getContentUriBasedOnType(split.firstOrNull()),
                                selections = "_id=?",
                                selectionArgs = arrayOf(split.getOrElse(1) { "" })
                            )
                        }
                        else -> null
                    }
                }
                "content" == newUri.scheme -> this.contentResolver.getPathOfFileInMediaStore(uri = newUri)
                "file" == newUri.scheme -> newUri.path
                else -> null
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

fun getContentUriBasedOnType(type: String?): Uri? = when (type) {
    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    else -> null
}


fun getRealUriIfUriFromPhotosContentProvider(uri: Uri): Uri =
    if (uri.toString().contains("com.google.android.apps.photos.contentprovider")) {
        try {
            uri.toString().split("/1/").firstOrNull().let { firstExtraction ->
                if (firstExtraction == null) {
                    uri
                } else {
                    val index = firstExtraction.indexOf("/ACTUAL")
                    if (index != -1)
                        Uri.parse(URLDecoder.decode(firstExtraction.substring(0, index), "UTF-8"))
                    else
                        uri
                }
            }

        } catch (ignored: Throwable) {
            ignored.printStackTrace()
            uri
        }
    } else uri

fun ContentResolver.getPathOfFileInMediaStore(
    uri: Uri?,
    selections: String? = null,
    selectionArgs: Array<String>? = null
): String? {
    if (uri != null)
        this.query(uri, arrayOf("_data"), selections, selectionArgs, null).use {
            if (it != null && it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow("_data"))?.let { path ->
                    if (path.startsWith("content://") || !path.startsWith("/") && !path.startsWith("file://"))
                        return null
                    else
                        path
                }
            } else
                return null
        }
    else
        return null
}