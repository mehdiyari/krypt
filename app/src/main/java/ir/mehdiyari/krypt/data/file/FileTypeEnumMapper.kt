package ir.mehdiyari.krypt.data.file

class FileTypeEnumMapper {

    @androidx.room.TypeConverter
    fun mapToFileTypeEnum(value:String?) : FileTypeEnum? = FileTypeEnum.values().toList().firstOrNull {
        it.value == value
    }

    @androidx.room.TypeConverter
    fun mapToString(fileTypeEnum: FileTypeEnum?): String? = fileTypeEnum?.value

}