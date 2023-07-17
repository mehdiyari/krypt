package ir.mehdiyari.krypt.data.repositories.files

import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.app.user.UsernameProvider
import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.file.FilesDao
import ir.mehdiyari.krypt.utils.FilesUtilities
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilesRepositoryTest {
    private val ioDispatcher = StandardTestDispatcher()
    private lateinit var filesDao: FilesDao
    private lateinit var backupDao: BackupDao
    private lateinit var usernameProvider: UsernameProvider
    private lateinit var filesUtilities: FilesUtilities
    private lateinit var filesRepository: FilesRepository
    private lateinit var fileWrapper: FileWrapper

    @Before
    fun setup() {
        Dispatchers.setMain(ioDispatcher)
        filesDao = mockk<FilesDao>()
        backupDao = mockk<BackupDao>()
        usernameProvider = mockk<UsernameProvider>()
        filesUtilities = mockk<FilesUtilities>()
        fileWrapper = mockk()
        filesRepository = FilesRepositoryImpl(
            filesDao,
            backupDao,
            usernameProvider,
            filesUtilities,
            fileWrapper,
            ioDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `getAllFilesTypeCounts should return correct counts`(): Unit = runTest {
        val username = "testUser"
        every { usernameProvider.getUsername() } returns username
        coEvery { filesDao.getFilesCountBasedOnType(username, any()) } returns 1

        val result = filesRepository.getAllFilesTypeCounts()

        assertEquals(FileTypeEnum.values().size, result.size)
        assertTrue(result.all { it.second == 1L })
    }

    @Test
    fun `getAllFilesTypeCounts should return 0 if usernameProvider#getUsername() is null`(): Unit =
        runTest {
            val username: String? = null
            every { usernameProvider.getUsername() } returns username
            coEvery { filesDao.getFilesCountBasedOnType("username", any()) } returns 1

            val result = filesRepository.getAllFilesTypeCounts()

            assertEquals(FileTypeEnum.values().size, result.size)
            assertTrue(result.all { it.second == 0L })
            coVerify(exactly = 0) {
                filesDao.getFilesCountBasedOnType("username", any())
            }
        }

    @Test
    fun `insertFiles should correctly insert files`(): Unit = runTest {
        val username = "testUser"
        val files = listOf(
            FileEntity(
                id = 1,
                type = null,
                filePath = "something",
                metaData = "meta",
                accountName = "user"
            )
        )
        every { usernameProvider.getUsername() } returns username
        coEvery { filesDao.insertFiles(any()) } just Runs

        filesRepository.insertFiles(files)

        coVerify(exactly = 1) {
            filesDao.insertFiles(
                files.map { it.copy(accountName = username) })
        }
    }

    @Test
    fun `getMediasCount should return correct count`(): Unit = runTest {
        val username = "testUser"
        val photoCount = 3L
        val videoCount = 2L
        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Photo
            )
        } returns photoCount
        coEvery {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Video
            )
        } returns videoCount

        val result = filesRepository.getMediasCount()

        assertEquals(photoCount + videoCount, result)
        coVerify(exactly = 1) {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Photo
            )
        }
        coVerify(exactly = 1) {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Video
            )
        }
    }

    @Test
    fun `mapThumbnailsAndNameToFileEntity should return correct FileEntity list`(): Unit = runTest {
        val username = "testUsername"
        val medias = arrayOf("path1", "/path/path2")
        val nameOfFile = "meta3"
        val allEncryptedMedia = generateFileEntity()

        coEvery { usernameProvider.getUsername() } returns username
        coEvery { filesDao.getAllMedia(username) } returns allEncryptedMedia
        coEvery { filesUtilities.getNameOfFileWithExtension("/path/path2") } returns nameOfFile

        val result = filesRepository.mapThumbnailsAndNameToFileEntity(medias)

        assertEquals(3, result.size)
        assertEquals("path1", result[0].filePath)
        assertEquals("meta3", result[1].metaData)
        coVerify(exactly = 1) {
            filesDao.getAllMedia(
                username
            )
        }
    }

    @Test
    fun `deleteEncryptedFilesFromKryptDBAndFileSystem should delete all given files`(): Unit =
        runTest {
            val files = generateFileEntity()

            coEvery { filesDao.deleteFiles(any()) } just runs
            coEvery { fileWrapper.delete(any()) } returns true

            filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(files)

            coVerify { filesDao.deleteFiles(files) }
            coVerify { fileWrapper.delete(any()) }
        }

    @Test
    fun `getAllFilesSize should return total size of all files`(): Unit = runTest {
        val username = "test_user"
        val backupFiles = listOf("backup_file1", "backup_file2")
        val daoFiles = listOf("dao_file1", "dao_file2")
        val backupFilesSize = 100L
        val daoFilesSize = 200L
        coEvery { backupDao.getAllBackupFiles(username) } returns backupFiles
        coEvery { filesDao.getAllFilesPath(username) } returns daoFiles
        coEvery { fileWrapper.length(any()) } returnsMany listOf(
            backupFilesSize,
            backupFilesSize,
            daoFilesSize,
            daoFilesSize
        )
        every { usernameProvider.getUsername() } returns username

        val totalSize = filesRepository.getAllFilesSize()

        coVerify { backupDao.getAllBackupFiles(username) }
        coVerify { filesDao.getAllFilesPath(username) }
        coVerify { fileWrapper.length("backup_file1") }
        coVerify { fileWrapper.length("backup_file2") }
        coVerify { fileWrapper.length("dao_file1") }
        coVerify { fileWrapper.length("dao_file2") }
        assertEquals((backupFilesSize * backupFiles.size + daoFilesSize * daoFiles.size), totalSize)
    }

    @Test
    fun `getLastEncryptedMediaThumbnail returns the last thumbnail of media`(): Unit = runTest {
        val username = "test"
        val files = generateFileEntity()

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Photo,
                FileTypeEnum.Video
            )
        } returns files

        val result = filesRepository.getLastEncryptedMediaThumbnail()

        assertEquals("meta10", result)
        coVerify(exactly = 1) {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Photo,
                FileTypeEnum.Video
            )
        }
    }

    @Test
    fun `getLastEncryptedPhotoThumbnail returns the last thumbnail of photo`(): Unit = runTest {
        val username = "test"
        val files = generateFileEntity()

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username, FileTypeEnum.Photo
            )
        } returns files

        val result = filesRepository.getLastEncryptedPhotoThumbnail()

        assertEquals("meta10", result)
        coVerify(exactly = 1) {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Photo
            )
        }
    }

    @Test
    fun `getLastEncryptedVideoThumbnail returns the last thumbnail of video`(): Unit = runTest {
        val username = "test"
        val files = generateFileEntity()

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username, FileTypeEnum.Video
            )
        } returns files

        val result = filesRepository.getLastEncryptedVideoThumbnail()

        assertEquals("meta10", result)
        coVerify(exactly = 1) {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Video
            )
        }
    }

    @Test
    fun `getAllTextFiles returns the text files of current account`(): Unit = runTest {
        val username = "test"
        val files = generateFileEntity().filter { it.type == FileTypeEnum.Text }

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Text
            )
        } returns files

        val result = filesRepository.getAllTextFiles()

        assertEquals(files, result)
        coVerify(exactly = 1) {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Text
            )
        }
    }

    @Test
    fun `getFileById returns a file by its id if the file exists`(): Unit = runTest {
        val username = "test"
        val file = generateFileEntity().firstOrNull { it.id == 10L }

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getFileById(username, 10)
        } returns file

        val result = filesRepository.getFileById(10L)

        assertEquals(file, result)
        coVerify(exactly = 1) { filesDao.getFileById(username, 10) }
    }

    @Test
    fun `getFileById returns null if the file not exists`(): Unit = runTest {
        val username = "test"

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getFileById(username, 1)
        } returns null

        val result = filesRepository.getFileById(1)

        assertEquals(null, result)
        coVerify(exactly = 1) { filesDao.getFileById(username, 1) }
    }

    @Test
    fun `getAllFiles returns all files`(): Unit = runTest {
        val username = "test"

        every { usernameProvider.getUsername() } returns username

        val files = generateFileEntity()

        coEvery {
            filesDao.getAllFiles(username)
        } returns files

        val result = filesRepository.getAllFiles()

        assertEquals(files, result)
        coVerify(exactly = 1) { filesDao.getAllFiles(username) }
    }

    @Test
    fun `getAllImages should return all images`(): Unit = runTest {
        val expected = generateFileEntity().filter { it.type == FileTypeEnum.Photo }
        val username = "test_user"

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllMedia(
                username,
                listOf(FileTypeEnum.Photo)
            )
        } returns expected

        val actual = filesRepository.getAllImages()

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getAllMedia(
                accountName = username,
                mediaType = listOf(FileTypeEnum.Photo)
            )
        }
    }

    @Test
    fun `getAllVideos should return all videos`(): Unit = runTest {
        val expected = generateFileEntity().filter {
            it.type == FileTypeEnum.Video
        }
        val username = "test_user"

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllMedia(
                username,
                listOf(FileTypeEnum.Video)
            )
        } returns expected

        val actual = filesRepository.getAllVideos()

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getAllMedia(
                accountName = username,
                mediaType = listOf(FileTypeEnum.Video)
            )
        }
    }

    @Test
    fun `getPhotosCount should return photos count`(): Unit = runTest {
        val expected = 5L
        val username = "test_user"
        every { usernameProvider.getUsername() } returns username

        coEvery {
            filesDao.getFilesCountBasedOnType(
                username, FileTypeEnum.Photo
            )
        } returns expected

        val actual = filesRepository.getPhotosCount()

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Photo
            )
        }
    }

    @Test
    fun `getAudiosCount should return audios count`(): Unit = runTest {
        val expected = 3L
        val username = "test_user"

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Audio
            )
        } returns expected

        val actual = filesRepository.getAudiosCount()

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Audio
            )
        }
    }

    @Test
    fun `getVideosCount should return videos count`(): Unit = runTest {
        val expected = 4L
        val username = "test_user"

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Video
            )
        } returns expected

        val actual = filesRepository.getVideosCount()

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getFilesCountBasedOnType(
                username,
                FileTypeEnum.Video
            )
        }
    }

    @Test
    fun `getFileByThumbPath should return file by thumb path`(): Unit = runTest {
        val username = "test_user"
        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getMediaFileByPath(
                username,
                "thumb_path"
            )
        } returns generateFileEntity().first()

        filesRepository.getFileByThumbPath("thumb_path")

        coVerify(exactly = 1) { filesDao.getMediaFileByPath(username, "thumb_path") }
    }

    @Test
    fun `getAllAudioFiles should return all audio files`(): Unit = runTest {
        val expected = generateFileEntity().filter { it.type == FileTypeEnum.Audio }
        val username = "test_user"

        every { usernameProvider.getUsername() } returns username
        coEvery {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Audio
            )
        } returns expected

        val actual = filesRepository.getAllAudioFiles()

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getAllFilesOfCurrentAccountBasedOnType(
                username,
                FileTypeEnum.Audio
            )
        }
    }

    @Test
    fun `updateFile should update file`(): Unit = runTest {
        val fileEntity = generateFileEntity().first()
        coEvery { filesDao.updateFile(fileEntity) } just Runs

        filesRepository.updateFile(fileEntity)

        coVerify(exactly = 1) { filesDao.updateFile(fileEntity) }
    }

    @Test
    fun `getAudioById should return audio file by id`(): Unit = runTest {
        val expected = generateFileEntity().firstOrNull { it.id == 11L }
        val username = "test_user"

        every { usernameProvider.getUsername() } returns username

        coEvery { filesDao.getFileById(username, 11L) } returns expected
        val actual = filesRepository.getAudioById(11L)

        assertEquals(expected, actual)
        coVerify(exactly = 1) {
            filesDao.getFileById(username, 11L)
        }
    }

    private fun generateFileEntity(): List<FileEntity> {
        val fileTypeEnum =
            arrayOf(FileTypeEnum.Text, FileTypeEnum.Photo, FileTypeEnum.Video, FileTypeEnum.Audio)
        return (0..10).map {
            FileEntity(
                id = it.toLong(),
                type = fileTypeEnum[it % 4],
                filePath = "path$it",
                metaData = "meta$it",
                accountName = "user"
            )
        }
    }

}