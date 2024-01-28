package ir.mehdiyari.krypt.mediaList

enum class MediaViewAction(val value: Int) {
    DEFAULT(0),
    PICK_MEDIA(1),
    DECRYPT_MEDIA(2),
    TAKE_MEDIA(3),
    ENCRYPT_MEDIA(4),
    SHARED_MEDIA(5),
}