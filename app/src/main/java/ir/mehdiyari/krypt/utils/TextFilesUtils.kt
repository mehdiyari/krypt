package ir.mehdiyari.krypt.utils

import ir.mehdiyari.krypt.crypto.Base64
import ir.mehdiyari.krypt.crypto.FileCrypt
import ir.mehdiyari.krypt.crypto.toUtf8Bytes
import java.io.File
import javax.inject.Inject

class TextFilesUtils @Inject constructor(
    private val fileCrypt: FileCrypt,
    private val filesUtilities: FilesUtilities
) {

    fun mapTitleAndContentToFile(title: String, content: String): File {
        val textFilePath = filesUtilities.generateTextFileCachePath()
        return File(textFilePath).also {
            it.writeText("${title.replace("\n", "")}\n$content")
            it.createNewFile()
        }
    }

    fun encryptTextFiles(file: File): Pair<Boolean, String?> {
        val destinationPath = filesUtilities.generateTextFilePath()
        return if (fileCrypt.encryptFileToPath(file.path, destinationPath)) {
            true to destinationPath
        } else {
            false to null
        }
    }

    fun getEncryptedBase64MetaDataFromTitleAndContent(title: String, content: String): String? =
        try {
            val str = "${title.replace("\n", "")}\n${getFirst64CharacterOfContent(content)}"
            Base64.encodeBytes(fileCrypt.encryptDataWithIVAtFirst(str.toUtf8Bytes()))
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    fun decryptMetaData(string: String): Pair<String, String>? {
        val decryptedText = fileCrypt.decryptDataWithIVAtFirst(Base64.decode(string))
        decryptedText ?: return null
        var text = ""
        decryptedText.forEach {
            text += "${it.toInt().toChar()}"
        }

        val title = text.substring(0, text.indexOf("\n"))
        val content = text.substring(text.indexOf("\n") + 1, text.length - 1)
        return title to content
    }

    private fun getFirst64CharacterOfContent(content: String): String =
        if (content.trim().length > 64)
            content.substring(0, 63)
        else
            content

    fun decryptTextFile(filePath: String): Pair<String, String>? {
        val encryptedFile = File(filePath)
        if (!encryptedFile.exists()) {
            return null
        }

        return try {
            val fileContentByteArray = encryptedFile.readBytes()
            val decryptedByteArray = fileCrypt.decryptDataWithIVAtFirst(fileContentByteArray)
            var text = ""
            decryptedByteArray!!.forEach {
                text += "${it.toInt().toChar()}"
            }

            val title = text.substring(0, text.indexOf("\n"))
            val content = text.substring(text.indexOf("\n") + 1, text.length - 1)

            title to content
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}