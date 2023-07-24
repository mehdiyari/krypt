package ir.mehdiyari.krypt.files.logic.repositories.api

import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum

interface FilesRepository {
    suspend fun getAllFilesTypeCounts(): List<Pair<FileTypeEnum, Long>>

    suspend fun insertFiles(
        files: List<FileEntity>
    )

    suspend fun getMediasCount(): Long

    suspend fun getLastEncryptedMediaThumbnail(): String?

    suspend fun getLastEncryptedPhotoThumbnail(): String?

    suspend fun getLastEncryptedVideoThumbnail(): String?

    suspend fun getAllEncryptedMedia(): List<FileEntity>

    suspend fun mapThumbnailsAndNameToFileEntity(medias: Array<String>): List<FileEntity>

    suspend fun deleteEncryptedFilesFromKryptDBAndFileSystem(files: List<FileEntity>)

    suspend fun getAllTextFiles(): List<FileEntity>

    suspend fun getFileById(id: Long): FileEntity?

    suspend fun getAllFiles(): List<FileEntity>

    suspend fun getAllFilesSize(): Long

    suspend fun getAllImages(): List<FileEntity>

    suspend fun getAllVideos(): List<FileEntity>

    suspend fun getPhotosCount(): Long

    suspend fun getAudiosCount(): Long

    suspend fun getVideosCount(): Long

    suspend fun getFileByThumbPath(thumbFileName: String): FileEntity?

    suspend fun getAllAudioFiles(): List<FileEntity>

    suspend fun updateFile(fileEntity: FileEntity): Unit

    suspend fun getAudioById(id: Long): FileEntity?
}