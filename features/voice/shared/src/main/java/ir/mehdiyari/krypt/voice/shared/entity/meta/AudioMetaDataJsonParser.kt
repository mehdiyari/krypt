package ir.mehdiyari.krypt.voice.shared.entity.meta

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import javax.inject.Inject

class AudioMetaDataJsonParser @Inject constructor() : JsonAdapter<AudioMetaData>() {

    private val options = JsonReader.Options.of("size", "duration", "date")

    override fun fromJson(reader: JsonReader): AudioMetaData? {
        var size: Long = 0
        var duration: Long = 0
        var date: Long = 0

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> size = reader.nextLong()
                1 -> duration = reader.nextLong()
                2 -> date = reader.nextLong()
                else -> {
                    reader.skipValue()
                    reader.skipValue()
                }
            }
        }

        reader.endObject()

        return AudioMetaData(size, duration, date)
    }

    override fun toJson(writer: JsonWriter, value: AudioMetaData?) {
        value?.apply {
            writer.beginObject()
            writer.name("size").value(value.size)
            writer.name("duration").value(value.duration)
            writer.name("date").value(value.date)
            writer.endObject()
        }
    }
}