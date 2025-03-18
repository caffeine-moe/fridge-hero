package moe.caffeine.fridgehero.domain.model.fooditem

import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.ext.toRealmSet
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.model.realm.RealmNutrimentEntry
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.DomainModel
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import org.mongodb.kbson.BsonObjectId

data class FoodItem(
  override val realmId: String = "",
  val name: String = "",
  val brand: String = "",
  val barcode: String = "",
  val imageByteArray: ByteArray = byteArrayOf(),
  val expiryDates: List<Long> = listOf(),
  val categories: List<String> = listOf(),
  val novaGroup: NovaGroup = NovaGroup.UNKNOWN,
  val nutriScore: NutriScore = NutriScore.UNKNOWN,
  val nutriments: Map<Nutriment, String> = mapOf(),
) : Parcelable, MappableModel<FoodItem, RealmFoodItem>, DomainModel {

  val isRemoved: Boolean
    get() = expiryDates.isEmpty()

  val isSaved: Boolean
    get() = realmId.isNotBlank()

  val imageBitmap: ImageBitmap
    get() = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
      .asImageBitmap()

  val novaGroupVectorPainter: Painter
    @Composable
    get() = painterResource(
      when (novaGroup) {
        NovaGroup.UNPROCESSED -> R.drawable.nova_group_1
        NovaGroup.PROCESSED_INGREDIENTS -> R.drawable.nova_group_2
        NovaGroup.PROCESSED -> R.drawable.nova_group_3
        NovaGroup.ULTRA_PROCESSED -> R.drawable.nova_group_4
        else -> R.drawable.nova_group_unknown
      }
    )

  val nutriScoreVectorPainter: Painter
    @Composable
    get() = painterResource(
      when (nutriScore) {
        NutriScore.A -> R.drawable.nutriscore_a
        NutriScore.B -> R.drawable.nutriscore_b
        NutriScore.C -> R.drawable.nutriscore_c
        NutriScore.D -> R.drawable.nutriscore_d
        NutriScore.E -> R.drawable.nutriscore_e
        else -> R.drawable.nutriscore_unknown
      }
    )

  override fun toDomainModel() = this

  //auto generated because of ByteArray
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FoodItem

    if (realmId != other.realmId) return false
    if (name != other.name) return false
    if (brand != other.brand) return false
    if (barcode != other.barcode) return false
    if (!imageByteArray.contentEquals(other.imageByteArray)) return false
    if (expiryDates != other.expiryDates) return false
    if (categories != other.categories) return false
    if (nutriments != other.nutriments) return false
    if (novaGroup != other.novaGroup) return false
    if (nutriScore != other.nutriScore) return false

    return true
  }

  override fun hashCode(): Int {
    var result = realmId.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + brand.hashCode()
    result = 31 * result + barcode.hashCode()
    result = 31 * result + imageByteArray.contentHashCode()
    result = 31 * result + expiryDates.hashCode()
    result = 31 * result + categories.hashCode()
    return result
  }

  //parcelable implementation
  constructor(parcel: Parcel) : this(
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.readString() ?: "",
    parcel.createByteArray() ?: byteArrayOf(),
    parcel.createLongArray()?.toList() ?: listOf(),
    parcel.createStringArray()?.toList() ?: listOf(),
    novaGroup = NovaGroup.enumByNumber(parcel.readInt()),
    nutriScore = NutriScore.enumByLetter(parcel.readString() ?: "")
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(realmId)
    parcel.writeString(name)
    parcel.writeString(brand)
    parcel.writeString(barcode)
    parcel.writeByteArray(imageByteArray)
    parcel.writeLongArray(expiryDates.toLongArray())
    parcel.writeStringArray(categories.toTypedArray())
    parcel.writeInt(novaGroup.number)
    parcel.writeString(nutriScore.letter)
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

  override fun toRealmModel(): RealmFoodItem =
    RealmFoodItem().apply {
      realmObjectId =
        this@FoodItem.realmId.let {
          if (it.isBlank()) BsonObjectId() else BsonObjectId.invoke(
            it
          )
        }
      name = this@FoodItem.name
      brand = this@FoodItem.brand
      barcode = this@FoodItem.barcode
      imageByteArray = this@FoodItem.imageByteArray
      expiryDates = this@FoodItem.expiryDates.toRealmList()
      categoryNames = this@FoodItem.categories.toRealmSet()
      novaGroup = this@FoodItem.novaGroup.number
      nutriScore = this@FoodItem.nutriScore.letter
      nutriments.addAll(this@FoodItem.nutriments.map {
        RealmNutrimentEntry().apply {
          nutriment = it.key.name
          value = it.value
        }
      })
      /*    categories = this@toRealmModel.categories.map {
            it.toRealmModel(realm)
          }.toRealmList()*/
    }

}
