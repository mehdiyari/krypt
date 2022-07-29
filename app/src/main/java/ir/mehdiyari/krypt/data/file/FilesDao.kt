package ir.mehdiyari.krypt.data.file

import androidx.room.*

@Dao
interface FilesDao {

    @Insert(entity = FileEntity::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFile(
        file: FileEntity
    )

    @Insert(entity = FileEntity::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFiles(
        files: List<FileEntity>
    )

    @Query("DELETE from files where filePath = :path")
    suspend fun deleteFileByPath(
        path: String
    ): Int

    @Query("SELECT * from files where accountName = :accountName AND type in (:type)")
    suspend fun getAllFilesOfCurrentAccountBasedOnType(
        accountName: String,
        vararg type: FileTypeEnum
    ): List<FileEntity>


    @Query("SELECT count(*) from files where accountName = :accountName AND type = :type")
    suspend fun getFilesCountBasedOnType(
        accountName: String,
        type: FileTypeEnum
    ): Long

    @Delete(entity = FileEntity::class)
    suspend fun deleteFiles(files: List<FileEntity>)

    @Query("select * from files where id = :id and accountName = :accountName LIMIT 1")
    suspend fun getFileById(accountName: String, id: Long): FileEntity?

    @Query("select * from files where accountName = :accountName and type in (:mediaType)")
    suspend fun getAllMedia(
        accountName: String,
        mediaType: List<FileTypeEnum> = listOf(FileTypeEnum.Photo, FileTypeEnum.Video)
    ): List<FileEntity>

    @Query("select * from files where accountName = :currentAccountName")
    suspend fun getAllFiles(currentAccountName: String): List<FileEntity>

    @Query("select filePath from files where accountName = :accountName")
    suspend fun getAllFilesPath(accountName: String): List<String>?
}