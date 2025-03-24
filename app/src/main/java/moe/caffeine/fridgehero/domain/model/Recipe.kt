package moe.caffeine.fridgehero.domain.model

import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.realm.kotlin.ext.toRealmSet
import moe.caffeine.fridgehero.data.model.realm.RealmRecipe
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem

data class Recipe(
  override val realmId: String = "",
  val name: String = "",
  val ingredients: Set<FoodItem> = emptySet(),
  val imageByteArray: ByteArray = byteArrayOf(),
  val instructions: String = "",
) : DomainModel, MappableModel<Recipe, RealmRecipe>, Parcelable {

  val imageBitmap: ImageBitmap
    get() = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
      .asImageBitmap()

  constructor(parcel: Parcel) : this(
    parcel.readString().toString(),
    parcel.readString().toString(),
    parcel.createTypedArrayList(FoodItem)?.toSet() ?: setOf(),
    parcel.createByteArray() ?: byteArrayOf(),
    parcel.readString().toString()
  )

  override fun toDomainModel(): Recipe = this

  override fun toRealmModel() =
    RealmRecipe().apply {
      realmObjectId = this@Recipe.realmObjectId
      name = this@Recipe.name
      ingredients = this@Recipe.ingredients.map { it.toRealmModel() }.toRealmSet()
      instructions = this@Recipe.instructions
      imageByteArray = this@Recipe.imageByteArray
    }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Recipe

    if (realmId != other.realmId) return false
    if (name != other.name) return false
    if (ingredients != other.ingredients) return false
    if (!imageByteArray.contentEquals(other.imageByteArray)) return false
    if (instructions != other.instructions) return false

    return true
  }

  override fun hashCode(): Int {
    var result = realmId.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + ingredients.hashCode()
    result = 31 * result + imageByteArray.contentHashCode()
    result = 31 * result + instructions.hashCode()
    return result
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(realmId)
    parcel.writeString(name)
    parcel.writeTypedList(ingredients.toList())
    parcel.writeByteArray(imageByteArray)
    parcel.writeString(instructions)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<Recipe> {
    override fun createFromParcel(parcel: Parcel): Recipe {
      return Recipe(parcel)
    }

    override fun newArray(size: Int): Array<Recipe?> {
      return arrayOfNulls(size)
    }
  }
}
