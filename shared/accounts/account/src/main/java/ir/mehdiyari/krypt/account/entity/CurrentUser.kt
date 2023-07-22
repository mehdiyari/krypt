package ir.mehdiyari.krypt.account.entity

class CurrentUser(
    var accountName: String? = null,
    var key: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = accountName.hashCode()

    fun clear() {
        accountName = null
        key = ByteArray(32) { 0 }
        key = null
    }
}