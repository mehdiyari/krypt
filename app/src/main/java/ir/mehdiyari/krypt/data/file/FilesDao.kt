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

    @Query("SELECT * from files where accountName = :accountName AND type = :type")
    suspend fun getAllFilesOfCurrentAccountBasedOnType(
        accountName: String,
        type: FileTypeEnum
    ): List<FileEntity>


    @Query("SELECT count(*) from files where accountName = :accountName AND type = :type")
    suspend fun getAllFilesCountOfCurrentAccountBasedOnType(
        accountName: String,
        type: FileTypeEnum
    ): Long

    @Delete(entity = FileEntity::class)
    suspend fun deleteFiles(files: List<FileEntity>)
}