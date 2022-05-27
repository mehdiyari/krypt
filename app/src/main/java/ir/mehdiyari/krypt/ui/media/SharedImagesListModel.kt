package ir.mehdiyari.krypt.ui.media

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class SharedImagesListModel(
    val images: List<Uri>? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Uri.CREATOR))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SharedImagesListModel> {
        override fun createFromParcel(parcel: Parcel): SharedImagesListModel {
            return SharedImagesListModel(parcel)
        }

        override fun newArray(size: Int): Array<SharedImagesListModel?> {
            return arrayOfNulls(size)
        }
    }
}