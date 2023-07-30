package ir.mehdiyari.krypt.ui.text.utils

import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.cryptography.utils.Base64
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.cryptography.utils.combineWith
import ir.mehdiyari.krypt.cryptography.utils.getAfterIndex
import ir.mehdiyari.krypt.cryptography.utils.getBeforeIndex
import ir.mehdiyari.krypt.cryptography.utils.toUtf8Bytes
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import java.io.File
import javax.inject.Inject

class TextFilesUtils @Inject constructor(
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val filesUtilities: FilesUtilities,
    private val symmetricHelper: SymmetricHelper,
) {

    fun mapTitleAndContentToFile(title: String, content: String): File {
        val textFilePath = filesUtilities.generateTextFileCachePath()
        return File(textFilePath).also {
            it.writeText("${title.replace("\n", "")}\n$content")
            it.createNewFile()
        }
    }

    suspend fun encryptTextFiles(file: File): Pair<Boolean, String?> {
        val destinationPath = filesUtilities.generateTextFilePath()
        return if (kryptCryptographyHelper.encryptFile(file.path, destinationPath).isSuccess) {
            true to destinationPath
        } else {
            false to null
        }
    }

    suspend fun getEncryptedBase64MetaDataFromTitleAndContent(
        title: String,
        content: String
    ): String? =
        try {
            val str = "${title.replace("\n", "")}\n${getFirst64CharacterOfContent(content)}"
            val initVector = symmetricHelper.createInitVector()
            Base64.encodeBytes(
                initVector.combineWith(
                    kryptCryptographyHelper.encryptBytes(
                        str.toUtf8Bytes(),
                        initVector
                    ).getOrThrow()
                )
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }

    suspend fun decryptMetaData(string: String): Pair<String, String>? {
        val encryptedBytes = Base64.decode(string)
        val initVector = encryptedBytes.getBeforeIndex(SymmetricHelper.INITIALIZE_VECTOR_SIZE)
        val decryptedText = kryptCryptographyHelper.decryptBytes(
            encryptedBytes.getAfterIndex(SymmetricHelper.INITIALIZE_VECTOR_SIZE),
            initVector
        ).getOrThrow()
        val text = String(decryptedText)
        val title = text.substring(0, text.indexOf("\n"))
        val content = text.substring(text.indexOf("\n") + 1, text.length)
        return title to content
    }

    private fun getFirst64CharacterOfContent(content: String): String =
        if (content.trim().length > 64)
            content.substring(0, 63)
        else
            content

    suspend fun decryptTextFile(filePath: String): Pair<String, String>? {
        val encryptedFile = File(filePath)
        if (!encryptedFile.exists()) {
            return null
        }

        return try {
            val fileContentByteArray = encryptedFile.readBytes()
            val initVector =
                fileContentByteArray.getBeforeIndex(SymmetricHelper.INITIALIZE_VECTOR_SIZE)
            val decryptedByteArray = kryptCryptographyHelper.decryptBytes(
                fileContentByteArray.getAfterIndex(SymmetricHelper.INITIALIZE_VECTOR_SIZE),
                initVector
            ).getOrThrow()
            val text = String(decryptedByteArray)

            val title = text.substring(0, text.indexOf("\n"))
            val content = text.substring(text.indexOf("\n") + 1, text.length)

            title to content
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}