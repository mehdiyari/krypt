package ir.mehdiyari.krypt.data.repositories

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