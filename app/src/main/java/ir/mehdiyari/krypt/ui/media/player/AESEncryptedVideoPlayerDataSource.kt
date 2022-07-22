package ir.mehdiyari.krypt.ui.media.player

import android.net.Uri
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import ir.mehdiyari.krypt.crypto.SymmetricHelper
import java.io.File
import java.io.FileInputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class AESEncryptedVideoPlayerDataSource @Inject constructor(
    private val key: dagger.Lazy<SecretKey>
) : DataSource {

    private var inputStream: CipherInputStream? = null
    private lateinit var uri: Uri

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return if (length == 0) {
            0
        } else {
            inputStream?.read(buffer, offset, length) ?: 0
        }
    }

    override fun addTransferListener(transferListener: TransferListener) = Unit

    override fun open(dataSpec: DataSpec): Long {
        val len = try {
            uri = dataSpec.uri
            val file = File(uri.path!!)
            val cipher = Cipher.getInstance(SymmetricHelper.AES_CBC_PKS5PADDING)
            val realFileInputStream = FileInputStream(file)
            val initVector = ByteArray(SymmetricHelper.INITIALIZE_VECTOR_SIZE)
            realFileInputStream.read(initVector)
            cipher.init(Cipher.DECRYPT_MODE, key.get(), IvParameterSpec(initVector))
            inputStream = CipherInputStream(realFileInputStream, cipher)
            (file.length() / 8) - 16
        } catch (t: Throwable) {
            t.printStackTrace()
            dataSpec.length
        }

        return len
    }

    override fun getUri(): Uri? = uri

    override fun close() {
        inputStream?.close()
    }
}