package moe.caffeine.fridgehero.domain.model

data class FoodCategory(
  val realmObjectId: String = "",
  val name: String = "",
  val parents: Set<FoodCategory> = setOf(),
  val children: Set<FoodCategory> = setOf()
)
