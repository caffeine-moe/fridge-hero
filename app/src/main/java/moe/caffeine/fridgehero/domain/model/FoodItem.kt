package moe.caffeine.fridgehero.domain.model

import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.mongodb.kbson.BsonObjectId

data class FoodItem(
  val realmObjectId: String = "",
  val name: String = "",
  val brand: String = "",
  val barcode: String = "",
  val imageByteArray: ByteArray = byteArrayOf(),
  val expiryDates: List<Long> = listOf(),
  val categories: List<FoodCategory> = listOf()
) : Parcelable {
  val isRemoved: Boolean
    get() = expiryDates.isEmpty()

  val imageBitmap: ImageBitmap
    get() = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
      .asImageBitmap()

  //auto generated because of ByteArray
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FoodItem

    if (realmObjectId != other.realmObjectId) return false
    if (name != other.name) return false
    if (brand != other.brand) return false
    if (barcode != other.barcode) return false
    if (!imageByteArray.contentEquals(other.imageByteArray)) return false
    if (expiryDates != other.expiryDates) return false

    return true
  }

  override fun hashCode(): Int {
    var result = realmObjectId.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + brand.hashCode()
    result = 31 * result + barcode.hashCode()
    result = 31 * result + imageByteArray.contentHashCode()
    result = 31 * result + expiryDates.hashCode()
    return result
  }

  //parcelable implementation
  constructor(parcel: Parcel) : this(
    parcel.readString() ?: BsonObjectId().toHexString(),
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.createByteArray() ?: byteArrayOf(),
    parcel.createLongArray()?.toList() ?: listOf()
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(realmObjectId)
    parcel.writeString(name)
    parcel.writeString(brand)
    parcel.writeString(barcode)
    parcel.writeByteArray(imageByteArray)
    parcel.writeLongArray(expiryDates.toLongArray())
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<FoodItem> {
    override fun createFromParcel(parcel: Parcel): FoodItem {
      return FoodItem(parcel)
    }

    override fun newArray(size: Int): Array<FoodItem?> {
      return arrayOfNulls(size)
    }
  }
}
