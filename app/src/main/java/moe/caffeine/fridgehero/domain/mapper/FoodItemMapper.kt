package moe.caffeine.fridgehero.domain.mapper

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.ext.toRealmSet
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.model.realm.RealmNutrimentEntry
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import org.mongodb.kbson.BsonObjectId

fun RealmFoodItem.toDomainModel(): FoodItem =
  FoodItem(
    _id.toHexString(),
    name,
    brand,
    barcode,
    imageByteArray,
    expiryDates.toList(),
    categoryNames.toList(),
    novaGroup = NovaGroup.enumByNumber(novaGroup),
    nutriScore = NutriScore.enumByLetter(nutriScore),
    nutriments = nutriments.associate {
      Nutriment.valueOf(it.nutriment) to it.value
    }
  )

fun FoodItem.toRealmModel(): RealmFoodItem =
  RealmFoodItem().apply {
    _id =
      this@toRealmModel.realmObjectId.let {
        if (it.isBlank()) BsonObjectId() else BsonObjectId.invoke(
          it
        )
      }
    name = this@toRealmModel.name
    brand = this@toRealmModel.brand
    barcode = this@toRealmModel.barcode
    imageByteArray = this@toRealmModel.imageByteArray
    expiryDates = this@toRealmModel.expiryDates.toRealmList()
    categoryNames = this@toRealmModel.categories.toRealmSet()
    novaGroup = this@toRealmModel.novaGroup.number
    nutriScore = this@toRealmModel.nutriScore.letter
    nutriments.addAll(this@toRealmModel.nutriments.map {
      RealmNutrimentEntry().apply {
        nutriment = it.key.name
        value = it.value
      }
    })
    /*    categories = this@toRealmModel.categories.map {
          it.toRealmModel(realm)
        }.toRealmList()*/
  }
