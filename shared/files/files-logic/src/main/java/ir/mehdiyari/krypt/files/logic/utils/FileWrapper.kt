package ir.mehdiyari.krypt.files.logic.utils

import java.io.File
import javax.inject.Inject

class FileWrapper @Inject constructor() {
    fun delete(filePath: String) = File(filePath).delete()

    fun length(filePath: String) = File(filePath).length()
}