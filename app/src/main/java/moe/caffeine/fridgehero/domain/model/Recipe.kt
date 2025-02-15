package moe.caffeine.fridgehero.domain.model

import org.mongodb.kbson.BsonObjectId

data class Recipe(
  val realmObjectId: BsonObjectId = BsonObjectId(),

  val name: String = "",

  val ingredientIds: List<BsonObjectId> = emptyList(),
  val instructions: String = ""
)
