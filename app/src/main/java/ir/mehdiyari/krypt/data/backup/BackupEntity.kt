package ir.mehdiyari.krypt.data.backup

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