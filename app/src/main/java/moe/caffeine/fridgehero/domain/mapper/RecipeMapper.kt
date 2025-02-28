package moe.caffeine.fridgehero.domain.mapper

import io.realm.kotlin.ext.toRealmList
import moe.caffeine.fridgehero.data.model.realm.RealmRecipe
import moe.caffeine.fridgehero.domain.model.Recipe

fun RealmRecipe.toDomainModel(): Recipe =
  Recipe(_id, name, ingredientIds, instructions)

fun Recipe.toRealmModel(): RealmRecipe =
  RealmRecipe().apply {
    _id = this@toRealmModel.realmObjectId
    name = this@toRealmModel.name
    ingredientIds = this@toRealmModel.ingredientIds.toRealmList()
    instructions = this@toRealmModel.instructions
  }
