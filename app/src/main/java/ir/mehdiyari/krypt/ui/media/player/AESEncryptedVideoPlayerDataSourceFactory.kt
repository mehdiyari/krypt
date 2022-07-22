package ir.mehdiyari.krypt.ui.media.player

import com.google.android.exoplayer2.upstream.DataSource
import dagger.Lazy
import javax.inject.Inject

class AESEncryptedVideoPlayerDataSourceFactory @Inject constructor(
    private val aesEncryptedVideoPlayerDataSource: Lazy<AESEncryptedVideoPlayerDataSource>
) : DataSource.Factory {

    override fun createDataSource(): DataSource = aesEncryptedVideoPlayerDataSource.get()

}