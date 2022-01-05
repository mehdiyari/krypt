package ir.mehdiyari.krypt.data.account

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts"
)
class AccountEntity(
    @PrimaryKey val name: String,
    val encryptedName: String
)