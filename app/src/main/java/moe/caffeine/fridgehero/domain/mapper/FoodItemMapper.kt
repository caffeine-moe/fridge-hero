package moe.caffeine.fridgehero.domain.mapper

import io.realm.kotlin.ext.toRealmList
import moe.caffeine.fridgehero.data.model.RealmFoodItem
import moe.caffeine.fridgehero.domain.model.FoodItem

fun RealmFoodItem.toDomainModel(): FoodItem =
  FoodItem(_id, name, brand, barcode, imageByteArray, expiryDates)

fun FoodItem.toRealmModel(): RealmFoodItem =
  RealmFoodItem().apply {
    _id = this@toRealmModel.realmObjectId
    name = this@toRealmModel.name
    brand = this@toRealmModel.brand
    barcode = this@toRealmModel.barcode
    imageByteArray = this@toRealmModel.imageByteArray
    expiryDates = this@toRealmModel.expiryDates.toRealmList()
  }
