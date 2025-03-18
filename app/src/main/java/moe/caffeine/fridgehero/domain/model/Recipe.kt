package moe.caffeine.fridgehero.domain.model

import io.realm.kotlin.ext.toRealmList
import moe.caffeine.fridgehero.data.model.realm.RealmRecipe
import moe.caffeine.fridgehero.domain.mapping.DomainModel
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import org.mongodb.kbson.BsonObjectId

data class Recipe(
  override val realmId: String = "",
  val name: String = "",
  val ingredientIds: List<BsonObjectId> = emptyList(),
  val instructions: String = "",
) : MappableModel<Recipe, RealmRecipe>, DomainModel {
  override fun toDomainModel(): Recipe = this

  override fun toRealmModel() =
    RealmRecipe().apply {
      realmObjectId = this@Recipe.realmObjectId
      name = this@Recipe.name
      ingredientIds = this@Recipe.ingredientIds.toRealmList()
      instructions = this@Recipe.instructions
    }
}
