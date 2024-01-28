package ir.mehdiyari.krypt.backup.logic.backup

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import ir.mehdiyari.krypt.accounts.data.entity.AccountEntity
import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import javax.inject.Inject

internal class DBBackupModelJsonAdapter @Inject constructor() : JsonAdapter<DBBackupModel>() {

    companion object {
        const val ACCOUNT = "account"
        const val DATA = "data"

        const val ACCOUNT_NAME = "name"
        const val ACCOUNT_ENCRYPTED_NAME = "encrypted_name"

        const val FILE_ID = "id"
        const val FILE_META_DATA = "meta"
        const val FILE_TYPE = "type"
        const val FILE_PATH = "path"
    }

    private val options = JsonReader.Options.of(ACCOUNT, DATA)
    private val accountOptions = JsonReader.Options.of(ACCOUNT_NAME, ACCOUNT_ENCRYPTED_NAME)
    private val fileOptions = JsonReader.Options.of(FILE_ID, FILE_META_DATA, FILE_TYPE)

    override fun fromJson(reader: JsonReader): DBBackupModel? {
        var account: AccountEntity? = null
        var files: List<FileEntity>? = null

        reader.beginObject()

        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> {
                    account = parseAccount(reader)
                }
                1 -> {
                    files = parseFiles(reader)
                }
                else -> {
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }


        reader.endObject()

        return if (account != null && !files.isNullOrEmpty()) {
            DBBackupModel(
                account,
                files
            )
        } else {
            null
        }
    }

    private fun parseFiles(reader: JsonReader): List<FileEntity>? {
        val files = mutableListOf<FileEntity>()
        reader.beginArray()
        while (reader.hasNext()) {
            reader.beginObject()
            var id: Long? = null
            var meta: String? = null
            var type: FileTypeEnum? = null
            var path: String? = null
            while (reader.hasNext()) {
                when (reader.selectName(fileOptions)) {
                    0 -> id = reader.nextLong()
                    1 -> meta = reader.nextString()
                    2 -> {
                        val typeString = reader.nextString()
                        type = FileTypeEnum.values().firstOrNull {
                            it.value == typeString
                        }
                    }
                    3 -> path = reader.nextString()
                    else -> {
                        reader.skipName()
                        reader.skipValue()
                    }
                }
            }

            if (id != null) {
                files.add(
                    FileEntity(
                        id = id,
                        type = type,
                        filePath = path ?: "",
                        metaData = meta ?: "",
                        accountName = ""
                    )
                )
            }
            reader.endObject()
        }
        reader.endArray()

        return files
    }

    private fun parseAccount(reader: JsonReader): AccountEntity? {
        var accountName: String? = null
        var accountEncryptedName: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(accountOptions)) {
                0 -> accountName = reader.nextString()
                1 -> accountEncryptedName = reader.nextString()
                else -> {
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()

        return if (!accountName.isNullOrBlank() && !accountEncryptedName.isNullOrBlank()) {
            AccountEntity(accountName, accountEncryptedName)
        } else {
            null
        }
    }

    override fun toJson(writer: JsonWriter, value: DBBackupModel?) {
        value ?: return

        writer.beginObject()
        writer.name(ACCOUNT)
        writer.beginObject()
        writer.name(ACCOUNT_NAME).value(value.account.name)
        writer.name(ACCOUNT_ENCRYPTED_NAME).value(value.account.encryptedName)
        writer.endObject()

        writer.name(DATA)
        writer.beginArray()
        value.files.forEach {
            writer.beginObject()
            writer.name(FILE_ID).value(it.id)
            writer.name(FILE_META_DATA).value(it.metaData)
            writer.name(FILE_TYPE).value(it.type?.value)
            writer.name(FILE_PATH).value(it.filePath)
            writer.endObject()
        }
        writer.endArray()
        writer.endObject()
    }

}