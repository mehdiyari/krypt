package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.file.FilesDao
import ir.mehdiyari.krypt.di.qualifiers.AccountName
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    private val filedDao: FilesDao,
    @AccountName private val currentAccountName: dagger.Lazy<String>
) {

    suspend fun getAllFilesTypeCounts(): List<Pair<FileTypeEnum, Long>> =
        mutableListOf<Pair<FileTypeEnum, Long>>().apply {
            FileTypeEnum.values().forEach { fileType ->
                add(
                    fileType to try {
                        filedDao.getAllFilesCountOfCurrentAccountBasedOnType(
                            currentAccountName.get(),
                            fileType
                        )
                    } catch (t: Throwable) {
                        0
                    }
                )
            }
        }.toList()


    suspend fun insertFiles(
        files: List<FileEntity>
    ) {
        filedDao.insertFiles(files)
    }
}