package ir.mehdiyari.krypt.voice.collection

import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class AudioTimeMapper @Inject constructor() {

    companion object {
        const val FORMAT = "yyyy/MM/dd HH:mm:ss"
    }

    fun mapDate(date: Long): String {
        return SimpleDateFormat(FORMAT, Locale.getDefault())
            .format(date)
    }

}