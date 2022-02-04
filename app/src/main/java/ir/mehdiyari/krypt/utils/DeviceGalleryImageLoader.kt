package ir.mehdiyari.krypt.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import ir.mehdiyari.fallery.imageLoader.FalleryImageLoader
import ir.mehdiyari.fallery.imageLoader.PhotoDiminution
import javax.inject.Inject

class DeviceGalleryImageLoader @Inject constructor() : FalleryImageLoader {

    override fun loadGif(
        context: Context,
        imageView: ImageView,
        resizeDiminution: PhotoDiminution,
        placeHolderColor: Int,
        path: String
    ) {
        Glide.with(imageView)
            .asGif()
            .placeholder(ColorDrawable(placeHolderColor))
            .load(path)
            .override(resizeDiminution.width, resizeDiminution.height)
            .into(imageView)
    }

    override fun loadPhoto(
        context: Context,
        imageView: ImageView,
        resizeDiminution: PhotoDiminution,
        placeHolderColor: Int,
        path: String
    ) {
        Glide.with(imageView)
            .asBitmap()
            .placeholder(ColorDrawable(placeHolderColor))
            .load(path)
            .override(resizeDiminution.width, resizeDiminution.height)
            .into(imageView)
    }
}