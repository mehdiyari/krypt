package ir.mehdiyari.krypt.voice.shared

import javax.inject.Inject

class SecondToTimerMapper @Inject constructor() {

    fun map(sec: Long): String {
        val hours = sec / 3600
        val minutes = (sec % 3600) / 60
        val seconds = sec % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}