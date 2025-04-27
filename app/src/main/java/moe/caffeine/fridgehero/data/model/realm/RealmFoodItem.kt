package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser
import moe.caffeine.fridgehero.data.realm.fetchAllByType
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import org.mongodb.kbson.BsonObjectId

class RealmFoodItem : RealmObject, MappableModel<FoodItem, RealmFoodItem> {
  @PrimaryKey
  override var realmObjectId: BsonObjectId = BsonObjectId()
  var isFromRecipe: Boolean = false

  var name: String = ""
  var brand: String = ""
  var barcode: String = ""
  var imageByteArray: ByteArray = byteArrayOf()
  var expiryDates: RealmList<Long> = realmListOf()
  var categoryNames: RealmSet<String> = realmSetOf()
  var novaGroup: Int = 0
  var nutriScore: String = ""
  var nutriments: RealmList<RealmNutrimentEntry> = realmListOf()

  @Ignore
  val categories: (Realm) -> Map<String, RealmFoodCategory>
    get() = { realm ->
      categoryNames.flatMap {
        realm.fetchAllByType<RealmFoodCategory>(
          "id == $0",
          OpenFoodFactsTaxonomyParser.normalisedIdFromName(it)
        )
      }.associateBy { it.name }
    }

  override fun toDomainModel() =
    FoodItem(
      realmObjectId.toHexString(),
      name,
      brand,
      barcode,
      imageByteArray,
      expiryDates.toList(),
      categoryNames.toList(),
      novaGroup = NovaGroup.entries.getOrNull(novaGroup) ?: NovaGroup.UNKNOWN,
      nutriScore = NutriScore.enumByLetter(nutriScore),
      nutriments = nutriments.associate {
        Nutriment.valueOf(it.nutriment) to it.value
      },
      isFromRecipe
    )

  override fun toRealmModel(): RealmFoodItem = this
}
