package ir.mehdiyari.krypt.file.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: FileTypeEnum?,
    val filePath: String,
    val metaData: String,
    val accountName: String
)

