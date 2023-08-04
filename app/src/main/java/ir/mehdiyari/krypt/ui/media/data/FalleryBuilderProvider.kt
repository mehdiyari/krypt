package ir.mehdiyari.krypt.ui.media.data

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mehdiyari.fallery.imageLoader.FalleryImageLoader
import ir.mehdiyari.fallery.main.fallery.CameraEnabledOptions
import ir.mehdiyari.fallery.main.fallery.CaptionEnabledOptions
import ir.mehdiyari.fallery.main.fallery.FalleryBucketsSpanCountMode
import ir.mehdiyari.fallery.main.fallery.FalleryBuilder
import ir.mehdiyari.fallery.main.fallery.FalleryOptions
import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.app.imageloader.DeviceGalleryImageLoader
import ir.mehdiyari.krypt.core.designsystem.theme.isInDarkTheme
import ir.mehdiyari.krypt.permission.getFileProviderAuthority
import ir.mehdiyari.krypt.mediaPlayer.PlayerActivity
import javax.inject.Inject

class FalleryBuilderProvider @Inject constructor(
    private val deviceGalleryImageLoader: DeviceGalleryImageLoader,
    @ApplicationContext private val context: Context,
    private val encryptedMediasBucketContentProvider: EncryptedMediasBucketContentProvider,
    private val encryptedMediasBucketProvider: EncryptedMediasBucketProvider,
) {
    fun getBaseOptionsOfFallery(
        deviceGalleryImageLoader: FalleryImageLoader,
        context: Context,
    ): FalleryBuilder = FalleryBuilder()
        .setImageLoader(deviceGalleryImageLoader)
        .mediaTypeFiltering(BucketType.VIDEO_PHOTO_BUCKETS)
        .setFalleryToolbarTitleText(R.string.app_name)
        .setMediaCountEnabled(true)
        .setGrantExternalStoragePermission(true)
        .setMediaObserverEnabled(true)
        .setCaptionEnabledOptions(CaptionEnabledOptions(false))
        .setFallerySpanCountMode(FalleryBucketsSpanCountMode.UserZoomInOrZoomOut)
        .setTheme(if (context.isInDarkTheme()) ir.mehdiyari.fallery.R.style.Fallery_Dracula else ir.mehdiyari.fallery.R.style.Fallery_Light)


    fun getDefaultFalleryOptions(): FalleryOptions {
        return getBaseOptionsOfFallery(deviceGalleryImageLoader, context).setCameraEnabledOptions(
            CameraEnabledOptions(
                true,
                getFileProviderAuthority(context.packageName)
            )
        ).setOnVideoPlayClick {
            context.startActivity(
                Intent(context, PlayerActivity::class.java).addExtraForPlayerToIntent(
                    it, false
                ).apply {
                    flags = FLAG_ACTIVITY_NEW_TASK
                }
            )
        }.build()
    }

    fun getMediaPickerForDecrypting(): FalleryOptions {
        return getBaseOptionsOfFallery(deviceGalleryImageLoader, context)
            .setGrantExternalStoragePermission(false)
            .setContentProviders(
                encryptedMediasBucketContentProvider,
                encryptedMediasBucketProvider
            ).setOnVideoPlayClick {
                ContextCompat.startActivity(
                    context,
                    Intent(context, PlayerActivity::class.java).apply {
                        flags = FLAG_ACTIVITY_NEW_TASK
                    }.addExtraForPlayerToIntent(it, true),
                    null
                )
            }
            .build()
    }
}

private fun Intent.addExtraForPlayerToIntent(videoPath: String, isEncryptedVideo: Boolean = false): Intent =
    this.apply {
        this.putExtra("video", videoPath)
        this.putExtra("encrypted", isEncryptedVideo)
    }