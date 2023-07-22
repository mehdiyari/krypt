package ir.mehdiyari.krypt.accounts.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts"
)
class AccountEntity(
    @PrimaryKey val name: String,
    val encryptedName: String
)