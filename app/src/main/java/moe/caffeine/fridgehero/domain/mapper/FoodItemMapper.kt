package moe.caffeine.fridgehero.domain.mapper

import io.realm.kotlin.ext.toRealmList
import moe.caffeine.fridgehero.data.model.RealmFoodItem
import moe.caffeine.fridgehero.domain.model.FoodItem
import org.mongodb.kbson.BsonObjectId

fun RealmFoodItem.toDomainModel(): FoodItem =
  FoodItem(_id.toHexString(), name, brand, barcode, imageByteArray, expiryDates.toList())

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
  }
