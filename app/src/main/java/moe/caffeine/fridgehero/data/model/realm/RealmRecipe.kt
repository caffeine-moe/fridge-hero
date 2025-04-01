package moe.caffeine.fridgehero.data.model.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import moe.caffeine.fridgehero.data.realm.fetchObjectById
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.Recipe
import org.mongodb.kbson.BsonObjectId

class RealmRecipe : RealmObject, MappableModel<Recipe, RealmRecipe> {
  @PrimaryKey
  override var realmObjectId: BsonObjectId = BsonObjectId()

  var name: String = ""

  var ingredients: RealmSet<RealmFoodItem> = realmSetOf()
  var imageByteArray: ByteArray = byteArrayOf()
  var instructions: String = ""

  @Ignore
  val leftOver: (realm: Realm) -> RealmFoodItem = {
    it.fetchObjectById<RealmFoodItem>(realmObjectId).getOrNull() ?: RealmFoodItem().apply {
      this.isFromRecipe = true
      this.realmObjectId = this@RealmRecipe.realmObjectId
      this.name = this@RealmRecipe.name
    }
  }

  override fun toDomainModel() =
    Recipe(
      realmObjectId.toHexString(),
      name,
      ingredients.map { it.toDomainModel() }.toSet(),
      imageByteArray,
      instructions
    )

  override fun toRealmModel(): RealmRecipe = this
}
