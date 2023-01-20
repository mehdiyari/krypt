package ir.mehdiyari.krypt.ui.voice.audios

import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.ui.voice.recorder.SecondToTimerMapper
import ir.mehdiyari.krypt.ui.voice.recorder.meta.AudioMetaData
import ir.mehdiyari.krypt.ui.voice.recorder.meta.AudioMetaDataJsonParser
import javax.inject.Inject

class AudioEntityMapper @Inject constructor(
    private val audioMetaDataJsonParser: AudioMetaDataJsonParser,
    private val secondToTimerMapper: SecondToTimerMapper,
    private val audioTimeMapper: AudioTimeMapper
) {
    companion object {
        const val DEFAULT_VALUE = 0L
    }

    fun map(fileEntity: FileEntity): AudioEntity {
        val meta = try {
            audioMetaDataJsonParser.fromJson(fileEntity.metaData)!!
        } catch (t: Throwable) {
            AudioMetaData(
                DEFAULT_VALUE, DEFAULT_VALUE, DEFAULT_VALUE
            )
        }

        val finalDuration = if (meta.duration == DEFAULT_VALUE)
            DEFAULT_VALUE.toString()
        else
            secondToTimerMapper.map(meta.duration)

        val finalTime = if (meta.date == 0L)
            DEFAULT_VALUE.toString()
        else {
            try {
                audioTimeMapper.mapDate(meta.date)
            } catch (t: Throwable) {
                DEFAULT_VALUE.toString()
            }
        }

        return AudioEntity(
            fileEntity.id,
            AudioEntity.NAME_PREFIX,
            finalDuration,
            finalTime
        )
    }
}