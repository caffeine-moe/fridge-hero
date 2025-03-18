package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.Recipe
import org.mongodb.kbson.BsonObjectId

class RealmRecipe : RealmObject, MappableModel<Recipe, RealmRecipe> {
  @PrimaryKey
  override var realmObjectId: BsonObjectId = BsonObjectId()

  var name: String = ""

  var ingredientIds: RealmList<BsonObjectId> = realmListOf()
  var instructions: String = ""

  override fun toDomainModel() =
    Recipe(realmObjectId.toHexString(), name, ingredientIds, instructions)

  override fun toRealmModel(): RealmRecipe = this
}
