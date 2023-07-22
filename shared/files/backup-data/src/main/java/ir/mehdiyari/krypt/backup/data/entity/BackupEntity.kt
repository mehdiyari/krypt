package ir.mehdiyari.krypt.backup.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "backups"
)
class BackupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val dateTime: Long,
    val account: String
)