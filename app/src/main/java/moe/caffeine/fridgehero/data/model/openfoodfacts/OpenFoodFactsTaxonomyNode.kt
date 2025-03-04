package moe.caffeine.fridgehero.data.model.openfoodfacts

import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser

data class OpenFoodFactsTaxonomyNode(
  val name: String = "",
  val parents: MutableMap<String, OpenFoodFactsTaxonomyNode> = mutableMapOf(),
  val children: MutableMap<String, OpenFoodFactsTaxonomyNode> = mutableMapOf(),
) {
  val id
    get() = OpenFoodFactsTaxonomyParser.normalisedIdFromName(name)
}
