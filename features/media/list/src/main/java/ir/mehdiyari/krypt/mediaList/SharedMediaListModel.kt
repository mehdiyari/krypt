package ir.mehdiyari.krypt.mediaList

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

internal data class SharedMediaListModel(
    val images: List<Uri>? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(Uri.CREATOR))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SharedMediaListModel> {
        override fun createFromParcel(parcel: Parcel): SharedMediaListModel {
            return SharedMediaListModel(parcel)
        }

        override fun newArray(size: Int): Array<SharedMediaListModel?> {
            return arrayOfNulls(size)
        }
    }
}