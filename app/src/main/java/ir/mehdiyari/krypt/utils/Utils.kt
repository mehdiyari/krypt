package ir.mehdiyari.krypt.utils

fun formatSize(size: Long): String {
    var internalSize = size
    var suffix: String?
    if (internalSize >= 1024) {
        suffix = "KB"
        internalSize /= 1024
        if (internalSize >= 1024) {
            suffix = "MB"
            internalSize /= 1024
            if (internalSize >= 1024) {
                suffix = "GB"
                internalSize /= 1024

            }
        }
    } else {
        suffix = "Bytes"
    }

    return "$internalSize $suffix"
}

fun Long.convertToReadableTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", minutes, remainingSeconds)
    }
}