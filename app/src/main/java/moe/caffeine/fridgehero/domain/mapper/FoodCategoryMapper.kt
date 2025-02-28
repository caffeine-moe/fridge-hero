package moe.caffeine.fridgehero.domain.mapper

import io.realm.kotlin.Realm
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.data.realm.fetchObjectById
import moe.caffeine.fridgehero.domain.model.FoodCategory
import org.mongodb.kbson.BsonObjectId

fun RealmFoodCategory.toDomainModel(
  mapped: MutableMap<BsonObjectId, FoodCategory> = mutableMapOf()
): FoodCategory {
  if (mapped.containsKey(_id)) return mapped[_id]!!

  val domainCategory = FoodCategory(
    realmObjectId = _id.toHexString(),
    name = name,
    parents = emptySet(),
    children = emptySet()
  )
  mapped[_id] = domainCategory

  val mappedParents = parents.map { it.toDomainModel(mapped) }.toSet()
  val mappedChildren = children.map { it.toDomainModel(mapped) }.toSet()

  return domainCategory.copy(
    parents = mappedParents,
    children = mappedChildren
  )
}

fun FoodCategory.toRealmModel(
  realm: Realm,
  mapped: MutableMap<String, RealmFoodCategory> = mutableMapOf()
): RealmFoodCategory {
  if (mapped.containsKey(realmObjectId)) return mapped[realmObjectId]!!

  val bsonId =
    realmObjectId.let { if (it.isNotBlank()) BsonObjectId.invoke(realmObjectId) else return RealmFoodCategory() }

  val realmCategory =
    realm.fetchObjectById<RealmFoodCategory>(bsonId).getOrNull() ?: return RealmFoodCategory()

  realmCategory.name = name

  mapped[realmObjectId] = realmCategory

  realmCategory.parents.clear()
  parents.forEach { domainParent ->
    val realmParent = domainParent.toRealmModel(realm, mapped)
    realmCategory.parents.add(realmParent)
  }

  return realmCategory
}
