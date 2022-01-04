package ir.mehdiyari.krypt.data.account

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts"
)
class AccountEntity(
    @PrimaryKey val name: String,
    val encryptedName: String,
    @Transient val password: String = "" /** We doesn't store the password in database */
) {
    constructor(name: String, encryptedName: String) : this(name, encryptedName, "")
}