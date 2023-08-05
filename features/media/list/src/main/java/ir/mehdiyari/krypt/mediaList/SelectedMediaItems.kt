package ir.mehdiyari.krypt.mediaList

import ir.mehdiyari.krypt.mediaList.utils.formatSize
import java.io.File

data class SelectedMediaItems(
    val path: String,
    val isEncrypted: Boolean
) {
    fun getFileSize(): String = formatSize(File(this.path).length())
    fun getFileRealName(): String = try {
        File(this.path).name.let {
            it.ifBlank {
                "File"
            }
        }
    } catch (t: Throwable) {
        "File"
    }
}