package moe.caffeine.fridgehero.domain.model

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import org.mongodb.kbson.BsonObjectId

data class FoodItem(
  val realmObjectId: BsonObjectId = BsonObjectId(),
  val name: String = "",
  val brand: String = "",
  val barcode: String = "",
  val imageByteArray: ByteArray = byteArrayOf(),
  val expiryDates: List<Long> = emptyList(),
) {
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
}
