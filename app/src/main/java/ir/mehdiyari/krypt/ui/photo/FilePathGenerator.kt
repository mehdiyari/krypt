package ir.mehdiyari.krypt.ui.photo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePathGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun generateFilePathForPhotos(photoPath: String): String =
        "${context.cacheDir.path}/krypt_${System.currentTimeMillis()}.${
            photoPath.split(".").lastOrNull() ?: ".jpg"
        }"
}
