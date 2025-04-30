package moe.caffeine.fridgehero.domain.model.fooditem

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.ext.toRealmSet
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.model.realm.RealmNutrimentEntry
import moe.caffeine.fridgehero.domain.helper.expiryImminent
import moe.caffeine.fridgehero.domain.helper.fuzzyMatch
import moe.caffeine.fridgehero.domain.helper.isExpired
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.DomainModel
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutrimentAvailability
import moe.caffeine.fridgehero.ui.component.item.resolveNovaGroupPainter
import moe.caffeine.fridgehero.ui.component.item.resolveNutriScorePainter
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
  val nutriments: Map<Nutriment, Double> = mapOf(),
  val isFromRecipe: Boolean = false,
  val nutrimentAvailability: NutrimentAvailability = NutrimentAvailability.UNAVALIABLE
) : DomainModel, MappableModel<FoodItem, RealmFoodItem>, Parcelable {

  val realExpiryDates: List<Long>
    get() = expiryDates.filterNot { it == -1L }

  val isRemoved: Boolean
    get() = expiryDates.isEmpty()

  val isSaved: Boolean
    get() = realmId.isNotBlank()

  val isExpired: Boolean
    get() = realExpiryDates.any { it.isExpired() }

  val expiresSoon: Boolean
    get() = realExpiryDates.any { it.expiryImminent() }

  val novaGroupPainter: @Composable () -> Painter = resolveNovaGroupPainter(novaGroup)

  val nutriScorePainter: @Composable () -> Painter = resolveNutriScorePainter(nutriScore)

  val hasNutritionalData: Boolean
    get() = nutrimentAvailability != NutrimentAvailability.UNAVALIABLE
            || (nutriments.isNotEmpty()
            && !nutriments.all { it.value == 0.0 })

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
    if (isFromRecipe != other.isFromRecipe) return false
    if (nutrimentAvailability != other.nutrimentAvailability) return false

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
    result = 31 * result + novaGroup.hashCode()
    result = 31 * result + nutriScore.hashCode()
    result = 31 * result + nutriments.hashCode()
    result = 31 * result + isFromRecipe.hashCode()
    result = 31 * result + nutrimentAvailability.hashCode()

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
    NovaGroup.entries.getOrNull(parcel.readInt()) ?: NovaGroup.UNKNOWN,
    NutriScore.enumByLetter(parcel.readString() ?: ""),
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(realmId)
    parcel.writeString(name)
    parcel.writeString(brand)
    parcel.writeString(barcode)
    parcel.writeByteArray(imageByteArray)
    parcel.writeLongArray(expiryDates.toLongArray())
    parcel.writeStringArray(categories.toTypedArray())
    parcel.writeInt(novaGroup.ordinal)
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
      novaGroup = this@FoodItem.novaGroup.ordinal
      nutriScore = this@FoodItem.nutriScore.letter
      nutriments.addAll(this@FoodItem.nutriments.map {
        RealmNutrimentEntry().apply {
          nutriment = it.key.name
          value = it.value
        }
      }
      )
      isFromRecipe = this@FoodItem.isFromRecipe
      nutrimentAvailability = this@FoodItem.nutrimentAvailability.ordinal
      /*    categories = this@toRealmModel.categories.map {
            it.toRealmModel(realm)
          }.toRealmList()*/
    }

}

fun FoodItem.matches(query: String): Boolean =
  query.isBlank() || (fuzzyMatch(this.name, query) ||
          this.categories.any {
            fuzzyMatch(it, query)
          })
