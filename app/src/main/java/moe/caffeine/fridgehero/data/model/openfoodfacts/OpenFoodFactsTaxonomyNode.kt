package moe.caffeine.fridgehero.data.model.openfoodfacts

import org.mongodb.kbson.BsonObjectId

data class OpenFoodFactsTaxonomyNode(
  val id: BsonObjectId = BsonObjectId(),
  val name: String = "",
  val parents: MutableMap<String, OpenFoodFactsTaxonomyNode> = mutableMapOf(),
  val children: MutableMap<String, OpenFoodFactsTaxonomyNode> = mutableMapOf(),
)
