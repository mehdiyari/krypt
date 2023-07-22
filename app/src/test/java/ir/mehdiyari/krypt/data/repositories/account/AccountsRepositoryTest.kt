package ir.mehdiyari.krypt.data.repositories.account

import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.app.user.UserKeyProvider
import ir.mehdiyari.krypt.app.user.UsernameProvider
import ir.mehdiyari.krypt.cryptography.api.KryptKeyGenerator
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.cryptography.utils.toUtf8Bytes
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalCoroutinesApi::class)
class AccountsRepositoryTest {
    private lateinit var accountsDao: AccountsDao
    private lateinit var symmetricHelper: SymmetricHelper
    private lateinit var kryptKeyGenerator: KryptKeyGenerator
    private lateinit var currentUserManager: CurrentUserManager
    private lateinit var usernameProvider: UsernameProvider
    private lateinit var userKeyProvider: UserKeyProvider
    private lateinit var hashingUtils: HashingUtils
    private lateinit var accountsRepository: AccountsRepository

    @Before
    fun setup() {
        accountsDao = mockk(relaxed = true)
        symmetricHelper = mockk(relaxed = true)
        kryptKeyGenerator = mockk(relaxed = true)
        currentUserManager = mockk(relaxed = true)
        usernameProvider = mockk(relaxed = true)
        userKeyProvider = mockk(relaxed = true)
        hashingUtils = mockk(relaxed = true)

        accountsRepository = AccountsRepositoryImpl(
            accountsDao,
            symmetricHelper,
            kryptKeyGenerator,
            currentUserManager,
            usernameProvider,
            userKeyProvider,
            hashingUtils
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `addAccount should return true when everything is ok`() = runTest {
        val accountName = "AmirHossein"
        val accountPassword = "StrongPassword123"
        val accountPasswordConfig = "StrongPassword123"
        val salt = byteArrayOf(0x00)
        val iv = byteArrayOf(0x00)
        val key = byteArrayOf(0x00)
        val encryptedNameBytes = byteArrayOf(0x00)
        coEvery { hashingUtils.generateRandomSalt() } returns salt
        coEvery { symmetricHelper.createInitVector() } returns iv
        coEvery { kryptKeyGenerator.generateKey(accountPassword, salt) } returns Result.success(key)
        coEvery { symmetricHelper.encrypt(any(), any(), any()) } returns encryptedNameBytes
        coEvery { accountsDao.insert(any()) } just Runs
        coEvery {
            symmetricHelper.encrypt(
                data = accountName.toUtf8Bytes(),
                key = SecretKeySpec(key, "AES"),
                initVector = iv
            )
        } returns encryptedNameBytes

        val result =
            accountsRepository.addAccount(
                accountName,
                accountPassword,
                accountPasswordConfig
            )

        assertTrue(result.first)
        assertNull(result.second)
        coVerify(exactly = 1) { accountsDao.insert(any()) }
        coVerify(exactly = 1) {
            symmetricHelper.encrypt(
                data = accountName.toUtf8Bytes(),
                key = SecretKeySpec(key, "AES"),
                initVector = iv
            )
        }
    }

    @Test
    fun `getAllAccountsName should returns all account names`(): Unit = runTest {
        val accounts = (0..10).map {
            AccountEntity(
                name = "name$it",
                encryptedName = "encryptedName$it"
            )
        }
        coEvery { accountsDao.getAccounts() } returns accounts

        val actual = accountsRepository.getAllAccountsName()

        assertEquals(accounts.map { it.name }, actual)
        coVerify(exactly = 1) { accountsDao.getAccounts() }
    }

    @Test
    fun `isAccountExists should return true if account exists`() = runTest {
        coEvery { accountsDao.isAnyAccountExist() } returns true

        val actual = accountsRepository.isAccountExists()

        coVerify(exactly = 1) { accountsDao.isAnyAccountExist() }
        assertEquals(true, actual)
    }

    @Ignore
    @Test
    fun testSuccessfulLogin() = runTest {

        // TODO: Mehdi would take care of it
        val accountName = "TestAccount"
        val password = "TestPassword"
        val account = AccountEntity(accountName, "EncodedTest")
        coEvery { accountsDao.getAccountWithName(accountName) } returns account

        coEvery {
            symmetricHelper.decrypt(
                any(),
                any(),
                any(),
                any()
            )
        } returns accountName.toByteArray()
        coEvery { kryptKeyGenerator.generateKey(password, any()) } returns Result.success(
            byteArrayOf()
        )
        val loginResult = accountsRepository.login(accountName, password)
        assertTrue(loginResult)
        coVerify { currentUserManager.setCurrentUser(accountName, any()) }
    }

    @Test
    fun `deleteCurrentAccount should delete the current account`() = runTest {
        val username = "TestUser"

        every { usernameProvider.getUsername() } returns username

        accountsRepository.deleteCurrentAccount()

        coVerify { accountsDao.deleteCurrentAccount(username) }
    }
}